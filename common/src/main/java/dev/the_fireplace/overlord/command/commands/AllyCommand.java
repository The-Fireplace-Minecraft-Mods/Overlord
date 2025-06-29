package dev.the_fireplace.overlord.command.commands;

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
import dev.the_fireplace.overlord.command.commands.helper.SharedAllianceHelpers;
import dev.the_fireplace.overlord.domain.data.PlayerAlliances;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;
import java.util.stream.Stream;

@Singleton
public final class AllyCommand implements RegisterableCommand
{
    private final FeedbackSender feedbackSender;
    private final Requirements requirements;
    private final ArgumentTypeFactory argumentTypeFactory;
    private final PlayerAlliances playerAlliances;
    private final Translator translator;
    private final MessageQueue messageQueue;
    private final TextStyles textStyles;
    private final TextPaginator textPaginator;
    private final GameProfileFinder gameProfileFinder;
    private final SharedAllianceHelpers sharedAllianceHelpers;

    @Inject
    public AllyCommand(
        Requirements requirements,
        TranslatorFactory translatorFactory,
        FeedbackSenderFactory feedbackSenderFactory,
        ArgumentTypeFactory argumentTypeFactory,
        PlayerAlliances playerAlliances,
        MessageQueue messageQueue,
        TextStyles textStyles,
        TextPaginator textPaginator,
        GameProfileFinder gameProfileFinder,
        SharedAllianceHelpers sharedAllianceHelpers
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
        this.sharedAllianceHelpers = sharedAllianceHelpers;
    }


    @Override
    public CommandNode<CommandSourceStack> register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> allyCommand = Commands.literal("ally")
            .requires(requirements::player);

        allyCommand.then(
            Commands.literal("add").then(
                Commands.argument("player", argumentTypeFactory.possiblyOfflinePlayer())
                    .suggests(this.sharedAllianceHelpers::suggestUnalignedOnlinePlayers)
                    .executes(this::executeAddAlly)
            )
        );

        allyCommand.then(
            Commands.literal("accept").then(
                Commands.argument("player", argumentTypeFactory.possiblyOfflinePlayer())
                    .suggests(((context, builder) ->
                        this.sharedAllianceHelpers.suggestPlayersFromList(context, builder, playerAlliances::getPlayersWhoHaveRequestedAllianceWith)
                    ))
                    .executes(this::executeAcceptAlly)
            )
        );

        allyCommand.then(
            Commands.literal("deny").then(
                Commands.argument("player", argumentTypeFactory.possiblyOfflinePlayer())
                    .suggests(((context, builder) ->
                        this.sharedAllianceHelpers.suggestPlayersFromList(context, builder, playerAlliances::getPlayersWhoHaveRequestedAllianceWith)
                    ))
                    .executes(this::executeDenyAlly)
            )
        );

        allyCommand.then(
            Commands.literal("list").executes((command) -> executeListAllies(command, 1))
                .then(Commands.argument("page", IntegerArgumentType.integer(1))
                    .executes((command) -> executeListAllies(command, command.getArgument("page", Integer.class)))
                )
        );

        allyCommand.then(
            Commands.literal("remove").then(
                Commands.argument("player", argumentTypeFactory.possiblyOfflinePlayer())
                    .suggests(((context, builder) ->
                        this.sharedAllianceHelpers.suggestPlayersFromList(context, builder, (playerId) -> Stream.concat(
                            playerAlliances.getConfirmedAlliancesWith(playerId),
                            playerAlliances.getAlliesRequestedBy(playerId)
                        ))
                    ))
                    .executes(this::executeRemoveAlly)
            )
        );

