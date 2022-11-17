package dev.the_fireplace.overlord.client.gui.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.domain.client.ScreenOpener;
import dev.the_fireplace.overlord.entity.OwnedSkeletonContainer;
import dev.the_fireplace.overlord.entity.OwnedSkeletonEntity;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static net.minecraft.client.gui.screens.inventory.InventoryScreen.renderEntityInInventory;

public class OwnedSkeletonGui extends AbstractContainerScreen<OwnedSkeletonContainer>
{
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(OverlordConstants.MODID, "textures/gui/skeleton_inventory.png");
    private static final ResourceLocation ICONS_TEXTURE = new ResourceLocation("minecraft", "textures/gui/icons.png");
    private int mouseX;
    private int mouseY;
    private final OwnedSkeletonEntity entity;
    private final ScreenOpener screenOpener;

    public OwnedSkeletonGui(OwnedSkeletonEntity skeleton, Inventory playerInventory, int syncId) {
        super(
            skeleton.getContainer(playerInventory, syncId),
            playerInventory,
            Component.translatable("entity.overlord.owned_skeleton")
        );
        entity = skeleton;
        imageHeight = 252;
        this.screenOpener = OverlordConstants.getInjector().getInstance(ScreenOpener.class);
    }

    @Override
    protected void init() {
        super.init();
        //x, y, width, height
        addRenderableWidget(new Button(leftPos + 96, topPos + 58, 74, 20, Component.translatable("gui.overlord.orders"), (b) -> screenOpener.openOrdersGUI(entity)));
        addRenderableWidget(new Button(leftPos + 96, topPos + 38, 74, 20, Component.translatable("gui.overlord.select_squad"), (b) -> screenOpener.openSquadSelectorGUI(entity)));
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
        int i = this.leftPos;
        int j = this.topPos;
        this.blit(matrixStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
        renderEntityInInventory(i + 51, j + 75, 30, (float) (i + 51) - this.mouseX, (float) (j + 75 - 50) - this.mouseY, entity);
    }

    @Override
    protected void renderLabels(PoseStack matrices, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, ICONS_TEXTURE);
        this.blit(matrices, this.imageWidth / 2 - 10, 4, 16, 0, 9, 9);
        this.blit(matrices, this.imageWidth / 2 - 10, 4, 52, 0, 9, 9);
        String currentHealthMessage = (int) Math.ceil(entity.getHealth()) + "/" + (int) Math.ceil(entity.getMaxHealth());
        this.font.draw(matrices, Component.nullToEmpty(currentHealthMessage), this.imageWidth / 2f, 5, 0xFF0000);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, delta);
        this.renderTooltip(matrixStack, mouseX, mouseY);
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }
}
