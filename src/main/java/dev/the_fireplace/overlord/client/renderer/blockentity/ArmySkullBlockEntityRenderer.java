package dev.the_fireplace.overlord.client.renderer.blockentity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.block.AbstractArmySkullBlock;
import dev.the_fireplace.overlord.block.ArmySkullBlock;
import dev.the_fireplace.overlord.block.WallArmySkullBlock;
import dev.the_fireplace.overlord.blockentity.ArmySkullBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.SkullEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class ArmySkullBlockEntityRenderer implements BlockEntityRenderer<ArmySkullBlockEntity>
{
    private final Map<AbstractArmySkullBlock.SkullType, SkullBlockEntityModel> MODELS;
    private static final Map<AbstractArmySkullBlock.SkullType, Identifier> TEXTURES = Util.make(Maps.newHashMap(), (map) -> {
        map.put(AbstractArmySkullBlock.SkullType.MUSCLE_SKELETON, new Identifier(Overlord.MODID, "textures/entity/owned_skeleton/owned_skeleton_muscles_4.png"));
        map.put(AbstractArmySkullBlock.SkullType.MUSCLE_SKIN_SKELETON, new Identifier(Overlord.MODID, "textures/entity/owned_skeleton/owned_skeleton_skin_muscles_4.png"));
        map.put(AbstractArmySkullBlock.SkullType.SKIN_SKELETON, new Identifier(Overlord.MODID, "textures/entity/owned_skeleton/owned_skeleton_skin_4.png"));
    });

    public static Map<AbstractArmySkullBlock.SkullType, SkullBlockEntityModel> getModels(EntityModelLoader modelLoader) {
        ImmutableMap.Builder<AbstractArmySkullBlock.SkullType, SkullBlockEntityModel> builder = ImmutableMap.builder();
        builder.put(AbstractArmySkullBlock.SkullType.MUSCLE_SKELETON, new SkullEntityModel(modelLoader.getModelPart(EntityModelLayers.PLAYER_HEAD)));
        builder.put(AbstractArmySkullBlock.SkullType.MUSCLE_SKIN_SKELETON, new SkullEntityModel(modelLoader.getModelPart(EntityModelLayers.PLAYER_HEAD)));
        builder.put(AbstractArmySkullBlock.SkullType.SKIN_SKELETON, new SkullEntityModel(modelLoader.getModelPart(EntityModelLayers.PLAYER_HEAD)));
        return builder.build();
    }

    public ArmySkullBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.MODELS = getModels(ctx.getLayerRenderDispatcher());
    }

    @Override
    public void render(ArmySkullBlockEntity skullBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        BlockState cachedState = skullBlockEntity.getCachedState();
        boolean isWall = cachedState.getBlock() instanceof WallArmySkullBlock;
        Direction direction = isWall ? cachedState.get(WallArmySkullBlock.FACING) : null;
        float yaw = 22.5F * (float) (isWall ? (2 + direction.getHorizontal()) * 4 : cachedState.get(ArmySkullBlock.ROTATION));
        AbstractArmySkullBlock.SkullType skullType = ((AbstractArmySkullBlock) cachedState.getBlock()).getSkullType();
        SkullBlockEntityModel skullBlockEntityModel = this.MODELS.get(skullType);
        RenderLayer renderLayer = getRenderLayer(skullType);
        renderSkull(direction, OverlayTexture.DEFAULT_UV, yaw, 0, matrixStack, vertexConsumerProvider, i, skullBlockEntityModel, renderLayer);
    }

    public static void renderSkull(@Nullable Direction direction, int overlay, float yaw, float animationProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, SkullBlockEntityModel model, RenderLayer renderLayer) {
        matrices.push();
        if (direction == null) {
            matrices.translate(0.5, 0.0, 0.5);
        } else {
            matrices.translate(0.5F - (float) direction.getOffsetX() * 0.25F, 0.25, 0.5F - (float) direction.getOffsetZ() * 0.25F);
        }

        matrices.scale(-1.0F, -1.0F, 1.0F);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);
        model.setHeadRotation(animationProgress, yaw, 0.0F);
        model.render(matrices, vertexConsumer, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
        matrices.pop();
    }

    public static RenderLayer getRenderLayer(AbstractArmySkullBlock.SkullType type) {
        Identifier identifier = TEXTURES.get(type);
        return RenderLayer.getEntityCutoutNoCullZOffset(identifier);
    }
}
