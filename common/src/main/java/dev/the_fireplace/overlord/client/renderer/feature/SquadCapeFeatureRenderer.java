package dev.the_fireplace.overlord.client.renderer.feature;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.domain.data.Squads;
import dev.the_fireplace.overlord.domain.data.objects.Squad;
import dev.the_fireplace.overlord.domain.registry.PatternRegistry;
import dev.the_fireplace.overlord.entity.ArmyEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SquadCapeFeatureRenderer<T extends ArmyEntity, M extends PlayerModel<T>> extends RenderLayer<T, M>
{
    private final Squads squads;
    private final PatternRegistry patternRegistry;

    public SquadCapeFeatureRenderer(RenderLayerParent<T, M> context) {
        super(context);
        Injector injector = OverlordConstants.getInjector();
        this.squads = injector.getInstance(Key.get(Squads.class, Names.named("client")));
        this.patternRegistry = injector.getInstance(PatternRegistry.class);
    }

    @Override
    public void render(PoseStack matrices, MultiBufferSource vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (!entity.hasExistingSquad(squads) || entity.isInvisible()) {
            return;
        }
        ItemStack chestStack = entity.getItemBySlot(EquipmentSlot.CHEST);
        if (chestStack.getItem().equals(Items.ELYTRA)) {
            return;
        }
        Squad squad = squads.getSquad(entity.getOwnerUUID(), entity.getSquad());
        if (squad == null) {
            return;
        }
        matrices.pushPose();
        matrices.translate(0.0D, 0.0D, 0.125D);
        double d = Mth.lerp(tickDelta, entity.prevCapeX, entity.capeX) - Mth.lerp(tickDelta, entity.xo, entity.getX());
        double e = Mth.lerp(tickDelta, entity.prevCapeY, entity.capeY) - Mth.lerp(tickDelta, entity.yo, entity.getY());
        double m = Mth.lerp(tickDelta, entity.prevCapeZ, entity.capeZ) - Mth.lerp(tickDelta, entity.zo, entity.getZ());
        float n = entity.yBodyRotO + (entity.yBodyRot - entity.yBodyRotO);
        double o = Mth.sin(n * 0.017453292F);
        double p = -Mth.cos(n * 0.017453292F);
        float q = (float) e * 10.0F;
        q = Mth.clamp(q, -6.0F, 32.0F);
        float r = (float) (d * o + m * p) * 100.0F;
        r = Mth.clamp(r, 0.0F, 150.0F);
        float s = (float) (d * p - m * o) * 100.0F;
        s = Mth.clamp(s, -20.0F, 20.0F);
        if (r < 0.0F) {
            r = 0.0F;
        }

        float t = Mth.lerp(tickDelta, entity.prevStrideDistance, entity.strideDistance);
        q += Mth.sin(Mth.lerp(tickDelta, entity.walkDistO, entity.walkDist) * 6.0F) * 32.0F * t;
        if (entity.isCrouching()) {
            q += 25.0F;
        }

        matrices.mulPose(Vector3f.XP.rotationDegrees(6.0F + r / 2.0F + q));
        matrices.mulPose(Vector3f.ZP.rotationDegrees(s / 2.0F));
        matrices.mulPose(Vector3f.YP.rotationDegrees(180.0F - s / 2.0F));

        ResourceLocation squadCapeTexture = patternRegistry.getById(squad.getPatternId()).getTextureLocation();

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.entitySolid(squadCapeTexture));
        this.getParentModel().renderCloak(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY);

        matrices.pushPose();
        matrices.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
        double zTranslate = entity.getItemBySlot(EquipmentSlot.CHEST).isEmpty() ? -0.03 : -0.1;
        matrices.translate(0, -0.5, zTranslate);
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemStack itemStack = squad.getItem();
        float zScale = itemHas3DModel(itemRenderer, itemStack) ? 0.25f : 1.5f;
        matrices.scale(0.5f, 0.5f, zScale);
        itemRenderer.renderStatic(
            entity,
            itemStack,
            ItemTransforms.TransformType.FIXED,
            false,
            matrices,
            vertexConsumers,
            entity.level,
            light,
            OverlayTexture.NO_OVERLAY
        );
        matrices.popPose();
        matrices.popPose();
    }

    private boolean itemHas3DModel(ItemRenderer itemRenderer, ItemStack itemStack) {
        BakedModel model = itemRenderer.getItemModelShaper().getItemModel(itemStack);
        return model.isGui3d();
    }
}
