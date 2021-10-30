package dev.the_fireplace.overlord.client.renderer;

import com.mojang.authlib.GameProfile;
import dev.the_fireplace.annotateddi.api.DIContainer;
import dev.the_fireplace.lib.api.uuid.injectables.EmptyUUID;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.client.model.OwnedSkeletonModel;
import dev.the_fireplace.overlord.client.renderer.feature.AugmentHeadFeatureRenderer;
import dev.the_fireplace.overlord.entity.OwnedSkeletonEntity;
import dev.the_fireplace.overlord.entity.SkeletonGrowthPhase;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.feature.ArmorBipedFeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckStingersFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;
import net.minecraft.world.GameMode;

@Environment(EnvType.CLIENT)
public class OwnedSkeletonRenderer extends BipedEntityRenderer<OwnedSkeletonEntity, OwnedSkeletonModel>
{
    private static final Identifier NO_TEXTURE_TO_CACHE = new Identifier(Overlord.MODID, "");
    private final EmptyUUID emptyUUID;
    private Identifier cachedTexture = null;

    public OwnedSkeletonRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher, new OwnedSkeletonModel(), 0.5F);

        this.emptyUUID = DIContainer.get().getInstance(EmptyUUID.class);

        BipedEntityModel<OwnedSkeletonEntity> leggingsModel = new BipedEntityModel<>(0.5F);
        BipedEntityModel<OwnedSkeletonEntity> bodyModel = new BipedEntityModel<>(1.0F);
        this.addFeature(new ArmorBipedFeatureRenderer<>(this, leggingsModel, bodyModel));
        this.addFeature(new AugmentHeadFeatureRenderer<>(this));
        this.addFeature(new StuckArrowsFeatureRenderer<>(this));
        this.addFeature(new StuckStingersFeatureRenderer<>(this));
    }

    @Override
    public Identifier getTexture(OwnedSkeletonEntity entity) {
        this.shadowSize = 0.25f * entity.getGrowthPhase().ordinal() / 4.0f + 0.25f;
        if (!entity.hasSkin() && !entity.hasMuscles()) {
            return new Identifier(Overlord.MODID, "textures/entity/owned_skeleton/owned_skeleton.png");
        }
        if (entity.getGrowthPhase() == SkeletonGrowthPhase.ADULT && entity.hasSkin() && !emptyUUID.is(entity.getSkinsuit())) {
            cacheSkinsuitTexture(entity);
            if (!cachedTexture.equals(NO_TEXTURE_TO_CACHE)) {
                return cachedTexture;
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
        if (cachedTexture == null) {
            ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
            if (networkHandler != null) {
                GameProfile gameProfile = new GameProfile(entity.getSkinsuit(), null);
                PlayerListS2CPacket dummyPlayerListPacket = new PlayerListS2CPacket();
                PlayerListEntry playerListEntry = new PlayerListEntry(dummyPlayerListPacket.new Entry(
                    gameProfile,
                    0,
                    GameMode.SURVIVAL,
                    null
                ));
                if (playerListEntry.hasSkinTexture()) {
                    cachedTexture = playerListEntry.getSkinTexture();
                    boolean hasThinArms = playerListEntry.getModel().equals("slim");
                    if (hasThinArms) {
                        this.model.setHasThinArmTexture(true);
                    }
                    return;
                }
            }
            cachedTexture = NO_TEXTURE_TO_CACHE;
        }
    }

    @Override
    protected void scale(OwnedSkeletonEntity entity, MatrixStack matrices, float tickDelta) {
        float shrinkMultiplier = (entity.getGrowthPhase().ordinal() + 1) * 0.1f + 0.5f;
        matrices.scale(shrinkMultiplier, shrinkMultiplier, shrinkMultiplier);
    }

    @Override
    public void render(OwnedSkeletonEntity livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        setModelPose(livingEntity);
        if (livingEntity.getGrowthPhase() == SkeletonGrowthPhase.ADULT && livingEntity.hasMuscles() && !this.getModel().hasThickLimbs()) {
            this.getModel().setHasThickLimbs(true);
        } else if ((livingEntity.getGrowthPhase() != SkeletonGrowthPhase.ADULT || !livingEntity.hasMuscles()) && this.getModel().hasThickLimbs()) {
            this.getModel().setHasThickLimbs(false);
        }
        ItemStack augment = livingEntity.getAugmentBlockStack();
        this.getModel().head.visible = augment.isEmpty();
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
