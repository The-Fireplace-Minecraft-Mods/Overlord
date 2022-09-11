package dev.the_fireplace.overlord.client.renderer.blockentity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.block.AbstractArmySkullBlock;
import dev.the_fireplace.overlord.block.ArmySkullBlock;
import dev.the_fireplace.overlord.block.WallArmySkullBlock;
import dev.the_fireplace.overlord.blockentity.ArmySkullBlockEntity;
import net.minecraft.Util;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public class ArmySkullBlockEntityRenderer implements BlockEntityRenderer<ArmySkullBlockEntity>
{
    private final Map<AbstractArmySkullBlock.SkullType, SkullModelBase> MODELS;
    private static final Map<AbstractArmySkullBlock.SkullType, ResourceLocation> TEXTURES = Util.make(Maps.newHashMap(), (map) -> {
        map.put(AbstractArmySkullBlock.SkullType.MUSCLE_SKELETON, new ResourceLocation(OverlordConstants.MODID, "textures/entity/owned_skeleton/owned_skeleton_muscles_4.png"));
        map.put(AbstractArmySkullBlock.SkullType.MUSCLE_SKIN_SKELETON, new ResourceLocation(OverlordConstants.MODID, "textures/entity/owned_skeleton/owned_skeleton_skin_muscles_4.png"));
        map.put(AbstractArmySkullBlock.SkullType.SKIN_SKELETON, new ResourceLocation(OverlordConstants.MODID, "textures/entity/owned_skeleton/owned_skeleton_skin_4.png"));
    });

    public static Map<AbstractArmySkullBlock.SkullType, SkullModelBase> getModels(EntityModelSet modelLoader) {
        ImmutableMap.Builder<AbstractArmySkullBlock.SkullType, SkullModelBase> builder = ImmutableMap.builder();
        builder.put(AbstractArmySkullBlock.SkullType.MUSCLE_SKELETON, new SkullModel(modelLoader.bakeLayer(ModelLayers.PLAYER_HEAD)));
        builder.put(AbstractArmySkullBlock.SkullType.MUSCLE_SKIN_SKELETON, new SkullModel(modelLoader.bakeLayer(ModelLayers.PLAYER_HEAD)));
        builder.put(AbstractArmySkullBlock.SkullType.SKIN_SKELETON, new SkullModel(modelLoader.bakeLayer(ModelLayers.PLAYER_HEAD)));
        return builder.build();
    }

    public ArmySkullBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.MODELS = getModels(ctx.getModelSet());
    }

    @Override
    public void render(ArmySkullBlockEntity skullBlockEntity, float f, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, int j) {
        BlockState cachedState = skullBlockEntity.getBlockState();
        boolean isWall = cachedState.getBlock() instanceof WallArmySkullBlock;
        Direction direction = isWall ? cachedState.getValue(WallArmySkullBlock.FACING) : null;
        float yaw = 22.5F * (float) (isWall ? (2 + direction.get2DDataValue()) * 4 : cachedState.getValue(ArmySkullBlock.ROTATION));
        AbstractArmySkullBlock.SkullType skullType = ((AbstractArmySkullBlock) cachedState.getBlock()).getSkullType();
        SkullModelBase skullBlockEntityModel = this.MODELS.get(skullType);
        RenderType renderLayer = getRenderLayer(skullType);
        renderSkull(direction, OverlayTexture.NO_OVERLAY, yaw, 0, matrixStack, vertexConsumerProvider, i, skullBlockEntityModel, renderLayer);
    }

    public static void renderSkull(Direction direction, int overlay, float yaw, float animationProgress, PoseStack matrices, MultiBufferSource vertexConsumers, int light, SkullModelBase model, RenderType renderLayer) {
        matrices.pushPose();
        if (direction == null) {
            matrices.translate(0.5, 0.0, 0.5);
        } else {
            matrices.translate(0.5F - (float) direction.getStepX() * 0.25F, 0.25, 0.5F - (float) direction.getStepZ() * 0.25F);
        }

        matrices.scale(-1.0F, -1.0F, 1.0F);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);
        model.setupAnim(animationProgress, yaw, 0.0F);
        model.renderToBuffer(matrices, vertexConsumer, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
        matrices.popPose();
    }

    public static RenderType getRenderLayer(AbstractArmySkullBlock.SkullType type) {
        ResourceLocation identifier = TEXTURES.get(type);
        return RenderType.entityCutoutNoCullZOffset(identifier);
    }
}