        return commandDispatcher.register(allyCommand);
    }

    private int executeAddAlly(CommandContext<CommandSourceStack> command) throws CommandSyntaxException {
        PossiblyOfflinePlayer targetPlayer = argumentTypeFactory.getPossiblyOfflinePlayer(command, "player");
        ServerPlayer sender = command.getSource().getPlayerOrException();
        UUID senderId = sender.getUUID();
        UUID targetPlayerId = targetPlayer.getId();
        if (playerAlliances.hasAllianceWith(senderId, targetPlayerId)) {
            feedbackSender.basic(command, "commands.overlord.ally.add.failure.already_allied", targetPlayer.getName());
        } else if (playerAlliances.hasRequestedAllianceWith(senderId, targetPlayerId)) {
            feedbackSender.basic(command, "commands.overlord.ally.add.failure.already_requested", targetPlayer.getName());
        } else if (playerAlliances.hasDeclaredEnemy(senderId, targetPlayerId)) {
            MutableComponent message = translator.getTextForTarget(senderId, "commands.overlord.ally.add.failure.enemy", targetPlayer.getName());
            message.append(Component.literal(" ")
                .append(sharedAllianceHelpers.getRemoveEnemyButton(senderId, targetPlayer.getName()))
            );
            messageQueue.queueMessages(sender, message);
        } else if (playerAlliances.hasRequestedAllianceWith(targetPlayerId, senderId)) {
            this.executeAcceptAlly(command);
        } else {
            playerAlliances.requestAlliance(senderId, targetPlayerId);
            feedbackSender.basic(command, "commands.overlord.ally.add.success", targetPlayer.getName());
            if (targetPlayer.entity() != null) {
                MutableComponent message = translator.getTextForTarget(targetPlayerId, "commands.overlord.ally.add.notification", sender.getName());
                if (!playerAlliances.hasDeclaredEnemy(targetPlayerId, senderId)) {
                    message.append(Component.literal(" ")
                        .append(getAcceptAllianceButton(targetPlayerId, sender.getGameProfile().getName())
                            .append(Component.literal(" ")
                                .append(getDenyAllianceButton(targetPlayerId, sender.getGameProfile().getName()))
                            )
                        )
                    );
                }
                messageQueue.queueMessages(targetPlayer.entity(), message);
            } else {
                //TODO queue notification for later
            }
        }

        return Command.SINGLE_SUCCESS;
    }

    private MutableComponent getAcceptAllianceButton(UUID targetPlayerId, String allyName) {
        ClickEvent removeEnemy = new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/ally accept %s", allyName));

        return translator.getTextForTarget(targetPlayerId, "commands.overlord.ally.add.accept")
            .setStyle(textStyles.aqua().withClickEvent(removeEnemy));
    }

    private MutableComponent getDenyAllianceButton(UUID targetPlayerId, String allyName) {
        ClickEvent removeEnemy = new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/ally deny %s", allyName));

        return translator.getTextForTarget(targetPlayerId, "commands.overlord.ally.add.deny")
            .setStyle(textStyles.aqua().withClickEvent(removeEnemy));
    }

    private int executeAcceptAlly(CommandContext<CommandSourceStack> command) throws CommandSyntaxException {
        PossiblyOfflinePlayer targetPlayer = argumentTypeFactory.getPossiblyOfflinePlayer(command, "player");
        ServerPlayer sender = command.getSource().getPlayerOrException();
        UUID senderId = sender.getUUID();
        UUID targetPlayerId = targetPlayer.getId();
        if (!playerAlliances.hasRequestedAllianceWith(targetPlayerId, senderId)) {
            feedbackSender.basic(command, "commands.overlord.ally.response_failure", targetPlayer.getName());
        } else {
            playerAlliances.requestAlliance(senderId, targetPlayerId);
            feedbackSender.basic(command, "commands.overlord.ally.accept.success", targetPlayer.getName());
            if (targetPlayer.entity() != null) {
                MutableComponent message = translator.getTextForTarget(targetPlayerId, "commands.overlord.ally.accept.notification", sender.getName());
                messageQueue.queueMessages(targetPlayer.entity(), message);
            } else {
                //TODO queue notification for later
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    private int executeDenyAlly(CommandContext<CommandSourceStack> command) throws CommandSyntaxException {
        PossiblyOfflinePlayer targetPlayer = argumentTypeFactory.getPossiblyOfflinePlayer(command, "player");
        ServerPlayer sender = command.getSource().getPlayerOrException();
        UUID senderId = sender.getUUID();
        UUID targetPlayerId = targetPlayer.getId();
        if (!playerAlliances.hasRequestedAllianceWith(targetPlayerId, senderId)) {
            feedbackSender.basic(command, "commands.overlord.ally.response_failure", targetPlayer.getName());
        } else {
            playerAlliances.denyAlliance(senderId, targetPlayerId);
            feedbackSender.basic(command, "commands.overlord.ally.deny.success", targetPlayer.getName());
            if (targetPlayer.entity() != null) {
                MutableComponent message = translator.getTextForTarget(targetPlayerId, "commands.overlord.ally.deny.notification", sender.getName());
                messageQueue.queueMessages(targetPlayer.entity(), message);
            } else {
                //TODO queue notification for later
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    private int executeListAllies(CommandContext<CommandSourceStack> command, int page) throws CommandSyntaxException {
        ServerPlayer sender = command.getSource().getPlayerOrException();
        UUID senderId = sender.getUUID();
        Stream<UUID> requestedAllies = playerAlliances.getAlliesRequestedBy(senderId);
        Stream<UUID> confirmedAllies = playerAlliances.getConfirmedAlliancesWith(senderId);
        Stream<UUID> incomingAllianceRequests = playerAlliances.getPlayersWhoHaveRequestedAllianceWith(senderId);
        List<Component> allyListEntries = new ArrayList<>();
        incomingAllianceRequests.forEach(incomingAllianceRequest -> {
            gameProfileFinder.findProfile(incomingAllianceRequest).ifPresent(requesterProfile -> {
                MutableComponent incomingAllianceRequestComponent = translator.getTextForTarget(senderId, "commands.overlord.ally.list.pending.inbound", requesterProfile.getName());
                incomingAllianceRequestComponent.append(Component.literal(" ")
                    .append(getAcceptAllianceButton(senderId, requesterProfile.getName())
                        .append(Component.literal(" ")
                            .append(getDenyAllianceButton(senderId, requesterProfile.getName()))
                        )
                    )
                );
                allyListEntries.add(incomingAllianceRequestComponent);
            });
        });
        confirmedAllies.forEach(confirmedAlly -> {
            gameProfileFinder.findProfile(confirmedAlly).ifPresent(confirmedAllyProfile -> {
                MutableComponent confirmedAllyComponent = translator.getTextForTarget(senderId, "commands.overlord.ally.list.allied", confirmedAllyProfile.getName());
                confirmedAllyComponent.append(Component.literal(" ")
                    .append(sharedAllianceHelpers.getRemoveAllyButton(senderId, confirmedAllyProfile.getName()))
                );
                allyListEntries.add(confirmedAllyComponent);
            });
        });
        requestedAllies.forEach(requestedAlly -> {
            gameProfileFinder.findProfile(requestedAlly).ifPresent(requestedAllyProfile -> {
                MutableComponent requestedAllyComponent = translator.getTextForTarget(senderId, "commands.overlord.ally.list.pending.outbound", requestedAllyProfile.getName());
                requestedAllyComponent.append(Component.literal(" ")
                    .append(sharedAllianceHelpers.getRemoveAllyButton(senderId, requestedAllyProfile.getName()))
                );
                allyListEntries.add(requestedAllyComponent);
            });
        });

        if (allyListEntries.isEmpty()) {
            feedbackSender.basic(command, "commands.overlord.ally.list.no_data");
        } else {
            this.textPaginator.sendPaginatedChat(
                command.getSource(),
                "/ally list %s",
                allyListEntries,
                page
            );
        }

        return Command.SINGLE_SUCCESS;
    }

    private int executeRemoveAlly(CommandContext<CommandSourceStack> command) throws CommandSyntaxException {
        PossiblyOfflinePlayer targetPlayer = argumentTypeFactory.getPossiblyOfflinePlayer(command, "player");
        ServerPlayer sender = command.getSource().getPlayerOrException();
        UUID senderId = sender.getUUID();
        UUID targetPlayerId = targetPlayer.getId();
        boolean hasConfirmedAlliance = playerAlliances.hasAllianceWith(senderId, targetPlayerId);
        boolean hasRequestedAlliance = playerAlliances.hasRequestedAllianceWith(senderId, targetPlayerId);
        if (hasConfirmedAlliance) {
            playerAlliances.removeAlliance(senderId, targetPlayerId);
            feedbackSender.basic(command, "commands.overlord.ally.remove.success.ally", targetPlayer.getName());
            if (targetPlayer.entity() != null) {
                MutableComponent message = translator.getTextForTarget(targetPlayerId, "commands.overlord.ally.remove.notification", sender.getName());
                messageQueue.queueMessages(targetPlayer.entity(), message);
            } else {
                //TODO queue notification for later
            }
        } else if (hasRequestedAlliance) {
            playerAlliances.removeAlliance(senderId, targetPlayerId);
            feedbackSender.basic(command, "commands.overlord.ally.remove.success.pending", targetPlayer.getName());
        } else {
            feedbackSender.basic(command, "commands.overlord.ally.remove.failure", targetPlayer.getName());
        }
        return Command.SINGLE_SUCCESS;
    }
}