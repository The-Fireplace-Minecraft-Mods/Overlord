package dev.the_fireplace.overlord.client.renderer;

import dev.the_fireplace.overlord.blockentity.TombstoneBlockEntity;
import dev.the_fireplace.overlord.init.OverlordBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.WallSignBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class TombstoneBlockEntityRenderer implements BlockEntityRenderer<TombstoneBlockEntity>
{
    @Override
    public void render(TombstoneBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        // Rotate text
        matrices.translate(0.5D, 0.5D, 0.5D);
        float rotation = -blockEntity.getCachedState().get(WallSignBlock.FACING).asRotation();
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(rotation));
        // This is the translate to mess with position relative to the center of the block
        matrices.translate(0.0D, 0.0D, 2.0 / 18.0);

        // Render text
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        matrices.translate(0.0D, 0.3333333432674408D, 0.046666666865348816D);
        float scale = 0.010416667F / 2f;
        matrices.scale(scale, -scale, scale);
        //TODO calculate this in the BlockEntity instead of hardcoding, make GUI render color and text colors consistent with this
        int baseTextColor = blockEntity.getCachedState().getBlock() == OverlordBlocks.BLACKSTONE_TOMBSTONE ? 0xFFFFFF : 0x000000;
        double colorScale = 0.4D;
        int red = (int) ((double) NativeImage.getRed(baseTextColor) * colorScale);
        int green = (int) ((double) NativeImage.getGreen(baseTextColor) * colorScale);
        int blue = (int) ((double) NativeImage.getBlue(baseTextColor) * colorScale);
        int finalTextColor = NativeImage.getAbgrColor(0, blue, green, red);

        String string = blockEntity.getNameText();
        if (!string.isEmpty()) {
            float x = (float) (-textRenderer.getWidth(string) / 2);
            float y = 5;
            textRenderer.draw(string, x, y, finalTextColor, false, matrices.peek().getModel(), vertexConsumers, false, 0, light);
        }

        matrices.pop();
    }
}
