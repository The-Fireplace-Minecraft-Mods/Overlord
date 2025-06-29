package dev.the_fireplace.overlord.command.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import dev.the_fireplace.lib.api.chat.injectables.MessageQueue;
import dev.the_fireplace.lib.api.chat.injectables.TextPaginator;
import dev.the_fireplace.lib.api.chat.injectables.TextStyles;
import dev.the_fireplace.lib.api.chat.injectables.TranslatorFactory;
import dev.the_fireplace.lib.api.chat.interfaces.Translator;
import dev.the_fireplace.lib.api.command.injectables.ArgumentTypeFactory;
import dev.the_fireplace.lib.api.command.injectables.FeedbackSenderFactory;
import dev.the_fireplace.lib.api.command.injectables.Requirements;
import dev.the_fireplace.lib.api.command.interfaces.FeedbackSender;
import dev.the_fireplace.lib.api.command.interfaces.PossiblyOfflinePlayer;
import dev.the_fireplace.lib.api.command.interfaces.RegisterableCommand;
import dev.the_fireplace.lib.api.player.injectables.GameProfileFinder;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.domain.data.PlayerAlliances;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Singleton
public final class EnemyCommand implements RegisterableCommand {
    private final FeedbackSender feedbackSender;
    private final Requirements requirements;
    private final ArgumentTypeFactory argumentTypeFactory;
    private final PlayerAlliances playerAlliances;
    private final Translator translator;
    private final MessageQueue messageQueue;
    private final TextStyles textStyles;
    private final TextPaginator textPaginator;
    private final GameProfileFinder gameProfileFinder;

    @Inject
    public EnemyCommand(
        Requirements requirements,
        TranslatorFactory translatorFactory,
        FeedbackSenderFactory feedbackSenderFactory,
        ArgumentTypeFactory argumentTypeFactory,
        PlayerAlliances playerAlliances,
        MessageQueue messageQueue,
        TextStyles textStyles,
        TextPaginator textPaginator,
        GameProfileFinder gameProfileFinder
    ) {
        this.translator = translatorFactory.getTranslator(OverlordConstants.MODID);
        this.feedbackSender = feedbackSenderFactory.get(this.translator);
        this.requirements = requirements;
        this.argumentTypeFactory = argumentTypeFactory;
        this.playerAlliances = playerAlliances;
        this.messageQueue = messageQueue;
        this.textStyles = textStyles;
        this.textPaginator = textPaginator;
        this.gameProfileFinder = gameProfileFinder;
    }


    @Override
    public CommandNode<CommandSourceStack> register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> enemyCommand = Commands.literal("enemy")
            .requires(requirements::player);

        enemyCommand.then(
            Commands.literal("add").then(
                Commands.argument("player", argumentTypeFactory.possiblyOfflinePlayer())
                    .suggests(argumentTypeFactory::listOfflinePlayerSuggestions)//TODO filter out current relations
                    .executes(this::executeAddEnemy)
            )
        );

        enemyCommand.then(
            Commands.literal("list").executes((command) -> executeListEnemies(command, 1))
                .then(Commands.argument("page", IntegerArgumentType.integer(1))
                    .executes((command) -> executeListEnemies(command, command.getArgument("page", Integer.class)))
                )
        );

        enemyCommand.then(
            Commands.literal("remove").then(
                Commands.argument("player", argumentTypeFactory.possiblyOfflinePlayer())
                    .suggests(argumentTypeFactory::listOfflinePlayerSuggestions)//TODO filter to only current enemies
                    .executes(this::executeRemoveEnemy)
            )
        );

