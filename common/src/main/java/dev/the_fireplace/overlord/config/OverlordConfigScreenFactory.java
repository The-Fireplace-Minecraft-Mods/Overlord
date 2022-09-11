package dev.the_fireplace.overlord.config;

import dev.the_fireplace.lib.api.chat.injectables.TranslatorFactory;
import dev.the_fireplace.lib.api.chat.interfaces.Translator;
import dev.the_fireplace.lib.api.client.injectables.ConfigScreenBuilderFactory;
import dev.the_fireplace.lib.api.client.interfaces.ConfigScreenBuilder;
import dev.the_fireplace.lib.api.lazyio.injectables.ConfigStateManager;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.domain.config.ConfigValues;
import net.minecraft.client.gui.screens.Screen;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public final class OverlordConfigScreenFactory
{
    private static final String TRANSLATION_BASE = "text.config." + OverlordConstants.MODID + ".";
    private static final String OPTION_TRANSLATION_BASE = TRANSLATION_BASE + "option.";

    private final Translator translator;
    private final ConfigStateManager configStateManager;
    private final OverlordConfig config;
    private final ConfigValues defaultConfigValues;
    private final ConfigScreenBuilderFactory configScreenBuilderFactory;

    private ConfigScreenBuilder configScreenBuilder;

    @Inject
    public OverlordConfigScreenFactory(
        TranslatorFactory translatorFactory,
        ConfigStateManager configStateManager,
        OverlordConfig config,
        @Named("default") ConfigValues defaultConfigValues,
        ConfigScreenBuilderFactory configScreenBuilderFactory
    ) {
        this.translator = translatorFactory.getTranslator(OverlordConstants.MODID);
        this.configStateManager = configStateManager;
        this.config = config;
        this.defaultConfigValues = defaultConfigValues;
        this.configScreenBuilderFactory = configScreenBuilderFactory;
    }

    public Screen getConfigScreen(Screen parent) {
        this.configScreenBuilder = configScreenBuilderFactory.create(
            translator,
            TRANSLATION_BASE + "title",
            TRANSLATION_BASE + "global",
            parent,
            () -> configStateManager.save(config)
        ).get();
        addGlobalCategoryEntries();

        return this.configScreenBuilder.build();
    }

    private void addGlobalCategoryEntries() {
        configScreenBuilder.startSubCategory(TRANSLATION_BASE + "milkAmounts");
        configScreenBuilder.addIntField(
            OPTION_TRANSLATION_BASE + "quarterGrownMilkCount",
            config.getQuarterGrownMilkCount(),
            defaultConfigValues.getQuarterGrownMilkCount(),
            config::setQuarterMilkCount
        ).setMinimum(1).setDescriptionRowCount((byte) 0);
        configScreenBuilder.addIntField(
            OPTION_TRANSLATION_BASE + "halfGrownMilkCount",
            config.getHalfGrownMilkCount(),
            defaultConfigValues.getHalfGrownMilkCount(),
            config::setHalfMilkCount
        ).setMinimum(1).setDescriptionRowCount((byte) 0);
        configScreenBuilder.addIntField(
            OPTION_TRANSLATION_BASE + "threeQuartersGrownMilkCount",
            config.getThreeQuartersGrownMilkCount(),
            defaultConfigValues.getThreeQuartersGrownMilkCount(),
            config::setThreeQuartersMilkCount
        ).setMinimum(1).setDescriptionRowCount((byte) 0);
        configScreenBuilder.addIntField(
            OPTION_TRANSLATION_BASE + "fullyGrownMilkCount",
            config.getFullyGrownMilkCount(),
            defaultConfigValues.getFullyGrownMilkCount(),
            config::setFullyGrownMilkCount
        ).setMinimum(1).setDescriptionRowCount((byte) 0);
        configScreenBuilder.endSubCategory();
        configScreenBuilder.addIntField(
            OPTION_TRANSLATION_BASE + "localOrdersDistance",
            config.getLocalOrdersDistance(),
            defaultConfigValues.getLocalOrdersDistance(),
            config::setLocalOrdersDistance
        ).setMinimum(1);
    }
}
