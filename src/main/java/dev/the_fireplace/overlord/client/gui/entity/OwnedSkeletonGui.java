package dev.the_fireplace.overlord.client.gui.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.the_fireplace.annotateddi.api.DIContainer;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.domain.client.ScreenOpener;
import dev.the_fireplace.overlord.entity.OwnedSkeletonContainer;
import dev.the_fireplace.overlord.entity.OwnedSkeletonEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import static net.minecraft.client.gui.screen.ingame.InventoryScreen.drawEntity;

@Environment(EnvType.CLIENT)
public class OwnedSkeletonGui extends HandledScreen<OwnedSkeletonContainer>
{
    private static final Identifier BACKGROUND_TEXTURE = new Identifier(Overlord.MODID, "textures/gui/skeleton_inventory.png");
    private static final Identifier ICONS_TEXTURE = new Identifier("minecraft", "textures/gui/icons.png");
    private int mouseX;
    private int mouseY;
    private final OwnedSkeletonEntity entity;
    private final ScreenOpener screenOpener;

    public OwnedSkeletonGui(OwnedSkeletonEntity skeleton, PlayerInventory playerInventory, int syncId) {
        super(
            skeleton.getContainer(playerInventory, syncId),
            playerInventory,
            new TranslatableText("entity.overlord.owned_skeleton")
        );
        entity = skeleton;
        backgroundHeight = 252;
        this.screenOpener = DIContainer.get().getInstance(ScreenOpener.class);
    }

    @Override
    protected void init() {
        super.init();
        //x, y, width, height
        addDrawableChild(new ButtonWidget(x + 96, y + 58, 74, 20, new TranslatableText("gui.overlord.orders"), (b) -> screenOpener.openOrdersGUI(entity)));
        addDrawableChild(new ButtonWidget(x + 96, y + 38, 74, 20, new TranslatableText("gui.overlord.select_squad"), (b) -> screenOpener.openSquadSelectorGUI(entity)));
    }

    @Override
    protected void drawBackground(MatrixStack matrixStack, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
        int i = this.x;
        int j = this.y;
        this.drawTexture(matrixStack, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        drawEntity(i + 51, j + 75, 30, (float) (i + 51) - this.mouseX, (float) (j + 75 - 50) - this.mouseY, entity);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, ICONS_TEXTURE);
        this.drawTexture(matrices, this.backgroundWidth / 2 - 10, 4, 16, 0, 9, 9);
        this.drawTexture(matrices, this.backgroundWidth / 2 - 10, 4, 52, 0, 9, 9);
        String currentHealthMessage = (int) Math.ceil(entity.getHealth()) + "/" + (int) Math.ceil(entity.getMaxHealth());
        this.textRenderer.draw(matrices, Text.of(currentHealthMessage), this.backgroundWidth / 2f, 5, 0xFF0000);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrixStack, mouseX, mouseY);
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }
}
