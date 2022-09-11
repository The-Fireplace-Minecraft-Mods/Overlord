package dev.the_fireplace.overlord.client.gui.squad;

import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.client.gui.rendertools.BoxRenderer;
import dev.the_fireplace.overlord.client.gui.rendertools.PatternRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class PatternButtonWidget extends Button
{
    protected final PatternRenderer patternRenderer;
    protected final ResourceLocation patternId;
    protected boolean isUsed = false;

    public PatternButtonWidget(int x, int y, int width, int height, Component text, ResourceLocation patternId, OnPress pressAction) {
        super(x, y, width, height, text.getString(), pressAction);
        this.patternId = patternId;
        this.patternRenderer = OverlordConstants.getInjector().getInstance(PatternRenderer.class);
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float delta) {
        this.drawCustomButton(mouseX, mouseY);

        if (this.isHovered() || this.isFocused()) {
            this.renderToolTip(mouseX, mouseY);
        }
    }

    private void drawCustomButton(int mouseX, int mouseY) {
        Minecraft minecraftClient = Minecraft.getInstance();
        Font textRenderer = minecraftClient.font;

        patternRenderer.drawPattern(this.patternId, this.x + 2 + (this.width - 4) / 5, this.y + 2, (this.width - 4) * 3 / 5, this.height - 4, this.alpha);
        drawBox();

        this.renderBg(minecraftClient, mouseX, mouseY);
        int j = this.active ? 16777215 : 10526880;
        drawCenteredString(textRenderer, this.getMessage(), this.x + this.width / 2, this.y + this.height - 4 - 9, j | Mth.ceil(this.alpha * 255.0F) << 24);
    }

    private void drawBox() {
        //TODO color and/or dotted/dashed change if unlocked or not
        int color = this.isHovered ? 0xEED489BF : 0xFFFFFFBF;
        BoxRenderer.drawBox(x, y, width, height, 1, color);
        if (isUsed) {
            BoxRenderer.drawBox(x, y, width, height, 2, color);
        }
    }

    public void notifyOfActivePattern(ResourceLocation pattern) {
        this.isUsed = this.patternId.equals(pattern);
    }
}
