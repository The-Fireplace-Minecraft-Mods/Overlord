package dev.the_fireplace.overlord.client.renderer.feature;

import com.mojang.authlib.GameProfile;
import dev.the_fireplace.overlord.entity.OwnedSkeletonEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.Vec3f;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class AugmentHeadFeatureRenderer<T extends OwnedSkeletonEntity, M extends EntityModel<T> & ModelWithHead> extends FeatureRenderer<T, M>
{
    private final Map<SkullBlock.SkullType, SkullBlockEntityModel> headModels;

    public AugmentHeadFeatureRenderer(FeatureRendererContext<T, M> context, EntityModelLoader loader) {
        super(context);
        this.headModels = SkullBlockEntityRenderer.getModels(loader);
    }

    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, T livingEntity, float f, float g, float h, float j, float k, float l) {
        ItemStack itemStack = livingEntity.getAugmentBlockStack();
        if (!itemStack.isEmpty()) {
            Item item = itemStack.getItem();
            matrixStack.push();

            // overlay makes it flash red when hurt, etc.
            int overlay = LivingEntityRenderer.getOverlay(livingEntity, 0.0F);

            this.getContextModel().getHead().rotate(matrixStack);
            if (item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof AbstractSkullBlock) {
                matrixStack.scale(1.1875F, -1.1875F, -1.1875F);

                GameProfile gameProfile = null;
                if (itemStack.hasNbt()) {
                    NbtCompound nbtCompound = itemStack.getNbt();
                    //noinspection ConstantConditions
                    if (nbtCompound.contains("SkullOwner", 10)) {
                        gameProfile = NbtHelper.toGameProfile(nbtCompound.getCompound("SkullOwner"));
                    }
                }

                matrixStack.translate(-0.5D, 0.0D, -0.5D);
                SkullBlock.SkullType skullType = ((AbstractSkullBlock) ((BlockItem) item).getBlock()).getSkullType();
                SkullBlockEntityModel skullBlockEntityModel = this.headModels.get(skullType);
                RenderLayer renderLayer = SkullBlockEntityRenderer.getRenderLayer(skullType, gameProfile);
                renderSkull(overlay, 180.0F, f, matrixStack, vertexConsumerProvider, light, skullBlockEntityModel, renderLayer);
            } else if (!(item instanceof ArmorItem) || ((ArmorItem) item).getSlotType() != EquipmentSlot.HEAD) {
                matrixStack.translate(0.0D, -0.25D, 0.0D);
                matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
                matrixStack.scale(0.5F, -0.5F, -0.5F);

                MinecraftClient.getInstance().getItemRenderer().renderItem(
                    livingEntity,
                    itemStack,
                    ModelTransformation.Mode.HEAD,
                    false,
                    matrixStack,
                    vertexConsumerProvider,
                    livingEntity.world,
                    light,
                    overlay,
                    0
                );
            }

            matrixStack.pop();
        }
    }

    /**
     * Modified version to take a custom overlay
     * {@link SkullBlockEntityRenderer#renderSkull}
     */
    public static void renderSkull(int overlay, float yaw, float animationProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, SkullBlockEntityModel model, RenderLayer renderLayer) {
        matrices.push();
        matrices.translate(0.5D, 0.0D, 0.5D);

        matrices.scale(-1.0F, -1.0F, 1.0F);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);
        model.setHeadRotation(animationProgress, yaw, 0.0F);
        model.render(matrices, vertexConsumer, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
        matrices.pop();
    }
}
