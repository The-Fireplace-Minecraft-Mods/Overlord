package dev.the_fireplace.overlord.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;

import java.util.List;

@Environment(EnvType.CLIENT)
public interface PartialScreen
{
    <T extends Element & Drawable> List<T> getChildren();
}
