package dev.the_fireplace.overlord.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.the_fireplace.overlord.entity.OwnedSkeletonEntity;
import dev.the_fireplace.overlord.entity.OwnedSkeletonEntity.AnimationState;
import dev.the_fireplace.overlord.mixin.client.PlayerEntityModelAccessor;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

public class OwnedSkeletonModel extends PlayerModel<OwnedSkeletonEntity>
{
    private final boolean hasThickLimbs;
    private final boolean isArmor;
    private final boolean hasThinArmTexture;

    public OwnedSkeletonModel(boolean hasThickLimbs, boolean isArmor, boolean hasThinArmTexture) {
        super(0, false);
        this.hasThickLimbs = hasThickLimbs;
        this.isArmor = isArmor;
        this.hasThinArmTexture = hasThinArmTexture;
        if (isArmor) {
            this.texHeight = 32;
        }
        resizeLimbs();
    }

    private void resizeLimbs() {
        float armorExtra = isArmor ? 0.5F : 0;
        float extraXZ = armorExtra + (hasThickLimbs ? (isArmor ? -0.02F : 0F) : -1F);
        float armWidth = hasThinArmTexture ? 3 : 4;
        float leftArmStartX = hasThickLimbs ? -1F : -2F;
        float rightArmStartX = hasThickLimbs ? -3.0F : -2F;
        float armPivotY = 2.0F;
        if (hasThickLimbs) {
            armPivotY += 0.5F;
        }
        if (hasThinArmTexture) {
            armPivotY += 0.5F;
            leftArmStartX += 0.5;
            rightArmStartX += 0.5;
        }
        float legPivotX = hasThickLimbs ? 2.5F : 2.0F;
        this.rightArm = new ModelPart(this, 40, 16);
        this.rightArm.addBox(rightArmStartX, -2.0F, -2.0F, armWidth, 12.0F, 4.0F, extraXZ, armorExtra, extraXZ);
        this.rightArm.setPos(-5.0F, armPivotY, 0.0F);
        this.leftArm = new ModelPart(this, 40, 16);
        this.leftArm.addBox(leftArmStartX, -2.0F, -2.0F, armWidth, 12.0F, 4.0F, extraXZ, armorExtra, extraXZ);
        this.leftArm.mirror = true;
        this.leftArm.setPos(5.0F, armPivotY, 0.0F);
        this.rightLeg = new ModelPart(this, 0, 16);
        this.rightLeg.addBox(-2.0F, -0.01F, -2.0F, 4.0F, 12.0F, 4.0F, extraXZ, armorExtra, extraXZ);
        this.rightLeg.setPos(-legPivotX, 12.0F, 0.0F);
        this.leftLeg = new ModelPart(this, 0, 16);
        this.leftLeg.addBox(-2.0F, -0.01F, -2.0F, 4.0F, 12.0F, 4.0F, extraXZ, armorExtra, extraXZ);
        this.leftLeg.mirror = true;
        this.leftLeg.setPos(legPivotX, 12.0F, 0.0F);
        if (!isArmor) {
            //noinspection RedundantCast
            PlayerEntityModelAccessor playerEntityModelAccessor = (PlayerEntityModelAccessor) (Object) this;

            ModelPart leftSleeve = new ModelPart(this, 48, 48);
            leftSleeve.addBox(leftArmStartX, -2.0F, -2.0F, armWidth, 12.0F, 4.0F, extraXZ + 0.25F, 0.25F, extraXZ + 0.25F);
            leftSleeve.setPos(5.0F, armPivotY, 0.0F);

            ModelPart rightSleeve = new ModelPart(this, 40, 32);
            rightSleeve.addBox(rightArmStartX, -2.0F, -2.0F, armWidth, 12.0F, 4.0F, extraXZ + 0.25F, 0.25F, extraXZ + 0.25F);
            rightSleeve.setPos(-5.0F, armPivotY, 10.0F);

            ModelPart leftPantLeg = new ModelPart(this, 0, 48);
            leftPantLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, extraXZ + 0.25F, 0.25F, extraXZ + 0.25F);
            leftPantLeg.setPos(legPivotX, 12.0F, 0.0F);

            ModelPart rightPantLeg = new ModelPart(this, 0, 32);
            rightPantLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, extraXZ + 0.25F, 0.25F, extraXZ + 0.25F);
            rightPantLeg.setPos(-legPivotX, 12.0F, 0.0F);

