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
    public void render(int index, int entryTop, int entryLeft, int width, int height, int mouseX, int mouseY, boolean hovering, float delta) {
        int iconWidth = (int) (height * 3f / 5f);
        drawIcon(entryTop, entryLeft, height, iconWidth);
        drawName(entryTop, entryLeft, width, height, iconWidth);
        drawBorder(entryTop, entryLeft, width, height, hovering);
    }

    private void drawIcon(int entryTop, int entryLeft, int iconHeight, int iconWidth) {
        if (squad.getPattern().isEmpty() || squad.getItem().isEmpty()) {
            return;
        }
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        PatternRenderer.drawPattern(new Identifier(Overlord.MODID, squad.getPattern()), entryLeft, entryTop, iconWidth, iconHeight, 1.0f);
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        ItemRenderer itemRenderer = minecraftClient.getItemRenderer();
        itemRenderer.renderGuiItem(squad.getItem(), entryLeft, (int) (entryTop + iconHeight / 5f));
        RenderSystem.disableBlend();
    }

    private void drawName(int entryTop, int entryLeft, int width, int height, int iconWidth) {
        String name = this.squad.getName();
        String trimmedName = name.trim();
        int maxNameWidth = width - iconWidth - 3;
        TextRenderer font = this.client.textRenderer;
        if (font.getStringWidth(trimmedName) > maxNameWidth) {
            trimmedName = font.trimToWidth(trimmedName, maxNameWidth - font.getStringWidth("...")) + "...";
        }
        font.draw(trimmedName, entryLeft + iconWidth + 3, entryTop + height / 2f - 4.5f, 0xFFFFFF);
    }

    private void drawBorder(int entryTop, int entryLeft, int width, int height, boolean hovering) {
        int color = hovering ? 0xEED489BF : 0xFFFFFFBF;
        BoxRenderer.drawBox(entryLeft, entryTop, width, height, 0, color);
        if (selected) {
            BoxRenderer.drawBox(entryLeft, entryTop, width, height, 1, color);
        }
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