        return commandDispatcher.register(enemyCommand);
    }

    private int executeAddEnemy(CommandContext<CommandSourceStack> command) throws CommandSyntaxException {
        PossiblyOfflinePlayer targetPlayer = argumentTypeFactory.getPossiblyOfflinePlayer(command, "player");
        ServerPlayer sender = command.getSource().getPlayerOrException();
        UUID senderId = sender.getUUID();
        UUID targetPlayerId = targetPlayer.getId();
        if (playerAlliances.hasAllianceWith(senderId, targetPlayerId)) {
            MutableComponent message = translator.getTextForTarget(senderId, "commands.overlord.enemy.add.failure.ally", targetPlayer.getName());
            message.append(Component.literal(" ")
                .append(getRemoveAllyButton(senderId, targetPlayer.getName()))
            );
            messageQueue.queueMessages(sender, message);
        } else if (playerAlliances.hasDeclaredEnemy(senderId, targetPlayerId)) {
            feedbackSender.basic(command, "commands.overlord.enemy.add.failure.already_enemy", targetPlayer.getName());
        } else {
            playerAlliances.declareEnemy(senderId, targetPlayerId);
            feedbackSender.basic(command, "commands.overlord.enemy.add.success", targetPlayer.getName());
            if (targetPlayer.entity() != null) {
                MutableComponent message = translator.getTextForTarget(targetPlayerId, "commands.overlord.enemy.add.notification", sender.getName());
                if (!playerAlliances.hasDeclaredEnemy(targetPlayerId, senderId)) {
                    message.append(Component.literal(" ")
                        .append(getAddEnemyButton(targetPlayerId, sender.getGameProfile().getName()))
                    );
                }
                messageQueue.queueMessages(targetPlayer.entity(), message);
            } else {
                //TODO queue notification for later
            }
        }

        return Command.SINGLE_SUCCESS;
    }

    private MutableComponent getRemoveAllyButton(UUID targetPlayerId, String allyName) {
        ClickEvent removeAlly = new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/ally remove %s", allyName));

        return translator.getTextForTarget(targetPlayerId, "commands.overlord.ally.list.remove_ally")
            .setStyle(textStyles.aqua().withClickEvent(removeAlly));
    }

    private MutableComponent getAddEnemyButton(UUID targetPlayerId, String enemyName) {
        ClickEvent addEnemy = new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/enemy add %s", enemyName));

        return translator.getTextForTarget(targetPlayerId, "commands.overlord.enemy.list.add_enemy")
            .setStyle(textStyles.aqua().withClickEvent(addEnemy));
    }

    private int executeListEnemies(CommandContext<CommandSourceStack> command, int page) throws CommandSyntaxException {
        ServerPlayer sender = command.getSource().getPlayerOrException();
        UUID senderId = sender.getUUID();
        List<UUID> declaredEnemies = playerAlliances.getEnemiesDeclaredBy(senderId).toList();
        List<UUID> playersWhoHaveDeclaredEnemy = playerAlliances.getPlayersWhoHaveDeclaredEnemy(senderId).toList();
        if (declaredEnemies.isEmpty() && playersWhoHaveDeclaredEnemy.isEmpty()) {
            feedbackSender.basic(command, "commands.overlord.enemy.list.no_data");
            return Command.SINGLE_SUCCESS;
        }
        List<UUID> mutualEnemies = declaredEnemies.stream()
            .filter(playersWhoHaveDeclaredEnemy::contains)
            .toList();
        Stream<UUID> declaredNonmutualEnemies = declaredEnemies.stream()
            .filter(player -> !mutualEnemies.contains(player));
        Stream<UUID> playersWhoHaveDeclaredSenderAsEnemy = playersWhoHaveDeclaredEnemy.stream()
            .filter(declaredEnemies::contains);
        List<Component> enemyListEntries = new ArrayList<>();
        for (UUID enemyId : mutualEnemies) {
            Optional<GameProfile> enemyProfile = gameProfileFinder.findProfile(enemyId);
            enemyProfile.ifPresent(gameProfile -> {
                MutableComponent mutualEnemyComponent = translator.getTextForTarget(senderId, "commands.overlord.enemy.list.mutual", gameProfile.getName());
                mutualEnemyComponent.append(Component.literal(" ")
                    .append(getRemoveEnemyButton(senderId, gameProfile.getName()))
                );
                enemyListEntries.add(mutualEnemyComponent);
            });
        }
        declaredNonmutualEnemies.forEach(enemyId -> {
            Optional<GameProfile> enemyProfile = gameProfileFinder.findProfile(enemyId);
            enemyProfile.ifPresent(gameProfile -> {
                MutableComponent mutualEnemyComponent = translator.getTextForTarget(senderId, "commands.overlord.enemy.list.outbound", gameProfile.getName());
                mutualEnemyComponent.append(Component.literal(" ")
                    .append(getRemoveEnemyButton(senderId, gameProfile.getName()))
                );
                enemyListEntries.add(mutualEnemyComponent);
            });
        });
        playersWhoHaveDeclaredSenderAsEnemy.forEach(enemyId -> {
            Optional<GameProfile> enemyProfile = gameProfileFinder.findProfile(enemyId);
            enemyProfile.ifPresent(gameProfile -> {
                MutableComponent mutualEnemyComponent = translator.getTextForTarget(senderId, "commands.overlord.enemy.list.inbound", gameProfile.getName());
                mutualEnemyComponent.append(Component.literal(" ")
                    .append(getAddEnemyButton(senderId, gameProfile.getName()))
                );
                enemyListEntries.add(mutualEnemyComponent);
            });
        });

        this.textPaginator.sendPaginatedChat(
            command.getSource(),
            "/enemy list %s",
            enemyListEntries,
            page
        );

        return Command.SINGLE_SUCCESS;
    }

    private MutableComponent getRemoveEnemyButton(UUID targetPlayerId, String enemyName) {
        ClickEvent removeEnemy = new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/enemy remove %s", enemyName));

        return translator.getTextForTarget(targetPlayerId, "commands.overlord.enemy.list.remove_enemy")
            .setStyle(textStyles.aqua().withClickEvent(removeEnemy));
    }

    private int executeRemoveEnemy(CommandContext<CommandSourceStack> command) throws CommandSyntaxException {
        PossiblyOfflinePlayer targetPlayer = argumentTypeFactory.getPossiblyOfflinePlayer(command, "player");
        ServerPlayer sender = command.getSource().getPlayerOrException();
        UUID senderId = sender.getUUID();
        UUID targetPlayerId = targetPlayer.getId();
        if (!playerAlliances.hasDeclaredEnemy(senderId, targetPlayerId)) {
            feedbackSender.basic(command, "commands.overlord.enemy.remove.failure", targetPlayer.getName());
        } else {
            playerAlliances.removeEnemy(senderId, targetPlayerId);
            feedbackSender.basic(command, "commands.overlord.enemy.remove.success", targetPlayer.getName());
            if (targetPlayer.entity() != null) {
                MutableComponent message = translator.getTextForTarget(targetPlayerId, "commands.overlord.enemy.remove.notification", sender.getName());
                if (playerAlliances.hasDeclaredEnemy(targetPlayerId, senderId)) {
                    message.append(Component.literal(" ")
                        .append(getRemoveEnemyButton(targetPlayerId, sender.getGameProfile().getName()))
                    );
                }
                messageQueue.queueMessages(targetPlayer.entity(), message);
            } else {
                //TODO queue notification for later
            }
        }

        return Command.SINGLE_SUCCESS;
    }
}