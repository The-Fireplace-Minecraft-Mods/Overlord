package dev.the_fireplace.overlord.client.gui.squad;

import dev.the_fireplace.annotateddi.api.DIContainer;
import dev.the_fireplace.overlord.client.gui.rendertools.BoxRenderer;
import dev.the_fireplace.overlord.client.gui.rendertools.PatternRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class PatternButtonWidget extends ButtonWidget
{
    protected final PatternRenderer patternRenderer;
    protected final Identifier patternId;
    protected boolean isUsed = false;

    public PatternButtonWidget(int x, int y, int width, int height, Text text, Identifier patternId, PressAction pressAction) {
        this(x, y, width, height, text, patternId, pressAction, EMPTY);
    }

    public PatternButtonWidget(int x, int y, int width, int height, Text text, Identifier patternId, PressAction pressAction, TooltipSupplier tooltipSupplier) {
        super(x, y, width, height, text, pressAction, tooltipSupplier);
        this.patternId = patternId;
        this.patternRenderer = DIContainer.get().getInstance(PatternRenderer.class);
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

        patternRenderer.drawPattern(matrices, this.patternId, this.x + 2 + (this.width - 4) / 5, this.y + 2, (this.width - 4) * 3 / 5, this.height - 4, this.alpha);
        drawBox(matrices);

        this.renderBackground(matrices, minecraftClient, mouseX, mouseY);
        int j = this.active ? 16777215 : 10526880;
        drawCenteredText(matrices, textRenderer, this.getMessage(), this.x + this.width / 2, this.y + this.height - 4 - 9, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
    }

    private void drawBox(MatrixStack matrices) {
        //TODO color and/or dotted/dashed change if unlocked or not
        int color = this.hovered ? 0xEED489BF : 0xFFFFFFBF;
        BoxRenderer.drawBox(matrices, x, y, width, height, 1, color);
        if (isUsed) {
            BoxRenderer.drawBox(matrices, x, y, width, height, 2, color);
        }
    }

    public void notifyOfActivePattern(Identifier pattern) {
        this.isUsed = this.patternId.equals(pattern);
    }
}
