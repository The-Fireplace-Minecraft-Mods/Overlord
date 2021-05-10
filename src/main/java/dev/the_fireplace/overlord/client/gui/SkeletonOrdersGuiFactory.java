package dev.the_fireplace.overlord.client.gui;

import dev.the_fireplace.lib.api.chat.TranslatorManager;
import dev.the_fireplace.lib.api.client.AdvancedConfigScreenBuilder;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.api.client.OrdersGuiFactory;
import dev.the_fireplace.overlord.model.aiconfig.AISettings;
import dev.the_fireplace.overlord.model.aiconfig.combat.CombatCategory;
import dev.the_fireplace.overlord.model.aiconfig.misc.MiscCategory;
import dev.the_fireplace.overlord.model.aiconfig.movement.EnumMovementMode;
import dev.the_fireplace.overlord.model.aiconfig.movement.MovementCategory;
import dev.the_fireplace.overlord.model.aiconfig.tasks.TasksCategory;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;

import java.util.Arrays;
import java.util.UUID;
import java.util.function.Consumer;

public class SkeletonOrdersGuiFactory extends AdvancedConfigScreenBuilder implements OrdersGuiFactory {
	private static final String TRANSLATION_BASE = "gui." + Overlord.MODID + ".aisettings.";
	private static final String OPTION_TRANSLATION_BASE = TRANSLATION_BASE + "option.";
	@Deprecated
	public static final SkeletonOrdersGuiFactory INSTANCE = new SkeletonOrdersGuiFactory();

	private final AISettings defaultSettings = new AISettings();

	private SkeletonOrdersGuiFactory() {
		super(TranslatorManager.getInstance().getTranslator(Overlord.MODID));
	}

	@Override
	public Screen build(Screen parent, AISettings settings) {
		ConfigBuilder builder = ConfigBuilder.create()
			.setParentScreen(parent)
			.setTitle(translator.getTranslatedString(TRANSLATION_BASE + "name"));

		buildCategories(builder, settings);

		builder.setSavingRunnable(() -> {

		});

		return builder.build();
	}

	private void buildCategories(ConfigBuilder builder, AISettings currentSettings) {
		ConfigEntryBuilder entryBuilder = builder.entryBuilder();

		ConfigCategory combat = builder.getOrCreateCategory(translator.getTranslatedString(TRANSLATION_BASE + "combat"));
		addCombatSettings(entryBuilder, combat, currentSettings.getCombat().getData());

		ConfigCategory movement = builder.getOrCreateCategory(translator.getTranslatedString(TRANSLATION_BASE + "movement"));
		addMovementSettings(entryBuilder, movement, currentSettings.getMovement().getData());

		ConfigCategory tasks = builder.getOrCreateCategory(translator.getTranslatedString(TRANSLATION_BASE + "tasks"));
		addTasksSettings(entryBuilder, tasks, currentSettings.getTasks().getData());

		ConfigCategory misc = builder.getOrCreateCategory(translator.getTranslatedString(TRANSLATION_BASE + "misc"));
		addMiscSettings(entryBuilder, misc, currentSettings.getMisc().getData());
	}

	private void addCombatSettings(ConfigEntryBuilder entryBuilder, ConfigCategory combatCategory, CombatCategory.Access currentSettings) {
		CombatCategory.Access defaults = defaultSettings.getCombat().getData();
		addBoolToggle(
			entryBuilder,
			combatCategory,
			OPTION_TRANSLATION_BASE + "enabled",
			currentSettings.isEnabled(),
			defaults.isEnabled(),
			currentSettings::setEnabled,
			(byte)0
		);
	}

	private void addMovementSettings(ConfigEntryBuilder entryBuilder, ConfigCategory movementCategory, MovementCategory.Access currentSettings) {
		MovementCategory.Access defaults = defaultSettings.getMovement().getData();
		addBoolToggle(
			entryBuilder,
			movementCategory,
			OPTION_TRANSLATION_BASE + "enabled",
			currentSettings.isEnabled(),
			defaults.isEnabled(),
			currentSettings::setEnabled,
			(byte)0
		);
		AbstractConfigListEntry<?> moveModeEntry = addEnumDropdown(
			entryBuilder,
			movementCategory,
			OPTION_TRANSLATION_BASE + "moveMode",
			currentSettings.getMoveMode(),
			defaults.getMoveMode(),
			Arrays.asList(EnumMovementMode.values()),
			currentSettings::setMoveMode
		);
	}

	private void addTasksSettings(ConfigEntryBuilder entryBuilder, ConfigCategory tasksCategory, TasksCategory.Access currentSettings) {
		TasksCategory.Access defaults = defaultSettings.getTasks().getData();
		addBoolToggle(
			entryBuilder,
			tasksCategory,
			OPTION_TRANSLATION_BASE + "enabled",
			currentSettings.isEnabled(),
			defaults.isEnabled(),
			currentSettings::setEnabled,
			(byte)0
		);
	}

	private void addMiscSettings(ConfigEntryBuilder entryBuilder, ConfigCategory miscCategory, MiscCategory.Access currentSettings) {
		MiscCategory.Access defaults = defaultSettings.getMisc().getData();
		addBoolToggle(
			entryBuilder,
			miscCategory,
			OPTION_TRANSLATION_BASE + "saveDamagedEquipment",
			currentSettings.isSaveDamagedEquipment(),
			defaults.isSaveDamagedEquipment(),
			currentSettings::setSaveDamagedEquipment
		);
		addUniversalList(
			entryBuilder,
			miscCategory,
			OPTION_TRANSLATION_BASE + "saveEquipmentList",
			currentSettings.getSaveEquipmentList(),
			defaults.getSaveEquipmentList(),
			currentSettings::setSaveEquipmentList
		);
		addBoolToggle(
			entryBuilder,
			miscCategory,
			OPTION_TRANSLATION_BASE + "loadChunks",
			currentSettings.isLoadChunks(),
			defaults.isLoadChunks(),
			currentSettings::setLoadChunks
		);
	}

	protected void addUniversalList(
		ConfigEntryBuilder entryBuilder,
		ConfigCategory category,
		String optionTranslationBase,
		UUID currentValue,
		UUID defaultValue,
		Consumer<UUID> saveFunction
	) {
		//TODO Probably button that opens List selector GUI
		// Temporarily showing it as a string.
		addStringField(
			entryBuilder,
			category,
			optionTranslationBase,
			currentValue.toString(),
			defaultValue.toString(),
			s -> saveFunction.accept(UUID.fromString(s))
		);
	}
}
