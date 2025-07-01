package dev.the_fireplace.overlord.client.gui;


import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;

import java.util.List;

public interface PartialScreen
{
    <T extends GuiEventListener & Renderable & NarratableEntry> List<T> getChildren();
}
