package dev.the_fireplace.overlord.client.gui.rendertools;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

public class BoxRenderer
{

    public static void drawBox(MatrixStack matrices, int x, int y, int width, int height, int pixelsFromEdge, int color) {
        int boxStartX = x + pixelsFromEdge;
        int boxEndX = x + width - pixelsFromEdge - 1;
        int boxStartY = y + pixelsFromEdge;
        int boxEndY = y + height - pixelsFromEdge - 1;
        drawVerticalLine(matrices, boxStartX, boxStartY, boxEndY, color);
        drawVerticalLine(matrices, boxEndX, boxStartY, boxEndY, color);
        drawHorizontalLine(matrices, boxStartX, boxEndX, boxStartY, color);
        drawHorizontalLine(matrices, boxStartX, boxEndX, boxEndY, color);
    }

    public static void drawHorizontalLine(MatrixStack matrices, int x1, int x2, int y, int color) {
        if (x2 < x1) {
            int i = x1;
            x1 = x2;
            x2 = i;
        }

        DrawableHelper.fill(matrices, x1, y, x2 + 1, y + 1, color);
    }

    public static void drawVerticalLine(MatrixStack matrices, int x, int y1, int y2, int color) {
        if (y2 < y1) {
            int i = y1;
            y1 = y2;
            y2 = i;
        }

        DrawableHelper.fill(matrices, x, y1 + 1, x + 1, y2, color);
    }
}
