package dev.the_fireplace.overlord.client.gui.rendertools;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.the_fireplace.overlord.domain.registry.PatternRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
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

    public void drawPattern(ResourceLocation patternId, int x, int y, int width, int height, float alpha) {
        Minecraft.getInstance().getTextureManager().bind(patternRegistry.getById(patternId).getTextureLocation());
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        GuiComponent.blit(x, y, width, height, 1, 1, 10, 16, 64, 32);
    }
}