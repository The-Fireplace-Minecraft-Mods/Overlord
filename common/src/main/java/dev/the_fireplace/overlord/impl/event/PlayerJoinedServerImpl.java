package dev.the_fireplace.overlord.impl.event;

import com.google.inject.Inject;
import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.command.commands.helper.AllianceNotificationSender;
import dev.the_fireplace.overlord.domain.event.PlayerJoinedServer;
import dev.the_fireplace.overlord.impl.data.AllianceUpdateMessageQueue;
import net.minecraft.server.level.ServerPlayer;

@Implementation
public final class PlayerJoinedServerImpl implements PlayerJoinedServer
{
    private final AllianceNotificationSender allianceNotificationSender;
    private final AllianceUpdateMessageQueue allianceUpdateMessageQueue;

    @Inject
    public PlayerJoinedServerImpl(
        AllianceNotificationSender allianceNotificationSender,
        AllianceUpdateMessageQueue allianceUpdateMessageQueue
    ) {
        this.allianceNotificationSender = allianceNotificationSender;
        this.allianceUpdateMessageQueue = allianceUpdateMessageQueue;
    }

    @Override
    public void onPlayerJoinedServer(ServerPlayer player) {
        this.allianceUpdateMessageQueue.popQueue(player.getUUID()).forEach(
            (senderId, value) -> this.allianceNotificationSender.sendOrQueueNotification(
                value,
                player.getUUID(),
                player,
                senderId,
                null
            )
        );
    }
}
