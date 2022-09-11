package dev.the_fireplace.overlord.client.gui.squad;

import dev.the_fireplace.overlord.client.gui.rendertools.BoxRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;

public class ItemButtonWidget extends Button
{
    protected final ItemStack stack;
    protected boolean isUsed = false;

    public ItemButtonWidget(int x, int y, int width, int height, ItemStack stack, OnPress pressAction) {
        super(x, y, width, height, stack.getHoverName().getString(), pressAction);
        this.stack = stack;
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
        ItemRenderer itemRenderer = minecraftClient.getItemRenderer();
        itemRenderer.renderAndDecorateItem(this.stack, this.x + 2, this.y + 2);
        drawBox();

        this.renderBg(minecraftClient, mouseX, mouseY);
    }

    private void drawBox() {
        int color = this.isHovered ? 0xEED489BF : 0xFFFFFFBF;
        BoxRenderer.drawBox(x, y, width, height, 1, color);
        if (isUsed) {
            BoxRenderer.drawBox(x, y, width, height, 2, color);
        }
    }

    public void notifyOfActiveStack(ItemStack stack) {
        this.isUsed = ItemStack.matches(this.stack, stack);
    }
}
