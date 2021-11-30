package dev.the_fireplace.overlord.client.gui.block;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CasketGui extends HandledScreen<GenericContainerScreenHandler>
{
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/generic_54.png");

    public CasketGui(GenericContainerScreenHandler container, PlayerInventory playerInventory) {
        super(container, playerInventory, new TranslatableText("container.casket"));
        this.backgroundHeight = 114 + 6 * 18;
    }

    @Override
    protected void drawForeground(MatrixStack matrixStack, int mouseX, int mouseY) {
        this.textRenderer.draw(matrixStack, this.title.asString(), 8.0F, 6.0F, 0x404040);
        this.textRenderer.draw(matrixStack, this.playerInventory.getDisplayName().asString(), 8.0F, (float) (this.backgroundHeight - 96 + 2), 0x404040);
    }

    @Override
    protected void drawBackground(MatrixStack matrixStack, float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        assert this.client != null;
        this.client.getTextureManager().bindTexture(TEXTURE);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(matrixStack, i, j, 0, 0, this.backgroundWidth, 6 * 18 + 17);
        this.drawTexture(matrixStack, i, j + 6 * 18 + 17, 0, 126, this.backgroundWidth, 96);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        super.render(matrixStack, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrixStack, mouseX, mouseY);
    }
}
