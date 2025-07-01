package dev.the_fireplace.overlord.client.gui.squad;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.the_fireplace.overlord.client.gui.rendertools.BoxRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class ItemButtonWidget extends Button
{
    protected final ItemStack stack;
    protected boolean isUsed = false;

    public ItemButtonWidget(int x, int y, int width, int height, ItemStack stack, OnPress pressAction) {
        super(x, y, width, height, stack.getHoverName(), pressAction, Supplier::get);
        this.stack = stack;
    }

    @Override
    public void renderWidget(PoseStack matrices, int mouseX, int mouseY, float delta) {
        this.drawCustomButton(matrices, mouseX, mouseY);

//        if (this.isHoveredOrFocused()) {
//            this.renderToolTip(matrices, mouseX, mouseY);
//        }
    }

    private void drawCustomButton(PoseStack matrices, int mouseX, int mouseY) {
        Minecraft minecraftClient = Minecraft.getInstance();
        ItemRenderer itemRenderer = minecraftClient.getItemRenderer();
        itemRenderer.renderAndDecorateFakeItem(matrices, this.stack, this.getX() + 2, this.getY() + 2);
        drawBox(matrices);

//        this.renderBg(matrices, minecraftClient, mouseX, mouseY);
    }

    private void drawBox(PoseStack matrices) {
        int color = this.isHovered ? 0xEED489BF : 0xFFFFFFBF;
        BoxRenderer.drawBox(matrices, this.getX(), this.getY(), width, height, 1, color);
        if (isUsed) {
            BoxRenderer.drawBox(matrices, this.getX(), this.getY(), width, height, 2, color);
        }
    }

    public void notifyOfActiveStack(ItemStack stack) {
        this.isUsed = ItemStack.matches(this.stack, stack);
    }
}
