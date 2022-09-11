package dev.the_fireplace.overlord.client.gui.squad;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.client.gui.rendertools.BoxRenderer;
import dev.the_fireplace.overlord.client.gui.rendertools.PatternRenderer;
import dev.the_fireplace.overlord.domain.data.objects.Squad;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.renderer.entity.ItemRenderer;

import java.util.UUID;

public class SelectorEntry extends ObjectSelectionList.Entry<SelectorEntry>
{
    protected final Minecraft client;
    protected final Squad squad;
    protected final PatternRenderer patternRenderer;
    protected boolean selected = false;

    SelectorEntry(Squad squad) {
        this.patternRenderer = OverlordConstants.getInjector().getInstance(PatternRenderer.class);
        this.client = Minecraft.getInstance();
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
        if (!patternRenderer.canDrawPattern(squad.getPatternId()) || squad.getItem().isEmpty()) {
            return;
        }
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        patternRenderer.drawPattern(squad.getPatternId(), entryLeft, entryTop, iconWidth, iconHeight, 1.0f);
        Minecraft minecraftClient = Minecraft.getInstance();
        ItemRenderer itemRenderer = minecraftClient.getItemRenderer();
        itemRenderer.renderAndDecorateItem(squad.getItem(), entryLeft, (int) (entryTop + iconHeight / 5f));
        RenderSystem.disableBlend();
    }

    private void drawName(int entryTop, int entryLeft, int width, int height, int iconWidth) {
        String name = this.squad.getName();
        String trimmedName = name.trim();
        int maxNameWidth = width - iconWidth - 3;
        Font font = this.client.font;
        if (font.width(trimmedName) > maxNameWidth) {
            trimmedName = font.substrByWidth(trimmedName, maxNameWidth - font.width("...")) + "...";
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
