package dev.the_fireplace.overlord.client.renderer.feature;

import com.mojang.authlib.GameProfile;
import dev.the_fireplace.overlord.entity.OwnedSkeletonEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import org.apache.commons.lang3.StringUtils;

@Environment(EnvType.CLIENT)
public class AugmentHeadFeatureRenderer<T extends OwnedSkeletonEntity, M extends EntityModel<T> & ModelWithHead> extends FeatureRenderer<T, M>
{
    public AugmentHeadFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
    }

    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l) {
        ItemStack itemStack = livingEntity.getAugmentBlockStack();
        if (!itemStack.isEmpty()) {
            Item item = itemStack.getItem();
            matrixStack.push();

            this.getContextModel().getHead().rotate(matrixStack);
            if (item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof AbstractSkullBlock) {
                matrixStack.scale(1.1875F, -1.1875F, -1.1875F);

                GameProfile gameProfile = null;
                if (itemStack.hasTag()) {
                    CompoundTag compoundTag = itemStack.getTag();
                    //noinspection ConstantConditions
                    if (compoundTag.contains("SkullOwner", 10)) {
                        gameProfile = NbtHelper.toGameProfile(compoundTag.getCompound("SkullOwner"));
                    } else if (compoundTag.contains("SkullOwner", 8)) {
                        String string = compoundTag.getString("SkullOwner");
                        if (!StringUtils.isBlank(string)) {
                            gameProfile = SkullBlockEntity.loadProperties(new GameProfile(null, string));
                            compoundTag.put("SkullOwner", NbtHelper.fromGameProfile(new CompoundTag(), gameProfile));
                        }
                    }
                }

                matrixStack.translate(-0.5D, 0.0D, -0.5D);
                SkullBlockEntityRenderer.render(null, 180.0F, ((AbstractSkullBlock) ((BlockItem) item).getBlock()).getSkullType(), gameProfile, f, matrixStack, vertexConsumerProvider, i);
            } else if (!(item instanceof ArmorItem) || ((ArmorItem) item).getSlotType() != EquipmentSlot.HEAD) {
                matrixStack.translate(0.0D, -0.25D, 0.0D);
                matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
                //matrixStack.scale(0.625F, -0.625F, -0.625F);
                matrixStack.scale(0.5F, -0.5F, -0.5F);

                MinecraftClient.getInstance().getHeldItemRenderer().renderItem(livingEntity, itemStack, ModelTransformation.Mode.HEAD, false, matrixStack, vertexConsumerProvider, i);
            }

            matrixStack.pop();
        }
    }
}