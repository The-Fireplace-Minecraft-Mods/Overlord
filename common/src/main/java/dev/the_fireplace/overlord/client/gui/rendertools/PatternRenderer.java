package dev.the_fireplace.overlord.client.gui.rendertools;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.the_fireplace.overlord.domain.registry.PatternRegistry;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

import javax.inject.Inject;

public class PatternRenderer
{
    private final PatternRegistry patternRegistry;

    @Inject
    public PatternRenderer(PatternRegistry patternRegistry) {
        this.patternRegistry = patternRegistry;
    }

    public boolean canDrawPattern(ResourceLocation patternId) {
        return patternRegistry.hasPattern(patternId);
    }

    public void drawPattern(PoseStack matrices, ResourceLocation patternId, int x, int y, int width, int height, float alpha) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, patternRegistry.getById(patternId).getTextureLocation());
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        GuiComponent.blit(matrices, x, y, width, height, 1, 1, 10, 16, 64, 32);
    }
}
