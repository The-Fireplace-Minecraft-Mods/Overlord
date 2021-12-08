package dev.the_fireplace.overlord.client.gui.squad;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.client.gui.rendertools.BoxRenderer;
import dev.the_fireplace.overlord.client.gui.rendertools.PatternRenderer;
import dev.the_fireplace.overlord.domain.data.objects.Squad;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.UUID;

@Environment(EnvType.CLIENT)
public class SelectorEntry extends AlwaysSelectedEntryListWidget.Entry<SelectorEntry>
{
    protected final MinecraftClient client;
    protected final Squad squad;
    protected boolean selected = false;

    SelectorEntry(Squad squad) {
        this.client = MinecraftClient.getInstance();
        this.squad = squad;
    }

    @Override
    public void render(MatrixStack matrixStack, int index, int entryTop, int entryLeft, int width, int height, int mouseX, int mouseY, boolean hovering, float delta) {
        int iconWidth = (int) (height * 3f / 5f);
        drawIcon(matrixStack, entryTop, entryLeft, height, iconWidth);
        drawName(matrixStack, entryTop, entryLeft, width, height, iconWidth);
        drawBorder(matrixStack, entryTop, entryLeft, width, height, hovering);
    }

    private void drawIcon(MatrixStack matrixStack, int entryTop, int entryLeft, int iconHeight, int iconWidth) {
        if (squad.getPattern().isBlank() || squad.getItem().isEmpty()) {
            return;
        }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        PatternRenderer.drawPattern(matrixStack, new Identifier(Overlord.MODID, squad.getPattern()), entryLeft, entryTop, iconWidth, iconHeight, 1.0f);
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        ItemRenderer itemRenderer = minecraftClient.getItemRenderer();
        matrixStack.push();
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        itemRenderer.renderInGui(squad.getItem(), entryLeft, (int) (entryTop + iconHeight / 5f));
        matrixStack.pop();
        RenderSystem.disableBlend();
    }

    private void drawName(MatrixStack matrixStack, int entryTop, int entryLeft, int width, int height, int iconWidth) {
        String name = this.squad.getName();
        String trimmedName = name.trim();
        int maxNameWidth = width - iconWidth - 3;
        TextRenderer font = this.client.textRenderer;
        if (font.getWidth(trimmedName) > maxNameWidth) {
            trimmedName = font.trimToWidth(trimmedName, maxNameWidth - font.getWidth("...")) + "...";
        }
        font.draw(matrixStack, trimmedName, entryLeft + iconWidth + 3, entryTop + height / 2f - 4.5f, 0xFFFFFF);
    }

    private void drawBorder(MatrixStack matrixStack, int entryTop, int entryLeft, int width, int height, boolean hovering) {
        int color = hovering ? 0xEED489BF : 0xFFFFFFBF;
        BoxRenderer.drawBox(matrixStack, entryLeft, entryTop, width, height, 0, color);
        if (selected) {
            BoxRenderer.drawBox(matrixStack, entryLeft, entryTop, width, height, 1, color);
        }
    }

    @Override
    public Text getNarration() {
        return Text.of(this.squad.getName());
    }

    public boolean hasId(UUID squadId) {
        return squad.getSquadId().equals(squadId);
    }

    public UUID getSquadId() {
        return squad.getSquadId();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return true;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
