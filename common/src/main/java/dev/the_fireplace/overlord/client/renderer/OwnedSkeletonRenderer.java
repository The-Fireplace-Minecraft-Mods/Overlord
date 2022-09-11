package dev.the_fireplace.overlord.client.renderer;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.the_fireplace.lib.api.uuid.injectables.EmptyUUID;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.client.model.OverlordModelLayers;
import dev.the_fireplace.overlord.client.model.OwnedSkeletonModel;
import dev.the_fireplace.overlord.client.renderer.feature.AugmentHeadFeatureRenderer;
import dev.the_fireplace.overlord.client.renderer.feature.SquadCapeFeatureRenderer;
import dev.the_fireplace.overlord.client.renderer.feature.SquadElytraFeatureRenderer;
import dev.the_fireplace.overlord.entity.OwnedSkeletonEntity;
import dev.the_fireplace.overlord.entity.SkeletonGrowthPhase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.BeeStingerLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket.PlayerUpdate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.GameType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OwnedSkeletonRenderer extends HumanoidMobRenderer<OwnedSkeletonEntity, OwnedSkeletonModel>
{
    private final EmptyUUID emptyUUID;

    private final Map<UUID, PlayerInfo> skinCache;

    private final OwnedSkeletonModel thinSkinMuscleModel;
    private final OwnedSkeletonModel muscleModel;
    private final OwnedSkeletonModel thinSkinModel;
    private final OwnedSkeletonModel standardModel;

    private final HumanoidArmorLayer<OwnedSkeletonEntity, OwnedSkeletonModel, HumanoidModel<OwnedSkeletonEntity>> standardArmorRenderer;
    private final HumanoidArmorLayer<OwnedSkeletonEntity, OwnedSkeletonModel, HumanoidModel<OwnedSkeletonEntity>> muscleArmorRenderer;

    private HumanoidArmorLayer<OwnedSkeletonEntity, OwnedSkeletonModel, HumanoidModel<OwnedSkeletonEntity>> currentArmorRenderer;
    private HumanoidArmorLayer<OwnedSkeletonEntity, OwnedSkeletonModel, HumanoidModel<OwnedSkeletonEntity>> previousArmorRenderer;

    public OwnedSkeletonRenderer(EntityRendererProvider.Context context) {
        super(context, new OwnedSkeletonModel(context.bakeLayer(OverlordModelLayers.OWNED_SKELETON_MODEL)), 0.5F);

        this.emptyUUID = OverlordConstants.getInjector().getInstance(EmptyUUID.class);

        this.standardModel = new OwnedSkeletonModel(context.bakeLayer(OverlordModelLayers.OWNED_SKELETON_MODEL));
        this.muscleModel = new OwnedSkeletonModel(context.bakeLayer(OverlordModelLayers.MUSCLE_OWNED_SKELETON_MODEL));
        this.thinSkinModel = new OwnedSkeletonModel(context.bakeLayer(OverlordModelLayers.SLIM_OWNED_SKELETON_MODEL));
        this.thinSkinMuscleModel = new OwnedSkeletonModel(context.bakeLayer(OverlordModelLayers.SLIM_MUSCLE_OWNED_SKELETON_MODEL));

        HumanoidModel<OwnedSkeletonEntity> bodyModel = new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR));
        OwnedSkeletonModel standardLeggingsModel = new OwnedSkeletonModel(context.bakeLayer(OverlordModelLayers.OWNED_SKELETON_LEGGINGS_MODEL));
        this.standardArmorRenderer = new HumanoidArmorLayer<>(this, standardLeggingsModel, bodyModel);
        OwnedSkeletonModel muscleLeggingsModel = new OwnedSkeletonModel(context.bakeLayer(OverlordModelLayers.MUSCLE_OWNED_SKELETON_LEGGINGS_MODEL));
        this.muscleArmorRenderer = new HumanoidArmorLayer<>(this, muscleLeggingsModel, bodyModel);

        this.addLayer(this.standardArmorRenderer);
        this.addLayer(new AugmentHeadFeatureRenderer<>(this, context.getModelSet()));
        this.addLayer(new SquadCapeFeatureRenderer<>(this));
        this.addLayer(new SquadElytraFeatureRenderer<>(this, context.getModelSet()));
        this.addLayer(new ArrowLayer<>(context, this));
        this.addLayer(new BeeStingerLayer<>(this));

        this.skinCache = new HashMap<>();
        this.currentArmorRenderer = this.standardArmorRenderer;
        this.previousArmorRenderer = this.standardArmorRenderer;
    }

    @Override
    public ResourceLocation getTextureLocation(OwnedSkeletonEntity entity) {
        this.shadowRadius = 0.25f * entity.getGrowthPhase().ordinal() / 4.0f + 0.25f;
        if (!entity.hasSkin() && !entity.hasMuscles()) {
            return new ResourceLocation(OverlordConstants.MODID, "textures/entity/owned_skeleton/owned_skeleton.png");
        }
        UUID skinsuit = entity.getSkinsuit();
        if (entity.getGrowthPhase() == SkeletonGrowthPhase.ADULT && entity.hasSkin() && !emptyUUID.is(skinsuit)) {
            cacheSkinsuitTexture(entity);
            if (skinCache.containsKey(skinsuit)) {
                return skinCache.get(skinsuit).getSkinLocation();
            }
        }
        if (entity.hasSkin() && !entity.hasMuscles()) {
            return new ResourceLocation(OverlordConstants.MODID, String.format("textures/entity/owned_skeleton/owned_skeleton_skin_%s.png", entity.getGrowthPhase().ordinal()));
        } else if (!entity.hasSkin() && entity.hasMuscles()) {
            return new ResourceLocation(OverlordConstants.MODID, String.format("textures/entity/owned_skeleton/owned_skeleton_muscles_%s.png", entity.getGrowthPhase().ordinal()));
        } else {
            return new ResourceLocation(OverlordConstants.MODID, String.format("textures/entity/owned_skeleton/owned_skeleton_skin_muscles_%s.png", entity.getGrowthPhase().ordinal()));
        }
    }

    private void cacheSkinsuitTexture(OwnedSkeletonEntity entity) {
        UUID skinsuit = entity.getSkinsuit();
        if (!skinCache.containsKey(skinsuit)) {
            ClientPacketListener networkHandler = Minecraft.getInstance().getConnection();
            if (networkHandler == null) {
                return;
            }
            GameProfile gameProfile = new GameProfile(skinsuit, null);
            skinCache.put(skinsuit, new PlayerInfo(new PlayerUpdate(
                gameProfile,
                0,
                GameType.SURVIVAL,
                null
            )));
        }
    }

    @Override
    protected void scale(OwnedSkeletonEntity entity, PoseStack matrices, float tickDelta) {
        float shrinkMultiplier = (entity.getGrowthPhase().ordinal() + 1) * 0.1f + 0.5f;
        matrices.scale(shrinkMultiplier, shrinkMultiplier, shrinkMultiplier);
    }

    @Override
    public void render(OwnedSkeletonEntity livingEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i) {
        setModel(livingEntity);
        setModelPose(livingEntity);
        ItemStack augment = livingEntity.getAugmentBlockStack();
        this.getModel().head.visible = augment.isEmpty();
        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    private void setModel(OwnedSkeletonEntity entity) {
        boolean hasThinArmTexture = skinCache.containsKey(entity.getSkinsuit()) && skinCache.get(entity.getSkinsuit()).getModelName().equals("slim");
        boolean hasThickLimbs = entity.getGrowthPhase() == SkeletonGrowthPhase.ADULT && entity.hasMuscles();
        if (hasThinArmTexture && hasThickLimbs) {
            this.model = this.thinSkinMuscleModel;
            this.currentArmorRenderer = this.muscleArmorRenderer;
        } else if (hasThinArmTexture) {
            this.model = this.thinSkinModel;
            this.currentArmorRenderer = this.standardArmorRenderer;
        } else if (hasThickLimbs) {
            this.model = this.muscleModel;
            this.currentArmorRenderer = this.muscleArmorRenderer;
        } else {
            this.model = this.standardModel;
            this.currentArmorRenderer = this.standardArmorRenderer;
        }
        if (this.previousArmorRenderer != this.currentArmorRenderer) {
            this.layers.remove(this.previousArmorRenderer);
            this.addLayer(currentArmorRenderer);
            this.previousArmorRenderer = this.currentArmorRenderer;
        }
    }

    private void setModelPose(OwnedSkeletonEntity entity) {
        OwnedSkeletonModel skeletonEntityModel = this.getModel();
        ItemStack mainStack = entity.getMainHandItem();
        ItemStack offStack = entity.getOffhandItem();
        skeletonEntityModel.setAllVisible(true);
        HumanoidModel.ArmPose armPose = this.getArmPose(entity, mainStack, offStack, InteractionHand.MAIN_HAND);
        HumanoidModel.ArmPose armPose2 = this.getArmPose(entity, mainStack, offStack, InteractionHand.OFF_HAND);
        if (entity.getMainArm() == HumanoidArm.RIGHT) {
            skeletonEntityModel.rightArmPose = armPose;
            skeletonEntityModel.leftArmPose = armPose2;
        } else {
            skeletonEntityModel.rightArmPose = armPose2;
            skeletonEntityModel.leftArmPose = armPose;
        }
    }

    private HumanoidModel.ArmPose getArmPose(OwnedSkeletonEntity entity, ItemStack mainHandStack, ItemStack offHandStack, InteractionHand hand) {
        HumanoidModel.ArmPose armPose = HumanoidModel.ArmPose.EMPTY;
        ItemStack handStack = hand == InteractionHand.MAIN_HAND ? mainHandStack : offHandStack;
        if (!handStack.isEmpty()) {
            armPose = HumanoidModel.ArmPose.ITEM;
            if (entity.getUseItemRemainingTicks() > 0) {
                UseAnim useAction = handStack.getUseAnimation();
                if (useAction == UseAnim.BLOCK) {
                    armPose = HumanoidModel.ArmPose.BLOCK;
                } else if (useAction == UseAnim.BOW) {
                    armPose = HumanoidModel.ArmPose.BOW_AND_ARROW;
                } else if (useAction == UseAnim.SPEAR) {
                    armPose = HumanoidModel.ArmPose.THROW_SPEAR;
                } else if (useAction == UseAnim.CROSSBOW && hand == entity.getUsedItemHand()) {
                    armPose = HumanoidModel.ArmPose.CROSSBOW_CHARGE;
                }
            } else {
                boolean mainHandCrossbow = mainHandStack.getItem() == Items.CROSSBOW;
                boolean isMainCrossbowCharged = CrossbowItem.isCharged(mainHandStack);
                boolean offHandCrossbow = offHandStack.getItem() == Items.CROSSBOW;
                boolean isOffHandCrossbowCharged = CrossbowItem.isCharged(offHandStack);
                if (mainHandCrossbow && isMainCrossbowCharged) {
                    armPose = HumanoidModel.ArmPose.CROSSBOW_HOLD;
                }

                if (offHandCrossbow && isOffHandCrossbowCharged && mainHandStack.getItem().getUseAnimation(mainHandStack) == UseAnim.NONE) {
                    armPose = HumanoidModel.ArmPose.CROSSBOW_HOLD;
                }
            }
        }

        return armPose;
    }
}
