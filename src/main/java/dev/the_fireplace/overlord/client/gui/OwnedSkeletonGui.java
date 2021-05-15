package dev.the_fireplace.overlord.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.api.client.GuiOpener;
import dev.the_fireplace.overlord.entity.OwnedSkeletonContainer;
import dev.the_fireplace.overlord.entity.OwnedSkeletonEntity;
import dev.the_fireplace.overlord.entity.SkeletonInventory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.Objects;

import static net.minecraft.client.gui.screen.ingame.InventoryScreen.drawEntity;

@Environment(EnvType.CLIENT)
public class OwnedSkeletonGui extends ContainerScreen<OwnedSkeletonContainer> {
    public static final Identifier BACKGROUND_TEXTURE = new Identifier(Overlord.MODID, "textures/gui/skeleton_inventory.png");
    private int mouseX;
    private int mouseY;
    private boolean isMouseDown;
    private final SkeletonInventory inv;
    private final OwnedSkeletonEntity entity;
    public OwnedSkeletonGui(OwnedSkeletonEntity skeleton, int syncId) {
        super(skeleton.getContainer(Objects.requireNonNull(MinecraftClient.getInstance().player).inventory, syncId),
            Objects.requireNonNull(MinecraftClient.getInstance().player).inventory,
            new TranslatableText("entity.overlord.owned_skeleton")
        );
        inv = skeleton.getInventory();
        entity = skeleton;
        containerHeight = 252;
    }

    @Override
    protected void init() {
        super.init();
        //x, y, width, height
        addButton(new AbstractPressableButtonWidget(x+109, y+58, 60, 20, I18n.translate("gui.overlord.owned_skeleton.orders")) {
            @Override
            public void onPress() {
                assert OwnedSkeletonGui.this.minecraft != null;
                GuiOpener.getInstance().openOrdersGUI(entity);
            }
        });
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
