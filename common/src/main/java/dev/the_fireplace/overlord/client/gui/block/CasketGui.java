package dev.the_fireplace.overlord.client.gui.block;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;

public class CasketGui extends AbstractContainerScreen<ChestMenu>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");

    public CasketGui(ChestMenu container, Inventory playerInventory) {
        super(container, playerInventory, Component.translatable("container.casket"));
        this.imageHeight = 114 + 6 * 18;
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        this.font.draw(matrixStack, this.title, 8.0F, 6.0F, 0x404040);
        this.font.draw(matrixStack, this.playerInventoryTitle, 8.0F, (float) (this.imageHeight - 96 + 2), 0x404040);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack, i, j, 0, 0, this.imageWidth, 6 * 18 + 17);
        this.blit(matrixStack, i, j + 6 * 18 + 17, 0, 126, this.imageWidth, 96);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float delta) {
        super.render(matrixStack, mouseX, mouseY, delta);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }
}
