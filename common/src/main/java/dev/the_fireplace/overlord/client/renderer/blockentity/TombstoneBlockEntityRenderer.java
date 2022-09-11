package dev.the_fireplace.overlord.client.renderer.blockentity;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.block.OverlordBlocks;
import dev.the_fireplace.overlord.blockentity.TombstoneBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallSignBlock;

public class TombstoneBlockEntityRenderer implements BlockEntityRenderer<TombstoneBlockEntity>
{
    private final OverlordBlocks overlordBlocks;

    public TombstoneBlockEntityRenderer() {
        this.overlordBlocks = OverlordConstants.getInjector().getInstance(OverlordBlocks.class);
    }

    @Override
    public void render(TombstoneBlockEntity blockEntity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        matrices.pushPose();
        // Rotate text
        matrices.translate(0.5D, 0.5D, 0.5D);
        float rotation = -blockEntity.getBlockState().getValue(WallSignBlock.FACING).toYRot();
        matrices.mulPose(Vector3f.YP.rotationDegrees(rotation));
        // This is the translate to mess with position relative to the center of the block
        matrices.translate(0.0D, 0.0D, 2.0 / 18.0);

        // Render text
        Font textRenderer = Minecraft.getInstance().font;
        matrices.translate(0.0D, 0.3333333432674408D, 0.046666666865348816D);
        float scale = 0.010416667F / 2f;
        matrices.scale(scale, -scale, scale);
        //TODO calculate this in the BlockEntity instead of hardcoding, make GUI render color and text colors consistent with this
        Block block = blockEntity.getBlockState().getBlock();
        int baseTextColor = block == overlordBlocks.getBlackstoneTombstone() || block == overlordBlocks.getDeepslateTombstone() ? 0xFFFFFF : 0x000000;
        double colorScale = 0.4D;
        int red = (int) ((double) NativeImage.getR(baseTextColor) * colorScale);
        int green = (int) ((double) NativeImage.getG(baseTextColor) * colorScale);
        int blue = (int) ((double) NativeImage.getB(baseTextColor) * colorScale);
        int finalTextColor = NativeImage.combine(0, blue, green, red);

        String string = blockEntity.getNameText();
        if (!string.isEmpty()) {
            float x = (float) (-textRenderer.width(string) / 2);
            float y = 5;
            textRenderer.drawInBatch(string, x, y, finalTextColor, false, matrices.last().pose(), vertexConsumers, false, 0, light);
        }

        matrices.popPose();
    }
}
