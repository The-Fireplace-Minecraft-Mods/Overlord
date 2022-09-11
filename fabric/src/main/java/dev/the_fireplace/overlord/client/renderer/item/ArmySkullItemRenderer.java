package dev.the_fireplace.overlord.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.the_fireplace.overlord.block.AbstractArmySkullBlock;
import dev.the_fireplace.overlord.client.renderer.blockentity.ArmySkullBlockEntityRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.Map;

public class ArmySkullItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer
{
    private Map<AbstractArmySkullBlock.SkullType, SkullModelBase> skullModels;

    private Map<AbstractArmySkullBlock.SkullType, SkullModelBase> getSkullModels() {
        if (this.skullModels == null) {
            this.skullModels = ArmySkullBlockEntityRenderer.getModels(Minecraft.getInstance().getEntityModels());
        }

        return this.skullModels;
    }

    @Override
    public void render(ItemStack stack, ItemTransforms.TransformType mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            if (block instanceof AbstractArmySkullBlock abstractSkullBlock) {
                AbstractArmySkullBlock.SkullType skullType = abstractSkullBlock.getSkullType();
                SkullModelBase skullBlockEntityModel = this.getSkullModels().get(skullType);
                RenderType renderLayer = ArmySkullBlockEntityRenderer.getRenderLayer(skullType);
                ArmySkullBlockEntityRenderer.renderSkull(
                    null,
                    overlay,
                    180.0F,
                    0.0F,
                    matrices,
                    vertexConsumers,
                    light,
                    skullBlockEntityModel,
                    renderLayer
                );
            }
        }
    }
}
