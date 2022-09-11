package dev.the_fireplace.overlord.client.renderer.feature;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import dev.the_fireplace.overlord.block.AbstractArmySkullBlock;
import dev.the_fireplace.overlord.client.renderer.blockentity.ArmySkullBlockEntityRenderer;
import dev.the_fireplace.overlord.entity.OwnedSkeletonEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.SkullBlock;

import java.util.Map;

public class AugmentHeadFeatureRenderer<T extends OwnedSkeletonEntity, M extends EntityModel<T> & HeadedModel> extends RenderLayer<T, M>
{
    private final Map<SkullBlock.Type, SkullModelBase> headModels;
    private final Map<AbstractArmySkullBlock.SkullType, SkullModelBase> armyHeadModels;

    public AugmentHeadFeatureRenderer(RenderLayerParent<T, M> context, EntityModelSet loader) {
        super(context);
        this.headModels = SkullBlockRenderer.createSkullRenderers(loader);
        this.armyHeadModels = ArmySkullBlockEntityRenderer.getModels(loader);
    }

    public void render(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, T livingEntity, float f, float g, float h, float j, float k, float l) {
        ItemStack itemStack = livingEntity.getAugmentBlockStack();
        if (!itemStack.isEmpty()) {
            Item item = itemStack.getItem();
            matrixStack.pushPose();

            // overlay makes it flash red when hurt, etc.
            int overlay = LivingEntityRenderer.getOverlayCoords(livingEntity, 0.0F);

            this.getParentModel().getHead().translateAndRotate(matrixStack);
            if (item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof AbstractSkullBlock) {
                matrixStack.scale(1.1875F, -1.1875F, -1.1875F);

                GameProfile gameProfile = null;
                if (itemStack.hasTag()) {
                    CompoundTag nbtCompound = itemStack.getTag();
                    //noinspection ConstantConditions
                    if (nbtCompound.contains("SkullOwner", 10)) {
                        gameProfile = NbtUtils.readGameProfile(nbtCompound.getCompound("SkullOwner"));
                    }
                }

                matrixStack.translate(-0.5D, 0.0D, -0.5D);
                SkullBlock.Type skullType = ((AbstractSkullBlock) ((BlockItem) item).getBlock()).getType();
                SkullModelBase skullBlockEntityModel = this.headModels.get(skullType);
                RenderType renderLayer = SkullBlockRenderer.getRenderType(skullType, gameProfile);
                renderVanillaSkull(overlay, 180.0F, f, matrixStack, vertexConsumerProvider, light, skullBlockEntityModel, renderLayer);
            } else if (item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof AbstractArmySkullBlock) {
                matrixStack.scale(1.1875F, -1.1875F, -1.1875F);

                matrixStack.translate(-0.5D, 0.0D, -0.5D);
                AbstractArmySkullBlock.SkullType skullType = ((AbstractArmySkullBlock) ((BlockItem) item).getBlock()).getSkullType();
                SkullModelBase skullBlockEntityModel = this.armyHeadModels.get(skullType);
                RenderType renderLayer = ArmySkullBlockEntityRenderer.getRenderLayer(skullType);
                ArmySkullBlockEntityRenderer.renderSkull(null, overlay, 180.0F, f, matrixStack, vertexConsumerProvider, light, skullBlockEntityModel, renderLayer);
            } else if (!(item instanceof ArmorItem) || ((ArmorItem) item).getSlot() != EquipmentSlot.HEAD) {
                matrixStack.translate(0.0D, -0.25D, 0.0D);
                matrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                matrixStack.scale(0.5F, -0.5F, -0.5F);

                Minecraft.getInstance().getItemRenderer().renderStatic(
                    livingEntity,
                    itemStack,
                    ItemTransforms.TransformType.HEAD,
                    false,
                    matrixStack,
                    vertexConsumerProvider,
                    livingEntity.level,
                    light,
                    overlay,
                    0
                );
            }

            matrixStack.popPose();
        }
    }

    /**
     * Modified version to take a custom overlay
     * {@link SkullBlockRenderer#renderSkull}
     */
    public static void renderVanillaSkull(int overlay, float yaw, float animationProgress, PoseStack matrices, MultiBufferSource vertexConsumers, int light, SkullModelBase model, RenderType renderLayer) {
        matrices.pushPose();
        matrices.translate(0.5D, 0.0D, 0.5D);

        matrices.scale(-1.0F, -1.0F, 1.0F);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);
        model.setupAnim(animationProgress, yaw, 0.0F);
        model.renderToBuffer(matrices, vertexConsumer, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
        matrices.popPose();
    }
}
