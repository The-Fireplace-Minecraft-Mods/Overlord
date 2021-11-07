package dev.the_fireplace.overlord.client.gui.config.listbuilder;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class ListSelectorEntry extends AlwaysSelectedEntryListWidget.Entry<ListSelectorEntry>
{
    protected final MinecraftClient client;
    protected final ListSelectorWidget list;

    public ListSelectorEntry(ListSelectorWidget list) {
        this.list = list;
        this.client = MinecraftClient.getInstance();
    }

    @Override
    public void render(MatrixStack matrixStack, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovering, float delta) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        String name = "";//TODO
        String trimmedName = name;
        int maxNameWidth = x - 3;
        TextRenderer font = this.client.textRenderer;
        if (font.getWidth(name) > maxNameWidth) {
            trimmedName = font.trimToWidth(name, maxNameWidth - font.getWidth("...")) + "...";
        }
        font.draw(matrixStack, trimmedName, y + 32 + 3, index + 1, 0xFFFFFF);
    }
}
