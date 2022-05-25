package dev.the_fireplace.overlord.client.renderer.feature;

import com.google.inject.Key;
import com.google.inject.name.Names;
import dev.the_fireplace.annotateddi.api.DIContainer;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.domain.data.Squads;
import dev.the_fireplace.overlord.domain.data.objects.Squad;
import dev.the_fireplace.overlord.entity.ArmyEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class SquadCapeFeatureRenderer<T extends ArmyEntity, M extends PlayerEntityModel<T>> extends FeatureRenderer<T, M>
{
    private final Squads squads;

    public SquadCapeFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
        this.squads = DIContainer.get().getInstance(Key.get(Squads.class, Names.named("client")));
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (!entity.hasExistingSquad() || entity.isInvisible()) {
            return;
        }
        ItemStack chestStack = entity.getEquippedStack(EquipmentSlot.CHEST);
        if (chestStack.isOf(Items.ELYTRA)) {
            return;
        }
        Squad squad = squads.getSquad(entity.getOwnerUuid(), entity.getSquad());
        if (squad == null) {
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

        Identifier squadCapeTexture = new Identifier(Overlord.MODID, "textures/entity/cape/" + squad.getPattern() + ".png");

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(squadCapeTexture));
        this.getContextModel().renderCape(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);

        matrices.push();
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F));
        double zTranslate = entity.getEquippedStack(EquipmentSlot.CHEST).isEmpty() ? -0.03 : -0.1;
        matrices.translate(0, -0.5, zTranslate);
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        ItemStack itemStack = squad.getItem();
        float zScale = itemHas3DModel(itemRenderer, itemStack) ? 0.25f : 1.5f;
        matrices.scale(0.5f, 0.5f, zScale);
        itemRenderer.renderItem(
            entity,
            itemStack,
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

    private boolean itemHas3DModel(ItemRenderer itemRenderer, ItemStack itemStack) {
        BakedModel model = itemRenderer.getModels().getModel(itemStack);
        return model.hasDepth();
    }
}
