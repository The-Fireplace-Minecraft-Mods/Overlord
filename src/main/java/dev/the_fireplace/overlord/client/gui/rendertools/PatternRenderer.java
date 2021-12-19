package dev.the_fireplace.overlord.client.gui.rendertools;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.the_fireplace.overlord.domain.registry.PatternRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.util.Identifier;

import javax.inject.Inject;

@Environment(EnvType.CLIENT)
public class PatternRenderer
{
    private final PatternRegistry patternRegistry;

    @Inject
    public PatternRenderer(PatternRegistry patternRegistry) {
        this.patternRegistry = patternRegistry;
    }

    public boolean canDrawPattern(Identifier patternId) {
        return patternRegistry.hasPattern(patternId);
    }

    public void drawPattern(Identifier patternId, int x, int y, int width, int height, float alpha) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, patternRegistry.getById(patternId).getTextureLocation());
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        DrawableHelper.blit(x, y, width, height, 1, 1, 10, 16, 64, 32);
    }
}
