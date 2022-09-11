package dev.the_fireplace.overlord.client.gui.config.listbuilder;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ObjectSelectionList;

public class ListSelectorEntry extends ObjectSelectionList.Entry<ListSelectorEntry>
{
    protected final Minecraft client;
    protected final ListSelectorWidget list;

    public ListSelectorEntry(ListSelectorWidget list) {
        this.list = list;
        this.client = Minecraft.getInstance();
    }

    @Override
    public void render(PoseStack matrixStack, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovering, float delta) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        String name = "";//TODO
        String trimmedName = name;
        int maxNameWidth = x - 3;
        Font font = this.client.font;
        if (font.width(name) > maxNameWidth) {
            trimmedName = font.plainSubstrByWidth(name, maxNameWidth - font.width("...")) + "...";
        }
        font.draw(matrixStack, trimmedName, y + 32 + 3, index + 1, 0xFFFFFF);
    }
}
