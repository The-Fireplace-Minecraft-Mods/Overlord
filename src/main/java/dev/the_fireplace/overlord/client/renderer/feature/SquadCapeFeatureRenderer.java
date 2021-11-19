package dev.the_fireplace.overlord.client.renderer.feature;

import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.entity.ArmyEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public class SquadCapeFeatureRenderer<T extends ArmyEntity, M extends PlayerEntityModel<T>> extends FeatureRenderer<T, M>
{
    public SquadCapeFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (!entity.hasSquad() || entity.isInvisible()) {
            return;
        }
        ItemStack chestStack = entity.getEquippedStack(EquipmentSlot.CHEST);
        if (chestStack.isOf(Items.ELYTRA)) {
            return;
        }
        matrices.push();
        matrices.translate(0.0D, 0.0D, 0.125D);
        double d = MathHelper.lerp(tickDelta, entity.prevCapeX, entity.capeX) - MathHelper.lerp(tickDelta, entity.prevX, entity.getX());
        double e = MathHelper.lerp(tickDelta, entity.prevCapeY, entity.capeY) - MathHelper.lerp(tickDelta, entity.prevY, entity.getY());
        double m = MathHelper.lerp(tickDelta, entity.prevCapeZ, entity.capeZ) - MathHelper.lerp(tickDelta, entity.prevZ, entity.getZ());
        float n = entity.prevBodyYaw + (entity.bodyYaw - entity.prevBodyYaw);
        double o = MathHelper.sin(n * 0.017453292F);
        double p = -MathHelper.cos(n * 0.017453292F);
        float q = (float) e * 10.0F;
        q = MathHelper.clamp(q, -6.0F, 32.0F);
        float r = (float) (d * o + m * p) * 100.0F;
        r = MathHelper.clamp(r, 0.0F, 150.0F);
        float s = (float) (d * p - m * o) * 100.0F;
        s = MathHelper.clamp(s, -20.0F, 20.0F);
        if (r < 0.0F) {
            r = 0.0F;
        }

        float t = MathHelper.lerp(tickDelta, entity.prevStrideDistance, entity.strideDistance);
        q += MathHelper.sin(MathHelper.lerp(tickDelta, entity.prevHorizontalSpeed, entity.horizontalSpeed) * 6.0F) * 32.0F * t;
        if (entity.isInSneakingPose()) {
            q += 25.0F;
        }

        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(6.0F + r / 2.0F + q));
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(s / 2.0F));
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F - s / 2.0F));

        Identifier squadCapeTexture = new Identifier(Overlord.MODID, "textures/entity/cape/red_bed_cape.png");

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(squadCapeTexture));
        this.getContextModel().renderCape(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);

        matrices.push();
        matrices.translate(0, 0.5, -0.03);
        matrices.scale(0.5f, 0.5f, 1.5f);
        ItemStack squadItem = new ItemStack(Items.NETHERITE_AXE);
        MinecraftClient.getInstance().getItemRenderer().renderItem(
            entity,
            squadItem,
            ModelTransformation.Mode.FIXED,
            false,
            matrices,
            vertexConsumers,
            entity.world,
            light,
            OverlayTexture.DEFAULT_UV,
            0
        );
        matrices.pop();
        matrices.pop();
    }
}
