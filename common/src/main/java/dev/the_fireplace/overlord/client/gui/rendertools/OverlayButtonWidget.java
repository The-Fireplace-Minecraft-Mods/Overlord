package dev.the_fireplace.overlord.client.gui.rendertools;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class OverlayButtonWidget extends Button
{
    public OverlayButtonWidget(int i, int j, int k, int l, Component text, OnPress pressAction) {
        super(i, j, k, l, text, pressAction);
    }

    public OverlayButtonWidget(int i, int j, int k, int l, Component text, OnPress pressAction, OnTooltip tooltipSupplier) {
        super(i, j, k, l, text, pressAction, tooltipSupplier);
    }

    @Override
    public void renderButton(PoseStack matrices, int mouseX, int mouseY, float delta) {
        matrices.pushPose();
        matrices.translate(0, 0, 999);
        super.renderButton(matrices, mouseX, mouseY, delta);
        matrices.popPose();
    }
}
