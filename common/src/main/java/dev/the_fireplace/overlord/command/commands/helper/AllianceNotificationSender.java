package dev.the_fireplace.overlord.command.commands.helper;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mojang.authlib.GameProfile;
import dev.the_fireplace.lib.api.chat.injectables.MessageQueue;
import dev.the_fireplace.lib.api.chat.injectables.TranslatorFactory;
import dev.the_fireplace.lib.api.chat.interfaces.Translator;
import dev.the_fireplace.lib.api.player.injectables.GameProfileFinder;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.domain.data.PlayerAlliances;
import dev.the_fireplace.overlord.impl.data.AllianceUpdateMessageQueue;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

@Singleton
public final class AllianceNotificationSender
{
    public enum Notification
    {
        REQUESTED_ALLIANCE,
        DENIED_ALLIANCE,
        ACCEPTED_ALLIANCE,
        REMOVED_ALLIANCE,
        DECLARED_ENEMY,
        REMOVED_ENEMY,
    }
    private final Translator translator;
    private final MessageQueue messageQueue;
    private final PlayerAlliances playerAlliances;
    private final SharedAllianceHelpers sharedAllianceHelpers;
    private final AllianceUpdateMessageQueue allianceUpdateMessageQueue;
    private final GameProfileFinder gameProfileFinder;

    @Inject
    public AllianceNotificationSender(
        TranslatorFactory translatorFactory,
        MessageQueue messageQueue,
        PlayerAlliances playerAlliances,
        SharedAllianceHelpers sharedAllianceHelpers,
        AllianceUpdateMessageQueue allianceUpdateMessageQueue,
        GameProfileFinder gameProfileFinder
    ) {
        this.translator = translatorFactory.getTranslator(OverlordConstants.MODID);
        this.messageQueue = messageQueue;
        this.playerAlliances = playerAlliances;
        this.sharedAllianceHelpers = sharedAllianceHelpers;
        this.allianceUpdateMessageQueue = allianceUpdateMessageQueue;
        this.gameProfileFinder = gameProfileFinder;
    }

    public void sendOrQueueNotification(
        Notification notification,
        UUID notifyPlayerId,
        @Nullable ServerPlayer notifyPlayer,
        UUID senderId,
        @Nullable String senderName
    ) {
        if (notifyPlayer == null) {
            this.allianceUpdateMessageQueue.queueNotification(notifyPlayerId, senderId, notification);
            return;
        }
        if (senderName == null) {
            Optional<GameProfile> senderGameProfile = gameProfileFinder.findProfile(senderId);
            if (senderGameProfile.isPresent()) {
                senderName = senderGameProfile.get().getName();
            } else {
                return;
            }
        }
        MutableComponent message = Component.empty();
        switch (notification) {
            case REQUESTED_ALLIANCE:
                message = translator.getTextForTarget(notifyPlayerId, "commands.overlord.ally.add.notification", senderName);
                if (!playerAlliances.hasDeclaredEnemy(notifyPlayerId, senderId)) {
                    message.append(Component.literal(" ")
                        .append(sharedAllianceHelpers.getAcceptAllianceButton(notifyPlayerId, senderName)
                            .append(Component.literal(" ")
                                .append(sharedAllianceHelpers.getDenyAllianceButton(notifyPlayerId, senderName))
                            )
                        )
                    );
                }
                break;
            case DENIED_ALLIANCE:
                message = translator.getTextForTarget(notifyPlayerId, "commands.overlord.ally.deny.notification", senderName);
                break;
            case ACCEPTED_ALLIANCE:
                message = translator.getTextForTarget(notifyPlayerId, "commands.overlord.ally.accept.notification", senderName);
                break;
            case REMOVED_ALLIANCE:
                message = translator.getTextForTarget(notifyPlayerId, "commands.overlord.ally.remove.notification", senderName);
                break;
            case DECLARED_ENEMY:
                message = translator.getTextForTarget(notifyPlayerId, "commands.overlord.enemy.add.notification", senderName);
                if (!playerAlliances.hasDeclaredEnemy(notifyPlayerId, senderId)) {
                    message.append(Component.literal(" ")
                        .append(sharedAllianceHelpers.getAddEnemyButton(notifyPlayerId, senderName))
                    );
                }
                break;
            case REMOVED_ENEMY:
                message = translator.getTextForTarget(notifyPlayerId, "commands.overlord.enemy.remove.notification", senderName);
                if (playerAlliances.hasDeclaredEnemy(notifyPlayerId, senderId)) {
                    message.append(Component.literal(" ")
                        .append(sharedAllianceHelpers.getRemoveEnemyButton(notifyPlayerId, senderName))
                    );
                }
                break;
        }
        messageQueue.queueMessages(notifyPlayer, message);
    }
}
