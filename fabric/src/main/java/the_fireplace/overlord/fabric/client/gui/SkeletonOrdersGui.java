package the_fireplace.overlord.fabric.client.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.List;

@Environment(EnvType.CLIENT)
public class SkeletonOrdersGui extends Screen {
    private static final Identifier WINDOW_TEXTURE = new Identifier("textures/gui/advancements/window.png");
    private static final Identifier BACKGROUND_TEXTURE = new Identifier("textures/block/bone_block_side.png");

    private final String title = getTitle().asFormattedString();
    private final OrderWidget rootWidget = null;
    private final List<OrderWidget> widgets = Lists.newArrayList();
    private double originX;
    private double originY;
    private int minPanX = Integer.MAX_VALUE;
    private int minPanY = Integer.MAX_VALUE;
    private int maxPanX = Integer.MIN_VALUE;
    private int maxPanY = Integer.MIN_VALUE;
    private float alpha;
    private boolean initialized;

    protected SkeletonOrdersGui() {
        super(NarratorManager.EMPTY);
    }

    @Override
    public void removed() {
        //TODO send packet with updated AI
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        int i = (this.width - 252) / 2;
        int j = (this.height - 140) / 2;
        this.renderBackground();
        this.drawOrderTree(mouseX, mouseY, i, j);
        this.drawWidgets(i, j);
        this.drawWidgetTooltip(mouseX, mouseY, i, j);
    }

    private void drawOrderTree(int mouseX, int mouseY, int x, int y) {
        RenderSystem.pushMatrix();
        RenderSystem.translatef((float)(x + 9), (float)(y + 18), 0.0F);
        renderTab();
        RenderSystem.popMatrix();
        RenderSystem.depthFunc(515);
        RenderSystem.disableDepthTest();
    }

    public void renderTab() {
        if (!this.initialized) {
            this.originX = 117 - (this.maxPanX + this.minPanX) / 2f;
            this.originY = 56 - (this.maxPanY + this.minPanY) / 2f;
            this.initialized = true;
        }

        RenderSystem.pushMatrix();
        RenderSystem.enableDepthTest();
        RenderSystem.translatef(0.0F, 0.0F, 950.0F);
        RenderSystem.colorMask(false, false, false, false);
        fill(4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.translatef(0.0F, 0.0F, -950.0F);
        RenderSystem.depthFunc(518);
        fill(234, 113, 0, 0, -16777216);
        RenderSystem.depthFunc(515);
        /*Identifier identifier = this.display.getBackground();
        if (identifier != null) {*/
            this.minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        /*} else {
            this.minecraft.getTextureManager().bindTexture(TextureManager.MISSING_IDENTIFIER);
        }*/

        int i = MathHelper.floor(this.originX);
        int j = MathHelper.floor(this.originY);
        int k = i % 16;
        int l = j % 16;

        for(int m = -1; m <= 15; ++m) {
            for(int n = -1; n <= 8; ++n) {
                blit(k + 16 * m, l + 16 * n, 0.0F, 0.0F, 16, 16, 16, 16);
            }
        }

        //TODO This should probably never be null
        if(this.rootWidget != null) {
            this.rootWidget.renderLines(i, j, true);
            this.rootWidget.renderLines(i, j, false);
            this.rootWidget.renderWidgets(i, j);
        }
        RenderSystem.depthFunc(518);
        RenderSystem.translatef(0.0F, 0.0F, -950.0F);
        RenderSystem.colorMask(false, false, false, false);
        fill(4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.translatef(0.0F, 0.0F, 950.0F);
        RenderSystem.depthFunc(515);
        RenderSystem.popMatrix();
    }

    public void drawWidgets(int x, int y) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        assert this.minecraft != null;
        this.minecraft.getTextureManager().bindTexture(WINDOW_TEXTURE);
        this.blit(x, y, 0, 0, 252, 140);

        this.font.draw(I18n.translate("gui.overlord.skeleton_orders"), (float)(x + 8), (float)(y + 6), 4210752);
    }

    private void drawWidgetTooltip(int mouseX, int mouseY, int x, int y) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.pushMatrix();
        RenderSystem.enableDepthTest();
        RenderSystem.translatef((float)(x + 9), (float)(y + 18), 400.0F);
        //this.selectedTab.drawWidgetTooltip(mouseX - x - 9, mouseY - y - 18, x, y);
        RenderSystem.disableDepthTest();
        RenderSystem.popMatrix();
    }
}
