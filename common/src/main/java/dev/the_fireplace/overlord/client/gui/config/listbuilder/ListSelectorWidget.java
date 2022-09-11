package dev.the_fireplace.overlord.client.gui.config.listbuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;

public class ListSelectorWidget extends ObjectSelectionList<ListSelectorEntry>
{
    public ListSelectorWidget(Minecraft minecraftClient, int width, int height, int top, int bottom, int itemHeight) {
        super(minecraftClient, width, height, top, bottom, itemHeight);
        this.setRenderBackground(false);
    }
}
