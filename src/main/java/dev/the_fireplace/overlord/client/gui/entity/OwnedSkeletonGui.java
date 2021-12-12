package dev.the_fireplace.overlord.client.gui.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.the_fireplace.annotateddi.api.DIContainer;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.domain.client.ScreenOpener;
import dev.the_fireplace.overlord.entity.OwnedSkeletonContainer;
import dev.the_fireplace.overlord.entity.OwnedSkeletonEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import static net.minecraft.client.gui.screen.ingame.InventoryScreen.drawEntity;

@Environment(EnvType.CLIENT)
public class OwnedSkeletonGui extends ContainerScreen<OwnedSkeletonContainer> {
    public static final Identifier BACKGROUND_TEXTURE = new Identifier(Overlord.MODID, "textures/gui/skeleton_inventory.png");
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
        containerHeight = 252;
        this.screenOpener = DIContainer.get().getInstance(ScreenOpener.class);
    }

    @Override
    protected void init() {
        super.init();
        //x, y, width, height
        addButton(new ButtonWidget(x + 96, y + 58, 74, 20, I18n.translate("gui.overlord.orders"), (b) -> screenOpener.openOrdersGUI(entity)));
        addButton(new ButtonWidget(x + 96, y + 38, 74, 20, I18n.translate("gui.overlord.select_squad"), (b) -> screenOpener.openSquadSelectorGUI(entity)));
    }

    @Override
    protected void drawBackground(float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        assert this.minecraft != null;
        this.minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        int i = this.x;
        int j = this.y;
        this.blit(i, j, 0, 0, this.containerWidth, this.containerHeight);
        drawEntity(i + 51, j + 75, 30, (float)(i + 51) - this.mouseX, (float)(j + 75 - 50) - this.mouseY, entity);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        super.render(mouseX, mouseY, delta);
        this.drawMouseoverTooltip(mouseX, mouseY);
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }
}
