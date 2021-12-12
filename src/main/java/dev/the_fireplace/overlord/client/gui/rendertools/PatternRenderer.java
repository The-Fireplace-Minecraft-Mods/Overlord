package dev.the_fireplace.overlord.client.gui.rendertools;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.the_fireplace.overlord.util.SquadPatterns;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PatternRenderer
{
    public static void drawPattern(Identifier patternId, int x, int y, int width, int height, float alpha) {
        //RenderSystem.setShader(GameRenderer::getPositionTexShader);
        MinecraftClient.getInstance().getTextureManager().bindTexture(SquadPatterns.getTextureForPatternId(patternId));
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        DrawableHelper.blit(x, y, width, height, 1, 1, 10, 16, 64, 32);
    }
}
