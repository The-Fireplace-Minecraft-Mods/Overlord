package dev.the_fireplace.overlord.client.renderer;

import com.mojang.authlib.GameProfile;
import dev.the_fireplace.annotateddi.api.DIContainer;
import dev.the_fireplace.lib.api.uuid.injectables.EmptyUUID;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.client.model.OverlordModelLayers;
import dev.the_fireplace.overlord.client.model.OwnedSkeletonModel;
import dev.the_fireplace.overlord.client.renderer.feature.AugmentHeadFeatureRenderer;
import dev.the_fireplace.overlord.client.renderer.feature.SquadCapeFeatureRenderer;
import dev.the_fireplace.overlord.entity.OwnedSkeletonEntity;
import dev.the_fireplace.overlord.entity.SkeletonGrowthPhase;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckStingersFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Entry;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;
import net.minecraft.world.GameMode;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class OwnedSkeletonRenderer extends BipedEntityRenderer<OwnedSkeletonEntity, OwnedSkeletonModel>
{
    private final EmptyUUID emptyUUID;

    private final Map<UUID, PlayerListEntry> skinCache;

    private final OwnedSkeletonModel thinSkinMuscleModel;
    private final OwnedSkeletonModel muscleModel;
    private final OwnedSkeletonModel thinSkinModel;
    private final OwnedSkeletonModel standardModel;

    private final ArmorFeatureRenderer<OwnedSkeletonEntity, OwnedSkeletonModel, BipedEntityModel<OwnedSkeletonEntity>> standardArmorRenderer;
    private final ArmorFeatureRenderer<OwnedSkeletonEntity, OwnedSkeletonModel, BipedEntityModel<OwnedSkeletonEntity>> muscleArmorRenderer;

    private ArmorFeatureRenderer<OwnedSkeletonEntity, OwnedSkeletonModel, BipedEntityModel<OwnedSkeletonEntity>> currentArmorRenderer;
    private ArmorFeatureRenderer<OwnedSkeletonEntity, OwnedSkeletonModel, BipedEntityModel<OwnedSkeletonEntity>> previousArmorRenderer;

    public OwnedSkeletonRenderer(EntityRendererFactory.Context context) {
        super(context, new OwnedSkeletonModel(context.getPart(OverlordModelLayers.OWNED_SKELETON_MODEL)), 0.5F);

        this.emptyUUID = DIContainer.get().getInstance(EmptyUUID.class);

        this.standardModel = new OwnedSkeletonModel(context.getPart(OverlordModelLayers.OWNED_SKELETON_MODEL));
        this.muscleModel = new OwnedSkeletonModel(context.getPart(OverlordModelLayers.MUSCLE_OWNED_SKELETON_MODEL));
        this.thinSkinModel = new OwnedSkeletonModel(context.getPart(OverlordModelLayers.SLIM_OWNED_SKELETON_MODEL));
        this.thinSkinMuscleModel = new OwnedSkeletonModel(context.getPart(OverlordModelLayers.SLIM_MUSCLE_OWNED_SKELETON_MODEL));

        BipedEntityModel<OwnedSkeletonEntity> bodyModel = new BipedEntityModel<>(context.getPart(EntityModelLayers.PLAYER_INNER_ARMOR));
        OwnedSkeletonModel standardLeggingsModel = new OwnedSkeletonModel(context.getPart(OverlordModelLayers.OWNED_SKELETON_LEGGINGS_MODEL));
        this.standardArmorRenderer = new ArmorFeatureRenderer<>(this, standardLeggingsModel, bodyModel);
        OwnedSkeletonModel muscleLeggingsModel = new OwnedSkeletonModel(context.getPart(OverlordModelLayers.MUSCLE_OWNED_SKELETON_LEGGINGS_MODEL));
        this.muscleArmorRenderer = new ArmorFeatureRenderer<>(this, muscleLeggingsModel, bodyModel);

        this.addFeature(this.standardArmorRenderer);
        this.addFeature(new AugmentHeadFeatureRenderer<>(this, context.getModelLoader()));
        this.addFeature(new SquadCapeFeatureRenderer<>(this));
        this.addFeature(new StuckArrowsFeatureRenderer<>(context, this));
        this.addFeature(new StuckStingersFeatureRenderer<>(this));

        this.skinCache = new HashMap<>();
        this.currentArmorRenderer = this.standardArmorRenderer;
        this.previousArmorRenderer = this.standardArmorRenderer;
    }

    @Override
    public Identifier getTexture(OwnedSkeletonEntity entity) {
        this.shadowRadius = 0.25f * entity.getGrowthPhase().ordinal() / 4.0f + 0.25f;
        if (!entity.hasSkin() && !entity.hasMuscles()) {
            return new Identifier(Overlord.MODID, "textures/entity/owned_skeleton/owned_skeleton.png");
        }
        UUID skinsuit = entity.getSkinsuit();
        if (entity.getGrowthPhase() == SkeletonGrowthPhase.ADULT && entity.hasSkin() && !emptyUUID.is(skinsuit)) {
            cacheSkinsuitTexture(entity);
            if (skinCache.containsKey(skinsuit)) {
                return skinCache.get(skinsuit).getSkinTexture();
            }
        }
        if (entity.hasSkin() && !entity.hasMuscles()) {
            return new Identifier(Overlord.MODID, String.format("textures/entity/owned_skeleton/owned_skeleton_skin_%s.png", entity.getGrowthPhase().ordinal()));
        } else if (!entity.hasSkin() && entity.hasMuscles()) {
            return new Identifier(Overlord.MODID, String.format("textures/entity/owned_skeleton/owned_skeleton_muscles_%s.png", entity.getGrowthPhase().ordinal()));
        } else {
            return new Identifier(Overlord.MODID, String.format("textures/entity/owned_skeleton/owned_skeleton_skin_muscles_%s.png", entity.getGrowthPhase().ordinal()));
        }
    }

    private void cacheSkinsuitTexture(OwnedSkeletonEntity entity) {
        UUID skinsuit = entity.getSkinsuit();
        if (!skinCache.containsKey(skinsuit)) {
            ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
            if (networkHandler == null) {
                return;
            }
            GameProfile gameProfile = new GameProfile(skinsuit, null);
            skinCache.put(skinsuit, new PlayerListEntry(new Entry(
                gameProfile,
                0,
                GameMode.SURVIVAL,
                null
            )));
        }
    }

    @Override
    protected void scale(OwnedSkeletonEntity entity, MatrixStack matrices, float tickDelta) {
        float shrinkMultiplier = (entity.getGrowthPhase().ordinal() + 1) * 0.1f + 0.5f;
        matrices.scale(shrinkMultiplier, shrinkMultiplier, shrinkMultiplier);
    }

    @Override
    public void render(OwnedSkeletonEntity livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        setModel(livingEntity);
        setModelPose(livingEntity);
        ItemStack augment = livingEntity.getAugmentBlockStack();
        this.getModel().head.visible = augment.isEmpty();
        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    private void setModel(OwnedSkeletonEntity entity) {
        boolean hasThinArmTexture = skinCache.containsKey(entity.getSkinsuit()) && skinCache.get(entity.getSkinsuit()).getModel().equals("slim");
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
            this.features.remove(this.previousArmorRenderer);
            this.addFeature(currentArmorRenderer);
            this.previousArmorRenderer = this.currentArmorRenderer;
        }
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
