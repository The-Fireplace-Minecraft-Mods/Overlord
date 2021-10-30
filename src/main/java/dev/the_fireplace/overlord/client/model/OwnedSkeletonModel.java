package dev.the_fireplace.overlord.client.model;

import dev.the_fireplace.overlord.entity.OwnedSkeletonEntity;
import dev.the_fireplace.overlord.entity.OwnedSkeletonEntity.AnimationState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class OwnedSkeletonModel extends PlayerEntityModel<OwnedSkeletonEntity>
{
    private boolean hasThickLimbs;
    private boolean hasThinArmTexture = false;

    public OwnedSkeletonModel() {
        super(0, false);
        setHasThickLimbs(false);
    }

    public void setHasThickLimbs(boolean hasThickLimbs) {
        this.hasThickLimbs = hasThickLimbs;
        resizeLimbs();
    }

    public void setHasThinArmTexture(boolean hasThinArmTexture) {
        this.hasThinArmTexture = hasThinArmTexture;
        resizeLimbs();
    }

    private void resizeLimbs() {
        float extra = hasThickLimbs ? 0F : -0.5F;
        float armWidth = hasThinArmTexture ? 3 : 4;
        float armWidthExtra = hasThinArmTexture ? 1 : 0;
        this.rightArm = new ModelPart(this, 40, 16);
        this.rightArm.addCuboid(hasThickLimbs ? -3.0F : -2.5F, -2.0F, -2.0F, armWidth, 12.0F, 4.0F, extra + armWidthExtra, 0, extra);
        this.rightArm.setPivot(-5.0F, hasThickLimbs ? 2.5F : 2.0F, 0.0F);
        this.leftArm = new ModelPart(this, 40, 16);
        this.leftArm.addCuboid(hasThickLimbs ? -1.0F : -1.5F, -2.0F, -2.0F, armWidth, 12.0F, 4.0F, extra + armWidthExtra, 0, extra);
        this.leftArm.mirror = true;
        this.leftArm.setPivot(5.0F, hasThickLimbs ? 2.5F : 2.0F, 0.0F);
        this.rightLeg = new ModelPart(this, 0, 16);
        this.rightLeg.addCuboid(-2.0F, -0.01F, -2.0F, 4.0F, 12.0F, 4.0F, extra, 0, extra);
        this.rightLeg.setPivot(hasThickLimbs ? -2.5F : -2.0F, 12.0F, 0.0F);
        this.leftLeg = new ModelPart(this, 0, 16);
        this.leftLeg.addCuboid(-2.0F, -0.01F, -2.0F, 4.0F, 12.0F, 4.0F, extra, 0, extra);
        this.leftLeg.mirror = true;
        this.leftLeg.setPivot(hasThickLimbs ? 2.5F : 2.0F, 12.0F, 0.0F);
        //TODO sleeves
    }

    @Override
    public void setAngles(OwnedSkeletonEntity entity, float f, float g, float h, float i, float j) {
        super.setAngles(entity, f, g, h, i, j);
        if (entity.getAnimationState().equals(AnimationState.MELEE_ATTACK)) {
            float k = MathHelper.sin(this.handSwingProgress * (float) Math.PI);
            float l = MathHelper.sin((1.0F - (1.0F - this.handSwingProgress) * (1.0F - this.handSwingProgress)) * (float) Math.PI);
            if (entity.getMainArm() == Arm.RIGHT) {
                ModelPart arm = this.rightArm;
                arm.roll = 0.0F;
                arm.yaw = -(0.1F - k * 0.6F);
                arm.pitch = (float) -Math.PI / 2;
                arm.pitch -= k * 1.2F - l * 0.4F;
                arm.roll += MathHelper.cos(h * 0.09F) * 0.05F + 0.05F;
                arm.pitch += MathHelper.sin(h * 0.067F) * 0.05F;
            } else {
                ModelPart arm = this.leftArm;
                arm.roll = 0.0F;
                arm.yaw = 0.1F - k * 0.6F;
                arm.pitch = (float) -Math.PI / 2;
                arm.pitch -= k * 1.2F - l * 0.4F;
                arm.roll -= MathHelper.cos(h * 0.09F) * 0.05F + 0.05F;
                arm.pitch -= MathHelper.sin(h * 0.067F) * 0.05F;
            }
        }
    }

    @Override
    public void setArmAngle(Arm arm, MatrixStack matrixStack) {
        float f = arm == Arm.RIGHT ? 1.0F : -1.0F;
        ModelPart modelPart = this.getArm(arm);
        modelPart.pivotX += f;
        modelPart.rotate(matrixStack);
        modelPart.pivotX -= f;
    }

    @Override
    public void animateModel(OwnedSkeletonEntity entity, float f, float g, float h) {
        ItemStack rightArmStack = entity.getMainArm() == Arm.RIGHT ? entity.getMainHandStack() : entity.getOffHandStack();
        ItemStack leftArmStack = entity.getMainArm() == Arm.LEFT ? entity.getMainHandStack() : entity.getOffHandStack();

        this.rightArmPose = rightArmStack.isEmpty() ? ArmPose.EMPTY : ArmPose.ITEM;
        this.leftArmPose = leftArmStack.isEmpty() ? ArmPose.EMPTY : ArmPose.ITEM;
        switch (entity.getAnimationState()) {
            case BOW_AND_ARROW:
                if (entity.getMainArm() == Arm.RIGHT) {
                    this.rightArmPose = ArmPose.BOW_AND_ARROW;
                } else {
                    this.leftArmPose = ArmPose.BOW_AND_ARROW;
                }
                break;
            case CROSSBOW_CHARGE:
                if (entity.getMainArm() == Arm.RIGHT) {
                    this.rightArmPose = ArmPose.CROSSBOW_CHARGE;
                } else {
                    this.leftArmPose = ArmPose.CROSSBOW_CHARGE;
                }
                break;
            case CROSSBOW_AIM:
                if (entity.getMainArm() == Arm.RIGHT) {
                    this.rightArmPose = ArmPose.CROSSBOW_HOLD;
                } else {
                    this.leftArmPose = ArmPose.CROSSBOW_HOLD;
                }
                break;
            case DRINK:
                //TODO Not sure the standard animations will cover this right now - maybe in a future update?
                break;
        }

        super.animateModel(entity, f, g, h);
    }

    public boolean hasThickLimbs() {
        return hasThickLimbs;
    }
}
