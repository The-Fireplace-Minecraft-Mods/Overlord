package dev.the_fireplace.overlord.client.gui.squad;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class PatternButtonWidget extends ButtonWidget
{
    protected final Identifier pattern;
    protected boolean isUsed = false;

    public PatternButtonWidget(int x, int y, int width, int height, Text text, Identifier pattern, PressAction pressAction) {
        this(x, y, width, height, text, pattern, pressAction, EMPTY);
    }

    public PatternButtonWidget(int x, int y, int width, int height, Text text, Identifier pattern, PressAction pressAction, TooltipSupplier tooltipSupplier) {
        super(x, y, width, height, text, pressAction, tooltipSupplier);
        this.pattern = pattern;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.drawCustomButton(matrices, mouseX, mouseY);

        if (this.isHovered()) {
            this.renderTooltip(matrices, mouseX, mouseY);
        }
    }

    private void drawCustomButton(MatrixStack matrices, int mouseX, int mouseY) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        TextRenderer textRenderer = minecraftClient.textRenderer;

        PatternRenderHelper.drawPattern(matrices, this.pattern, this.x + 2 + (this.width - 4) / 5, this.y + 2, (this.width - 4) * 3 / 5, this.height - 4, this.alpha);
        drawBox(matrices);

        this.renderBackground(matrices, minecraftClient, mouseX, mouseY);
        int j = this.active ? 16777215 : 10526880;
        drawCenteredText(matrices, textRenderer, this.getMessage(), this.x + this.width / 2, this.y + this.height - 4 - 9, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
    }

    private void drawBox(MatrixStack matrices) {
        //TODO color and/or dotted/dashed change if unlocked or not
        int color = this.hovered ? 0xEED489BF : 0xFFFFFFBF;
        drawBox(matrices, 1, color);
        if (isUsed) {
            drawBox(matrices, 2, color);
        }
    }

    private void drawBox(MatrixStack matrices, int pixelsFromEdge, int color) {
        int boxStartX = this.x + pixelsFromEdge;
        int boxEndX = this.x + this.width - pixelsFromEdge - 1;
        int boxStartY = this.y + pixelsFromEdge;
        int boxEndY = this.y + this.height - pixelsFromEdge - 1;
        this.drawVerticalLine(matrices, boxStartX, boxStartY, boxEndY, color);
        this.drawVerticalLine(matrices, boxEndX, boxStartY, boxEndY, color);
        this.drawHorizontalLine(matrices, boxStartX, boxEndX, boxStartY, color);
        this.drawHorizontalLine(matrices, boxStartX, boxEndX, boxEndY, color);
    }

    public void notifyOfActivePattern(Identifier pattern) {
        this.isUsed = this.pattern.equals(pattern);
    }
}
