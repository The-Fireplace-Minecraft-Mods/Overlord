package dev.the_fireplace.overlord.client.renderer;

import dev.the_fireplace.overlord.blockentity.TombstoneBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.WallSignBlock;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;

@Environment(EnvType.CLIENT)
public class TombstoneBlockEntityRenderer extends BlockEntityRenderer<TombstoneBlockEntity>
{
    public TombstoneBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(TombstoneBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        // Rotate text
        matrices.translate(0.5D, 0.5D, 0.5D);
        float rotation = -blockEntity.getCachedState().get(WallSignBlock.FACING).asRotation();
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(rotation));
        // This is the translate to mess with position relative to the center of the block
        matrices.translate(0.0D, 0.0D, 2.0 / 18.0);

        // Render text
        TextRenderer textRenderer = this.dispatcher.getTextRenderer();
        matrices.translate(0.0D, 0.3333333432674408D, 0.046666666865348816D);
        float scale = 0.010416667F / 2f;
        matrices.scale(scale, -scale, scale);
        int baseTextColor = 0x000000;
        double colorScale = 0.4D;
        int red = (int) ((double) NativeImage.method_24033(baseTextColor) * colorScale);
        int green = (int) ((double) NativeImage.method_24034(baseTextColor) * colorScale);
        int blue = (int) ((double) NativeImage.method_24035(baseTextColor) * colorScale);
        int finalTextColor = NativeImage.method_24031(0, blue, green, red);

        String string = blockEntity.getNameText();
        if (!string.isEmpty()) {
            float x = (float) (-textRenderer.getStringWidth(string) / 2);
            float y = 5;
            textRenderer.draw(string, x, y, finalTextColor, false, matrices.peek().getModel(), vertexConsumers, false, 0, light);
        }

        matrices.pop();
    }
}
