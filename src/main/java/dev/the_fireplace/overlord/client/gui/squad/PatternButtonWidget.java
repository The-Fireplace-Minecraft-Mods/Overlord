package dev.the_fireplace.overlord.client.gui.squad;

import dev.the_fireplace.overlord.client.gui.rendertools.BoxRenderer;
import dev.the_fireplace.overlord.client.gui.rendertools.PatternRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class PatternButtonWidget extends ButtonWidget
{
    protected final Identifier pattern;
    protected boolean isUsed = false;

    public PatternButtonWidget(int x, int y, int width, int height, Text text, Identifier pattern, PressAction pressAction) {
        super(x, y, width, height, text.asFormattedString(), pressAction);
        this.pattern = pattern;
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float delta) {
        this.drawCustomButton(mouseX, mouseY);

        if (this.isHovered()) {
            this.renderToolTip(mouseX, mouseY);
        }
    }

    private void drawCustomButton(int mouseX, int mouseY) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        TextRenderer textRenderer = minecraftClient.textRenderer;

        PatternRenderer.drawPattern(this.pattern, this.x + 2 + (this.width - 4) / 5, this.y + 2, (this.width - 4) * 3 / 5, this.height - 4, this.alpha);
        drawBox();

        this.renderBg(minecraftClient, mouseX, mouseY);
        int j = this.active ? 16777215 : 10526880;
        drawCenteredString(textRenderer, this.getMessage(), this.x + this.width / 2, this.y + this.height - 4 - 9, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
    }

    private void drawBox() {
        //TODO color and/or dotted/dashed change if unlocked or not
        int color = this.isHovered ? 0xEED489BF : 0xFFFFFFBF;
        BoxRenderer.drawBox(x, y, width, height, 1, color);
        if (isUsed) {
            BoxRenderer.drawBox(x, y, width, height, 2, color);
        }
    }

    public void notifyOfActivePattern(Identifier pattern) {
        this.isUsed = this.pattern.equals(pattern);
    }
}
