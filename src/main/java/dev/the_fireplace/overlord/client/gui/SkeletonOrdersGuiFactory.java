package dev.the_fireplace.overlord.client.gui;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.lib.api.chat.injectables.TranslatorFactory;
import dev.the_fireplace.lib.api.chat.interfaces.Translator;
import dev.the_fireplace.lib.api.client.injectables.ConfigScreenBuilderFactory;
import dev.the_fireplace.lib.api.client.interfaces.ConfigScreenBuilder;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.domain.client.OrdersGuiFactory;
import dev.the_fireplace.overlord.domain.entity.OrderableEntity;
import dev.the_fireplace.overlord.domain.network.ClientToServerPacketIDs;
import dev.the_fireplace.overlord.domain.network.client.SaveAIPacketBufferBuilder;
import dev.the_fireplace.overlord.model.aiconfig.AISettings;
import dev.the_fireplace.overlord.model.aiconfig.combat.CombatCategory;
import dev.the_fireplace.overlord.model.aiconfig.misc.MiscCategory;
import dev.the_fireplace.overlord.model.aiconfig.movement.EnumMovementMode;
import dev.the_fireplace.overlord.model.aiconfig.movement.MovementCategory;
import dev.the_fireplace.overlord.model.aiconfig.tasks.TasksCategory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.Screen;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.UUID;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
@Implementation
public final class SkeletonOrdersGuiFactory implements OrdersGuiFactory {
	private static final String TRANSLATION_BASE = "gui." + Overlord.MODID + ".aisettings.";
	private static final String OPTION_TRANSLATION_BASE = TRANSLATION_BASE + "option.";

	private final AISettings defaultSettings = new AISettings();
	private final Translator translator;
	private final ConfigScreenBuilderFactory configScreenBuilderFactory;
	private final ClientToServerPacketIDs clientToServerPacketIDs;
	private final SaveAIPacketBufferBuilder saveAIPacketBufferBuilder;
	private ConfigScreenBuilder screenBuilder;

	@Inject
	public SkeletonOrdersGuiFactory(
		TranslatorFactory translatorFactory,
		ConfigScreenBuilderFactory configScreenBuilderFactory,
		ClientToServerPacketIDs clientToServerPacketIDs,
		SaveAIPacketBufferBuilder saveAIPacketBufferBuilder
	) {
		this.translator = translatorFactory.getTranslator(Overlord.MODID);
		this.configScreenBuilderFactory = configScreenBuilderFactory;
		this.clientToServerPacketIDs = clientToServerPacketIDs;
		this.saveAIPacketBufferBuilder = saveAIPacketBufferBuilder;
	}

	@Override
	public Screen build(Screen parent, OrderableEntity aiEntity) {
		this.screenBuilder = configScreenBuilderFactory.create(
			translator,
			TRANSLATION_BASE + "name",
			TRANSLATION_BASE + "combat",
			parent,
			() -> ClientPlayNetworking.send(
				clientToServerPacketIDs.saveAiPacketID(),
				saveAIPacketBufferBuilder.build(aiEntity)
			)
		);

		buildCategories(aiEntity.getAISettings());

		return this.screenBuilder.build();
	}

	private void buildCategories(AISettings currentSettings) {
		addCombatSettings(currentSettings.getCombat());

		this.screenBuilder.startCategory(TRANSLATION_BASE + "movement");
		addMovementSettings(currentSettings.getMovement());

		this.screenBuilder.startCategory(TRANSLATION_BASE + "tasks");
		addTasksSettings(currentSettings.getTasks());

		this.screenBuilder.startCategory(TRANSLATION_BASE + "misc");
		addMiscSettings(currentSettings.getMisc());
	}

	private void addCombatSettings(CombatCategory currentSettings) {
		CombatCategory defaults = defaultSettings.getCombat();
		this.screenBuilder.addBoolToggle(
			OPTION_TRANSLATION_BASE + "enabled",
			currentSettings.isEnabled(),
			defaults.isEnabled(),
			currentSettings::setEnabled,
			(byte) 0
		);
	}

	private void addMovementSettings(MovementCategory currentSettings) {
		MovementCategory defaults = defaultSettings.getMovement();
		this.screenBuilder.addBoolToggle(
			OPTION_TRANSLATION_BASE + "enabled",
			currentSettings.isEnabled(),
			defaults.isEnabled(),
			currentSettings::setEnabled,
			(byte) 0
		);
		this.screenBuilder.addEnumDropdown(
			OPTION_TRANSLATION_BASE + "moveMode",
			currentSettings.getMoveMode(),
			defaults.getMoveMode(),
			Arrays.asList(EnumMovementMode.values()),
			currentSettings::setMoveMode
		);
	}

	private void addTasksSettings(TasksCategory currentSettings) {
		TasksCategory defaults = defaultSettings.getTasks();
		this.screenBuilder.addBoolToggle(
			OPTION_TRANSLATION_BASE + "enabled",
			currentSettings.isEnabled(),
			defaults.isEnabled(),
			currentSettings::setEnabled,
			(byte) 0
		);
	}

	private void addMiscSettings(MiscCategory currentSettings) {
		MiscCategory defaults = defaultSettings.getMisc();
		this.screenBuilder.addBoolToggle(
			OPTION_TRANSLATION_BASE + "saveDamagedEquipment",
			currentSettings.isSaveDamagedEquipment(),
			defaults.isSaveDamagedEquipment(),
			currentSettings::setSaveDamagedEquipment
		);
		addUniversalList(
			OPTION_TRANSLATION_BASE + "saveEquipmentList",
			currentSettings.getSaveEquipmentList(),
			defaults.getSaveEquipmentList(),
			currentSettings::setSaveEquipmentList
		);
		this.screenBuilder.addBoolToggle(
			OPTION_TRANSLATION_BASE + "loadChunks",
			currentSettings.isLoadChunks(),
			defaults.isLoadChunks(),
			currentSettings::setLoadChunks
		);
	}

	protected void addUniversalList(
		String optionTranslationBase,
		UUID currentValue,
		UUID defaultValue,
		Consumer<UUID> saveFunction
	) {
		//TODO Probably button that opens List selector GUI
		// Temporarily showing it as a string.
		this.screenBuilder.addStringField(
			optionTranslationBase,
			currentValue.toString(),
			defaultValue.toString(),
			s -> saveFunction.accept(UUID.fromString(s))
		);
	}
}
