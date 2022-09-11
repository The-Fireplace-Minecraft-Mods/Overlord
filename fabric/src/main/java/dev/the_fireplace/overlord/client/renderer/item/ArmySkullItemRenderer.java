package dev.the_fireplace.overlord.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.the_fireplace.overlord.block.AbstractArmySkullBlock;
import dev.the_fireplace.overlord.client.renderer.blockentity.ArmySkullBlockEntityRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRenderer;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.Map;

public class ArmySkullItemRenderer implements BuiltinItemRenderer
{
    private Map<AbstractArmySkullBlock.SkullType, SkullModel> skullModels;

    private Map<AbstractArmySkullBlock.SkullType, SkullModel> getSkullModels() {
        if (this.skullModels == null) {
            this.skullModels = ArmySkullBlockEntityRenderer.getModels();
        }

        return this.skullModels;
    }

    @Override
    public void render(ItemStack stack, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        if (stack.getItem() instanceof BlockItem) {
            Block block = ((BlockItem) stack.getItem()).getBlock();
            if (block instanceof AbstractArmySkullBlock) {
                AbstractArmySkullBlock.SkullType skullType = ((AbstractArmySkullBlock) block).getSkullType();
                SkullModel skullBlockEntityModel = this.getSkullModels().get(skullType);
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
