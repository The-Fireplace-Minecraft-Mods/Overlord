package dev.the_fireplace.overlord.command;

import com.google.inject.Inject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.the_fireplace.lib.api.command.injectables.HelpCommandFactory;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.command.commands.AllyCommand;
import dev.the_fireplace.overlord.command.commands.EnemyCommand;
import net.minecraft.commands.CommandSourceStack;

public final class RegisterCommands
{
    private final HelpCommandFactory helpCommandFactory;
    private final AllyCommand allyCommand;
    private final EnemyCommand enemyCommand;

    @Inject
    public RegisterCommands(
        HelpCommandFactory helpCommandFactory,
        AllyCommand allyCommand,
        EnemyCommand enemyCommand
    ) {
        this.helpCommandFactory = helpCommandFactory;
        this.allyCommand = allyCommand;
        this.enemyCommand = enemyCommand;
    }

    public void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        helpCommandFactory.create(
            OverlordConstants.MODID,
            LiteralArgumentBuilder.literal("overlordhelp")
        ).addCommands(
            allyCommand.register(commandDispatcher),
            enemyCommand.register(commandDispatcher)
        ).register(commandDispatcher);
    }
}
