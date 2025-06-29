package dev.the_fireplace.overlord.command.commands.helper;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.the_fireplace.lib.api.chat.injectables.TextStyles;
import dev.the_fireplace.lib.api.chat.injectables.TranslatorFactory;
import dev.the_fireplace.lib.api.chat.interfaces.Translator;
import dev.the_fireplace.lib.api.player.injectables.GameProfileFinder;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.domain.data.PlayerAlliances;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.MutableComponent;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;

@Singleton
public final class SharedAllianceHelpers
{
    private final PlayerAlliances playerAlliances;
    private final GameProfileFinder gameProfileFinder;
    private final Translator translator;
    private final TextStyles textStyles;

    @Inject
    public SharedAllianceHelpers(
        TranslatorFactory translatorFactory,
        PlayerAlliances playerAlliances,
        GameProfileFinder gameProfileFinder,
        TextStyles textStyles
    ) {
        this.translator = translatorFactory.getTranslator(OverlordConstants.MODID);
        this.playerAlliances = playerAlliances;
        this.gameProfileFinder = gameProfileFinder;
        this.textStyles = textStyles;
    }

    public <S> CompletableFuture<Suggestions> suggestUnalignedOnlinePlayers(CommandContext<S> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        if (context.getSource() instanceof SharedSuggestionProvider commandSource) {
            StringReader reader = new StringReader(builder.getInput());
            reader.setCursor(builder.getStart());
            EntitySelectorParser entitySelectorReader = new EntitySelectorParser(reader);

            try {
                entitySelectorReader.parse();
            } catch (CommandSyntaxException ignored) {
            }

            if (context.getSource() instanceof CommandSourceStack commandSourceStack) {
                UUID playerId = commandSourceStack.getPlayerOrException().getUUID();
                return entitySelectorReader.fillSuggestions(builder, (suggestionsBuilder) -> {
                    Collection<String> onlinePlayerNames = commandSource.getOnlinePlayerNames();
                    Collection<String> acceptableAllies = onlinePlayerNames.stream()
                        .filter(playerName ->
                            gameProfileFinder.findProfile(playerName)
                                .flatMap(gameProfile -> Optional.of(
                                    !playerAlliances.hasDeclaredEnemy(playerId, gameProfile.getId())
                                        && !playerAlliances.hasAllianceWith(playerId, gameProfile.getId())
                                        && !playerAlliances.hasRequestedAllianceWith(playerId, gameProfile.getId())
                                ))
                                .orElse(false)
                        ).toList();
                    SharedSuggestionProvider.suggest(acceptableAllies, suggestionsBuilder);
                });
            } else {
                return Suggestions.empty();
            }
        } else {
            return Suggestions.empty();
        }
    }

    public <S> CompletableFuture<Suggestions> suggestPlayersFromList(
        CommandContext<S> context,
        SuggestionsBuilder builder,
        Function<UUID, Stream<UUID>> getPlayerList
    ) throws CommandSyntaxException {
        StringReader reader = new StringReader(builder.getInput());
        reader.setCursor(builder.getStart());
        EntitySelectorParser entitySelectorReader = new EntitySelectorParser(reader);

        try {
            entitySelectorReader.parse();
        } catch (CommandSyntaxException ignored) {
        }

        if (context.getSource() instanceof CommandSourceStack commandSourceStack) {
            UUID sourcePlayerId = commandSourceStack.getPlayerOrException().getUUID();

            return entitySelectorReader.fillSuggestions(builder, (suggestionsBuilder) -> {
                Collection<String> acceptableAllies = getPlayerList.apply(sourcePlayerId)
                    .map(listedPlayerId ->
                        gameProfileFinder.findProfile(listedPlayerId)
                            .flatMap(gameProfile -> Optional.of(
                                gameProfile.getName()
                            ))
                            .orElse("")
                    ).filter(string -> !string.isBlank())
                    .toList();
                SharedSuggestionProvider.suggest(acceptableAllies, suggestionsBuilder);
            });
        } else {
            return Suggestions.empty();
        }
    }

    public MutableComponent getRemoveAllyButton(UUID targetPlayerId, String allyName) {
        ClickEvent removeAlly = new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/ally remove %s", allyName));

        return translator.getTextForTarget(targetPlayerId, "commands.overlord.ally.list.remove_ally")
            .setStyle(textStyles.aqua().withClickEvent(removeAlly));
    }

    public MutableComponent getRemoveEnemyButton(UUID targetPlayerId, String enemyName) {
        ClickEvent removeEnemy = new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/enemy remove %s", enemyName));

        return translator.getTextForTarget(targetPlayerId, "commands.overlord.enemy.list.remove_enemy")
            .setStyle(textStyles.aqua().withClickEvent(removeEnemy));
    }
}
