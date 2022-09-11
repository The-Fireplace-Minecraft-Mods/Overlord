package dev.the_fireplace.overlord.client.gui;


import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;

import java.util.List;

public interface PartialScreen
{
    <T extends GuiEventListener & Widget & NarratableEntry> List<T> getChildren();
}
