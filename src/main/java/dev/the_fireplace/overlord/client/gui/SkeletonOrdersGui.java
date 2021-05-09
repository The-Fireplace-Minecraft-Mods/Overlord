package dev.the_fireplace.overlord.client.gui;

import dev.the_fireplace.overlord.api.mechanic.AIControllable;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;

public class SkeletonOrdersGui {
	public static Screen makeOrdersGui(Screen parent, AIControllable entity) {
		ConfigBuilder builder = ConfigBuilder.create()
			.setParentScreen(parent)
			.setTitle("TEST");

		ConfigCategory general = builder.getOrCreateCategory("Weeeeeee");
		ConfigEntryBuilder entryBuilder = builder.entryBuilder();
		general.addEntry(entryBuilder.startBooleanToggle("Big Brain Time", true)
			.setDefaultValue(true)
			.setTooltip("Jk this is a small brain move")
			.setSaveConsumer(newValue -> {})
			.build());

		builder.setSavingRunnable(() -> {

		});
		return builder.build();
	}
}
