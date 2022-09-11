package dev.the_fireplace.overlord.client.renderer.feature;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.domain.data.Squads;
import dev.the_fireplace.overlord.domain.data.objects.Squad;
import dev.the_fireplace.overlord.domain.registry.PatternRegistry;
import dev.the_fireplace.overlord.entity.ArmyEntity;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SquadElytraFeatureRenderer<T extends ArmyEntity, M extends EntityModel<T>> extends RenderLayer<T, M>
{
    private static final ResourceLocation SKIN = new ResourceLocation("textures/entity/elytra.png");
    private final Squads squads;
    private final PatternRegistry patternRegistry;
    private final ElytraModel<T> elytraModel;

    public SquadElytraFeatureRenderer(RenderLayerParent<T, M> context) {
        super(context);
        this.elytraModel = new ElytraModel<>();
        Injector injector = OverlordConstants.getInjector();
        this.squads = injector.getInstance(Key.get(Squads.class, Names.named("client")));
        this.patternRegistry = injector.getInstance(PatternRegistry.class);
    }

    public void render(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, T entity, float f, float g, float h, float j, float k, float l) {
        ItemStack itemStack = entity.getItemBySlot(EquipmentSlot.CHEST);
        if (entity.isInvisible() || !itemStack.getItem().equals(Items.ELYTRA)) {
            return;
        }
        ResourceLocation identifier = SKIN;
        if (entity.hasExistingSquad(squads)) {
            Squad squad = squads.getSquad(entity.getOwnerUUID(), entity.getSquad());
            if (squad != null) {
                identifier = patternRegistry.getById(squad.getPatternId()).getTextureLocation();
            }
        }

        matrixStack.pushPose();
        matrixStack.translate(0.0D, 0.0D, 0.125D);
        this.getParentModel().copyPropertiesTo(this.elytraModel);
        this.elytraModel.setupAnim(entity, f, g, j, k, l);
        VertexConsumer abstractClientPlayerEntity = ItemRenderer.getArmorFoilBuffer(vertexConsumerProvider, RenderType.armorCutoutNoCull(identifier), false, itemStack.hasFoil());
        this.elytraModel.renderToBuffer(matrixStack, abstractClientPlayerEntity, i, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStack.popPose();
    }
}
