package dev.the_fireplace.overlord.client.model;

import dev.the_fireplace.overlord.entity.OwnedSkeletonEntity;
import dev.the_fireplace.overlord.entity.OwnedSkeletonEntity.AnimationState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class OwnedSkeletonModel extends PlayerEntityModel<OwnedSkeletonEntity>
{
    public OwnedSkeletonModel(ModelPart root) {
        super(root, false);
    }

    public static TexturedModelData getTexturedModelData(Dilation dilation, boolean hasThickLimbs, boolean isArmor, boolean hasThinArmTexture) {
        float armorExtra = isArmor ? 0.5F : 0;
        float extraXZ = armorExtra + (hasThickLimbs ? 0F : -1F);
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

        ModelData modelData = PlayerEntityModel.getTexturedModelData(dilation, hasThinArmTexture);
        ModelPartData modelPartData = modelData.getRoot();
        Dilation limbDilation = dilation.add(extraXZ, armorExtra, extraXZ);
        modelPartData.addChild(EntityModelPartNames.RIGHT_ARM, ModelPartBuilder.create()
                .uv(40, 16)
                .cuboid(rightArmStartX, -2.0F, -2.0F, armWidth, 12.0F, 4.0F, limbDilation),
            ModelTransform.pivot(-5.0F, armPivotY, 0.0F)
        );
        modelPartData.addChild(EntityModelPartNames.LEFT_ARM, ModelPartBuilder.create()
                .uv(isArmor ? 40 : 32, isArmor ? 16 : 48)
                .mirrored(isArmor)
                .cuboid(leftArmStartX, -2.0F, -2.0F, armWidth, 12.0F, 4.0F, limbDilation),
            ModelTransform.pivot(5.0F, armPivotY, 0.0F)
        );
        modelPartData.addChild(EntityModelPartNames.RIGHT_LEG, ModelPartBuilder.create()
                .uv(0, 16)
                .cuboid(-2.0F, -0.01F, -2.0F, 4.0F, 12.0F, 4.0F, limbDilation),
            ModelTransform.pivot(-legPivotX, 12.0F, 0.0F)
        );
        modelPartData.addChild(EntityModelPartNames.LEFT_LEG, ModelPartBuilder.create()
                .uv(isArmor ? 0 : 16, isArmor ? 16 : 48)
                .mirrored(isArmor)
                .cuboid(-2.0F, -0.01F, -2.0F, 4.0F, 12.0F, 4.0F, limbDilation),
            ModelTransform.pivot(legPivotX, 12.0F, 0.0F)
        );
        if (!isArmor) {
            Dilation secondLayerLimbDilation = limbDilation.add(0.25F);
            modelPartData.addChild("left_sleeve", ModelPartBuilder.create()
                    .uv(48, 48)
                    .cuboid(leftArmStartX, -2.0F, -2.0F, armWidth, 12.0F, 4.0F, secondLayerLimbDilation),
                ModelTransform.pivot(5.0F, armPivotY, 0.0F)
            );
            modelPartData.addChild("right_sleeve", ModelPartBuilder.create()
                    .uv(40, 32)
                    .cuboid(rightArmStartX, -2.0F, -2.0F, armWidth, 12.0F, 4.0F, secondLayerLimbDilation),
                ModelTransform.pivot(-5.0F, armPivotY, 10.0F)
            );
            modelPartData.addChild("left_pants", ModelPartBuilder.create()
                    .uv(0, 48)
                    .cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, secondLayerLimbDilation),
                ModelTransform.pivot(legPivotX, 12.0F, 0.0F)
            );
            modelPartData.addChild("right_pants", ModelPartBuilder.create()
                    .uv(0, 32)
                    .cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, secondLayerLimbDilation),
                ModelTransform.pivot(-legPivotX, 12.0F, 0.0F)
            );
        }

        return TexturedModelData.of(modelData, 64, isArmor ? 32 : 64);
    }

    @Override
    public void setAngles(OwnedSkeletonEntity entity, float limbAngle, float limbDistance, float customAngle, float headYaw, float headPitch) {
        super.setAngles(entity, limbAngle, limbDistance, customAngle, headYaw, headPitch);
        if (entity.getAnimationState().equals(AnimationState.MELEE_ATTACK)) {
            float k = MathHelper.sin(this.handSwingProgress * (float) Math.PI);
            float l = MathHelper.sin((1.0F - (1.0F - this.handSwingProgress) * (1.0F - this.handSwingProgress)) * (float) Math.PI);
            boolean raiseBothArms = entity.getMainHandStack().isEmpty();
            boolean raiseRightArm = entity.getMainArm() == Arm.RIGHT || raiseBothArms;
            boolean raiseLeftArm = entity.getMainArm() == Arm.LEFT || raiseBothArms;
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
        arm.roll = 0.0F;
        arm.yaw = -(0.1F - k * 0.6F);
        arm.pitch = (float) -Math.PI / 2;
        arm.pitch -= k * 1.2F - l * 0.4F;
        arm.roll += MathHelper.cos(customAngle * 0.09F) * 0.05F + 0.05F;
        arm.pitch += MathHelper.sin(customAngle * 0.067F) * 0.05F;
    }

    private void raiseLeftArm(float customAngle, float k, float l, ModelPart arm) {
        arm.roll = 0.0F;
        arm.yaw = 0.1F - k * 0.6F;
        arm.pitch = (float) -Math.PI / 2;
        arm.pitch -= k * 1.2F - l * 0.4F;
        arm.roll -= MathHelper.cos(customAngle * 0.09F) * 0.05F + 0.05F;
        arm.pitch -= MathHelper.sin(customAngle * 0.067F) * 0.05F;
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
}
