package dev.the_fireplace.overlord.client.renderer;

import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.client.model.OwnedSkeletonModel;
import dev.the_fireplace.overlord.entity.OwnedSkeletonEntity;
import dev.the_fireplace.overlord.entity.SkeletonGrowthPhase;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorBipedFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;

@Environment(EnvType.CLIENT)
public class OwnedSkeletonRenderer extends LivingEntityRenderer<OwnedSkeletonEntity, OwnedSkeletonModel> {
    public OwnedSkeletonRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher, new OwnedSkeletonModel(), 0.5F);
        this.addFeature(new ArmorBipedFeatureRenderer<>(this, new BipedEntityModel<>(0.5F), new BipedEntityModel<>(1.0F)));
        this.addFeature(new HeldItemFeatureRenderer<>(this));
        //TODO These were designed for the player model. Remake to work with others.
        //this.addFeature(new StuckArrowsFeatureRenderer<>(this));
        //this.addFeature(new StuckStingersFeatureRenderer<>(this));
    }

    @Override
    public Identifier getTexture(OwnedSkeletonEntity entity) {
        this.shadowSize = 0.25f * entity.getGrowthPhase().ordinal() / 4.0f + 0.25f;
        if (!entity.hasSkin() && !entity.hasMuscles()) {
            return new Identifier(Overlord.MODID, "textures/entity/owned_skeleton/owned_skeleton.png");
        }
        if (entity.getGrowthPhase() == SkeletonGrowthPhase.ADULT && entity.hasSkin() && entity.getSkinsuit() != null) {
            ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
            if (networkHandler != null) {
                PlayerListEntry playerListEntry = networkHandler.getPlayerListEntry(entity.getSkinsuit());
                if (playerListEntry != null && playerListEntry.hasSkinTexture()) {
                    return playerListEntry.getSkinTexture();
                }
            }
        }
        if (entity.hasSkin() && !entity.hasMuscles())
            return new Identifier(Overlord.MODID, String.format("textures/entity/owned_skeleton/owned_skeleton_skin_%s.png", entity.getGrowthPhase()));
        else if(!entity.hasSkin() && entity.hasMuscles())
            return new Identifier(Overlord.MODID, String.format("textures/entity/owned_skeleton/owned_skeleton_muscles_%s.png", entity.getGrowthPhase()));
        else
            return new Identifier(Overlord.MODID, String.format("textures/entity/owned_skeleton/owned_skeleton_skin_muscles_%s.png", entity.getGrowthPhase()));
    }

    @Override
    protected void scale(OwnedSkeletonEntity entity, MatrixStack matrices, float tickDelta) {
        float g = (entity.getGrowthPhase().ordinal() + 1) * 0.1f + 0.5f;
        matrices.scale(g, g, g);
    }

    @Override
    public void render(OwnedSkeletonEntity livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        setModelPose(livingEntity);
        if (livingEntity.getGrowthPhase() == SkeletonGrowthPhase.ADULT && livingEntity.hasMuscles() && !this.getModel().isThicc()) {
            this.getModel().setThicc(true);
        } else if ((livingEntity.getGrowthPhase() != SkeletonGrowthPhase.ADULT || !livingEntity.hasMuscles()) && this.getModel().isThicc()) {
            this.getModel().setThicc(false);
        }
        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    private void setModelPose(OwnedSkeletonEntity entity) {
        OwnedSkeletonModel skeletonEntityModel = this.getModel();
        ItemStack mainStack = entity.getMainHandStack();
        ItemStack offStack = entity.getOffHandStack();
        skeletonEntityModel.setVisible(true);
        BipedEntityModel.ArmPose armPose = this.getArmPose(entity, mainStack, offStack, Hand.MAIN_HAND);
        BipedEntityModel.ArmPose armPose2 = this.getArmPose(entity, mainStack, offStack, Hand.OFF_HAND);
        if (entity.getMainArm() == Arm.RIGHT) {
            skeletonEntityModel.rightArmPose = armPose;
            skeletonEntityModel.leftArmPose = armPose2;
        } else {
            skeletonEntityModel.rightArmPose = armPose2;
            skeletonEntityModel.leftArmPose = armPose;
        }
    }

    private BipedEntityModel.ArmPose getArmPose(OwnedSkeletonEntity entity, ItemStack mainHandStack, ItemStack offHandStack, Hand hand) {
        BipedEntityModel.ArmPose armPose = BipedEntityModel.ArmPose.EMPTY;
        ItemStack handStack = hand == Hand.MAIN_HAND ? mainHandStack : offHandStack;
        if (!handStack.isEmpty()) {
            armPose = BipedEntityModel.ArmPose.ITEM;
            if (entity.getItemUseTimeLeft() > 0) {
                UseAction useAction = handStack.getUseAction();
                if (useAction == UseAction.BLOCK) {
                    armPose = BipedEntityModel.ArmPose.BLOCK;
                } else if (useAction == UseAction.BOW) {
                    armPose = BipedEntityModel.ArmPose.BOW_AND_ARROW;
                } else if (useAction == UseAction.SPEAR) {
                    armPose = BipedEntityModel.ArmPose.THROW_SPEAR;
                } else if (useAction == UseAction.CROSSBOW && hand == entity.getActiveHand()) {
                    armPose = BipedEntityModel.ArmPose.CROSSBOW_CHARGE;
                }
            } else {
                boolean mainHandCrossbow = mainHandStack.getItem() == Items.CROSSBOW;
                boolean isMainCrossbowCharged = CrossbowItem.isCharged(mainHandStack);
                boolean offHandCrossbow = offHandStack.getItem() == Items.CROSSBOW;
                boolean isOffHandCrossbowCharged = CrossbowItem.isCharged(offHandStack);
                if (mainHandCrossbow && isMainCrossbowCharged) {
                    armPose = BipedEntityModel.ArmPose.CROSSBOW_HOLD;
                }

                if (offHandCrossbow && isOffHandCrossbowCharged && mainHandStack.getItem().getUseAction(mainHandStack) == UseAction.NONE) {
                    armPose = BipedEntityModel.ArmPose.CROSSBOW_HOLD;
                }
            }
        }

        return armPose;
    }
}
