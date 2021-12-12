package dev.the_fireplace.overlord.client.gui.squad;

import dev.the_fireplace.overlord.client.gui.rendertools.BoxRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class ItemButtonWidget extends ButtonWidget
{
    protected final ItemStack stack;
    protected boolean isUsed = false;

    public ItemButtonWidget(int x, int y, int width, int height, ItemStack stack, PressAction pressAction) {
        super(x, y, width, height, stack.getName().asFormattedString(), pressAction);
        this.stack = stack;
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
        ItemRenderer itemRenderer = minecraftClient.getItemRenderer();
        itemRenderer.renderGuiItem(this.stack, this.x + 2, this.y + 2);
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
        this.isUsed = ItemStack.areItemsEqual(this.stack, stack) && ItemStack.areTagsEqual(this.stack, stack);
    }
}
