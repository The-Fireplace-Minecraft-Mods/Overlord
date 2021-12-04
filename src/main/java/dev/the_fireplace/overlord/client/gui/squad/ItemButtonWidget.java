package dev.the_fireplace.overlord.client.gui.squad;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class ItemButtonWidget extends ButtonWidget
{
    protected final ItemStack stack;
    protected boolean isUsed = false;

    public ItemButtonWidget(int x, int y, int width, int height, ItemStack stack, PressAction pressAction) {
        this(x, y, width, height, stack, pressAction, EMPTY);
    }

    public ItemButtonWidget(int x, int y, int width, int height, ItemStack stack, PressAction pressAction, TooltipSupplier tooltipSupplier) {
        super(x, y, width, height, stack.getName(), pressAction, tooltipSupplier);
        this.stack = stack;
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
        ItemRenderer itemRenderer = minecraftClient.getItemRenderer();
        itemRenderer.renderInGui(this.stack, this.x + 2, this.y + 2);
        drawBox(matrices);

        this.renderBackground(matrices, minecraftClient, mouseX, mouseY);
    }

    private void drawBox(MatrixStack matrices) {
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

    public void notifyOfActiveStack(ItemStack stack) {
        this.isUsed = this.stack.equals(stack);
    }
}
