package dev.the_fireplace.overlord.client.renderer.item;

import dev.the_fireplace.overlord.block.AbstractArmySkullBlock;
import dev.the_fireplace.overlord.client.renderer.blockentity.ArmySkullBlockEntityRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

import java.util.Map;

public class ArmySkullItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer
{
    private Map<AbstractArmySkullBlock.SkullType, SkullBlockEntityModel> skullModels;

    private Map<AbstractArmySkullBlock.SkullType, SkullBlockEntityModel> getSkullModels() {
        if (this.skullModels == null) {
            this.skullModels = ArmySkullBlockEntityRenderer.getModels(MinecraftClient.getInstance().getEntityModelLoader());
        }

        return this.skullModels;
    }

    @Override
    public void render(ItemStack stack, ModelTransformation.Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            if (block instanceof AbstractArmySkullBlock abstractSkullBlock) {
                AbstractArmySkullBlock.SkullType skullType = abstractSkullBlock.getSkullType();
                SkullBlockEntityModel skullBlockEntityModel = this.getSkullModels().get(skullType);
                RenderLayer renderLayer = ArmySkullBlockEntityRenderer.getRenderLayer(skullType);
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
