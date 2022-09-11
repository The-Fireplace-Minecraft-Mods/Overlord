package dev.the_fireplace.overlord.client.gui.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.domain.client.ScreenOpener;
import dev.the_fireplace.overlord.entity.OwnedSkeletonContainer;
import dev.the_fireplace.overlord.entity.OwnedSkeletonEntity;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;
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
            new TranslatableComponent("entity.overlord.owned_skeleton")
        );
        entity = skeleton;
        imageHeight = 252;
        this.screenOpener = OverlordConstants.getInjector().getInstance(ScreenOpener.class);
    }

    @Override
    protected void init() {
        super.init();
        //x, y, width, height
        addButton(new Button(leftPos + 96, topPos + 58, 74, 20, I18n.get("gui.overlord.orders"), (b) -> screenOpener.openOrdersGUI(entity)));
        addButton(new Button(leftPos + 96, topPos + 38, 74, 20, I18n.get("gui.overlord.select_squad"), (b) -> screenOpener.openSquadSelectorGUI(entity)));
    }

    @Override
    protected void renderBg(float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(BACKGROUND_TEXTURE);
        int i = this.leftPos;
        int j = this.topPos;
        this.blit(i, j, 0, 0, this.imageWidth, this.imageHeight);
        renderEntityInInventory(i + 51, j + 75, 30, (float) (i + 51) - this.mouseX, (float) (j + 75 - 50) - this.mouseY, entity);
    }

    @Override
    protected void renderLabels(int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(ICONS_TEXTURE);
        this.blit(this.imageWidth / 2 - 10, 4, 16, 0, 9, 9);
        this.blit(this.imageWidth / 2 - 10, 4, 52, 0, 9, 9);
        String currentHealthMessage = (int) Math.ceil(entity.getHealth()) + "/" + (int) Math.ceil(entity.getMaxHealth());
        this.font.draw(currentHealthMessage, this.imageWidth / 2f, 5, 0xFF0000);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        super.render(mouseX, mouseY, delta);
        this.renderTooltip(mouseX, mouseY);
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }
}
