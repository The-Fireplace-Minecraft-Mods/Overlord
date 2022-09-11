package dev.the_fireplace.overlord.client.gui.block;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;

public class CasketGui extends AbstractContainerScreen<ChestMenu>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");

    public CasketGui(ChestMenu container, Inventory playerInventory) {
        super(container, playerInventory, new TranslatableComponent("container.casket"));
        this.imageHeight = 114 + 6 * 18;
    }

    @Override
    protected void renderLabels(int mouseX, int mouseY) {
        this.font.draw(this.title.getString(), 8.0F, 6.0F, 0x404040);
        this.font.draw(this.inventory.getName().toString(), 8.0F, (float) (this.imageHeight - 96 + 2), 0x404040);
    }

    @Override
    protected void renderBg(float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(TEXTURE);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(i, j, 0, 0, this.imageWidth, 6 * 18 + 17);
        this.blit(i, j + 6 * 18 + 17, 0, 126, this.imageWidth, 96);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        super.render(mouseX, mouseY, delta);
        this.renderTooltip(mouseX, mouseY);
    }
}
