package dev.the_fireplace.overlord.client.renderer.feature;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import dev.the_fireplace.annotateddi.api.DIContainer;
import dev.the_fireplace.overlord.domain.data.Squads;
import dev.the_fireplace.overlord.domain.data.objects.Squad;
import dev.the_fireplace.overlord.domain.registry.PatternRegistry;
import dev.the_fireplace.overlord.entity.ArmyEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SquadElytraFeatureRenderer<T extends ArmyEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M>
{
    private static final Identifier SKIN = new Identifier("textures/entity/elytra.png");
    private final Squads squads;
    private final PatternRegistry patternRegistry;
    private final ElytraEntityModel<T> elytraModel;

    public SquadElytraFeatureRenderer(FeatureRendererContext<T, M> context, EntityModelLoader loader) {
        super(context);
        this.elytraModel = new ElytraEntityModel<>(loader.getModelPart(EntityModelLayers.ELYTRA));
        Injector injector = DIContainer.get();
        this.squads = injector.getInstance(Key.get(Squads.class, Names.named("client")));
        this.patternRegistry = injector.getInstance(PatternRegistry.class);
    }

    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T entity, float f, float g, float h, float j, float k, float l) {
        ItemStack itemStack = entity.getEquippedStack(EquipmentSlot.CHEST);
        if (entity.isInvisible() || !itemStack.isOf(Items.ELYTRA)) {
            return;
        }
        Identifier identifier = SKIN;
        if (entity.hasExistingSquad(squads)) {
            Squad squad = squads.getSquad(entity.getOwnerUuid(), entity.getSquad());
            if (squad != null) {
                identifier = patternRegistry.getById(squad.getPatternId()).getTextureLocation();
            }
        }

        matrixStack.push();
        matrixStack.translate(0.0D, 0.0D, 0.125D);
        this.getContextModel().copyStateTo(this.elytraModel);
        this.elytraModel.setAngles(entity, f, g, j, k, l);
        VertexConsumer abstractClientPlayerEntity = ItemRenderer.getArmorGlintConsumer(vertexConsumerProvider, RenderLayer.getArmorCutoutNoCull(identifier), false, itemStack.hasGlint());
        this.elytraModel.render(matrixStack, abstractClientPlayerEntity, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStack.pop();
    }
}