            playerEntityModelAccessor.setLeftSleeve(leftSleeve);
            playerEntityModelAccessor.setRightSleeve(rightSleeve);
            playerEntityModelAccessor.setLeftPants(leftPantLeg);
            playerEntityModelAccessor.setRightPants(rightPantLeg);
        }
    }

    @Override
    public void setupAnim(OwnedSkeletonEntity entity, float limbAngle, float limbDistance, float customAngle, float headYaw, float headPitch) {
        super.setupAnim(entity, limbAngle, limbDistance, customAngle, headYaw, headPitch);
        if (entity.getAnimationState().equals(AnimationState.MELEE_ATTACK)) {
            float k = Mth.sin(this.attackTime * (float) Math.PI);
            float l = Mth.sin((1.0F - (1.0F - this.attackTime) * (1.0F - this.attackTime)) * (float) Math.PI);
            boolean raiseBothArms = entity.getMainHandItem().isEmpty();
            boolean raiseRightArm = entity.getMainArm() == HumanoidArm.RIGHT || raiseBothArms;
            boolean raiseLeftArm = entity.getMainArm() == HumanoidArm.LEFT || raiseBothArms;
            if (raiseRightArm) {
                raiseRightArm(customAngle, k, l, this.rightArm);
                raiseRightArm(customAngle, k, l, this.rightSleeve);
            }
            if (raiseLeftArm) {
                raiseLeftArm(customAngle, k, l, this.leftArm);
                raiseLeftArm(customAngle, k, l, this.leftSleeve);
            }
        }
    }

    private void raiseRightArm(float customAngle, float k, float l, ModelPart arm) {
        arm.zRot = 0.0F;
        arm.yRot = -(0.1F - k * 0.6F);
        arm.xRot = (float) -Math.PI / 2;
        arm.xRot -= k * 1.2F - l * 0.4F;
        arm.zRot += Mth.cos(customAngle * 0.09F) * 0.05F + 0.05F;
        arm.xRot += Mth.sin(customAngle * 0.067F) * 0.05F;
    }

    private void raiseLeftArm(float customAngle, float k, float l, ModelPart arm) {
        arm.zRot = 0.0F;
        arm.yRot = 0.1F - k * 0.6F;
        arm.xRot = (float) -Math.PI / 2;
        arm.xRot -= k * 1.2F - l * 0.4F;
        arm.zRot -= Mth.cos(customAngle * 0.09F) * 0.05F + 0.05F;
        arm.xRot -= Mth.sin(customAngle * 0.067F) * 0.05F;
    }

    @Override
    public void translateToHand(HumanoidArm arm, PoseStack matrixStack) {
        float f = arm == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        ModelPart modelPart = this.getArm(arm);
        modelPart.x += f;
        modelPart.translateAndRotate(matrixStack);
        modelPart.x -= f;
    }

    @Override
    public void prepareMobModel(OwnedSkeletonEntity entity, float f, float g, float h) {
        ItemStack rightArmStack = entity.getMainArm() == HumanoidArm.RIGHT ? entity.getMainHandItem() : entity.getOffhandItem();
        ItemStack leftArmStack = entity.getMainArm() == HumanoidArm.LEFT ? entity.getMainHandItem() : entity.getOffhandItem();

        this.rightArmPose = rightArmStack.isEmpty() ? ArmPose.EMPTY : ArmPose.ITEM;
        this.leftArmPose = leftArmStack.isEmpty() ? ArmPose.EMPTY : ArmPose.ITEM;
        switch (entity.getAnimationState()) {
            case BOW_AND_ARROW:
                if (entity.getMainArm() == HumanoidArm.RIGHT) {
                    this.rightArmPose = ArmPose.BOW_AND_ARROW;
                } else {
                    this.leftArmPose = ArmPose.BOW_AND_ARROW;
                }
                break;
            case CROSSBOW_CHARGE:
                if (entity.getMainArm() == HumanoidArm.RIGHT) {
                    this.rightArmPose = ArmPose.CROSSBOW_CHARGE;
                } else {
                    this.leftArmPose = ArmPose.CROSSBOW_CHARGE;
                }
                break;
            case CROSSBOW_AIM:
                if (entity.getMainArm() == HumanoidArm.RIGHT) {
                    this.rightArmPose = ArmPose.CROSSBOW_HOLD;
                } else {
                    this.leftArmPose = ArmPose.CROSSBOW_HOLD;
                }
                break;
            case DRINK:
                //TODO Not sure the standard animations will cover this right now - maybe in a future update?
                break;
        }

        super.prepareMobModel(entity, f, g, h);
    }
}
