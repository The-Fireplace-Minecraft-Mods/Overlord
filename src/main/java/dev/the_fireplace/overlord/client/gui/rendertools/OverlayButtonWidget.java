package dev.the_fireplace.overlord.client.gui.rendertools;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class OverlayButtonWidget extends ButtonWidget
{
    public OverlayButtonWidget(int i, int j, int k, int l, Text text, PressAction pressAction) {
        super(i, j, k, l, text, pressAction);
    }

    public OverlayButtonWidget(int i, int j, int k, int l, Text text, PressAction pressAction, TooltipSupplier tooltipSupplier) {
        super(i, j, k, l, text, pressAction, tooltipSupplier);
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        matrices.push();
        matrices.translate(0, 0, 999);
        super.renderButton(matrices, mouseX, mouseY, delta);
        matrices.pop();
    }
}
