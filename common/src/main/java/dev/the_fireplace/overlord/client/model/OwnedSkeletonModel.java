package dev.the_fireplace.overlord.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.the_fireplace.overlord.entity.OwnedSkeletonEntity;
import dev.the_fireplace.overlord.entity.OwnedSkeletonEntity.AnimationState;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

public class OwnedSkeletonModel extends PlayerModel<OwnedSkeletonEntity>
{
    public OwnedSkeletonModel(ModelPart root) {
        super(root, false);
    }

    public static LayerDefinition getTexturedModelData(CubeDeformation dilation, boolean hasThickLimbs, boolean isArmor, boolean hasThinArmTexture) {
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

        MeshDefinition modelData = PlayerModel.createMesh(dilation, hasThinArmTexture);
        PartDefinition modelPartData = modelData.getRoot();
        CubeDeformation limbDilation = dilation.extend(extraXZ, armorExtra, extraXZ);
        modelPartData.addOrReplaceChild(PartNames.RIGHT_ARM, CubeListBuilder.create()
                .texOffs(40, 16)
                .addBox(rightArmStartX, -2.0F, -2.0F, armWidth, 12.0F, 4.0F, limbDilation),
            PartPose.offset(-5.0F, armPivotY, 0.0F)
        );
        modelPartData.addOrReplaceChild(PartNames.LEFT_ARM, CubeListBuilder.create()
                .texOffs(isArmor ? 40 : 32, isArmor ? 16 : 48)
                .mirror(isArmor)
                .addBox(leftArmStartX, -2.0F, -2.0F, armWidth, 12.0F, 4.0F, limbDilation),
            PartPose.offset(5.0F, armPivotY, 0.0F)
        );
        modelPartData.addOrReplaceChild(PartNames.RIGHT_LEG, CubeListBuilder.create()
                .texOffs(0, 16)
                .addBox(-2.0F, -0.01F, -2.0F, 4.0F, 12.0F, 4.0F, limbDilation),
            PartPose.offset(-legPivotX, 12.0F, 0.0F)
        );
        modelPartData.addOrReplaceChild(PartNames.LEFT_LEG, CubeListBuilder.create()
                .texOffs(isArmor ? 0 : 16, isArmor ? 16 : 48)
                .mirror(isArmor)
                .addBox(-2.0F, -0.01F, -2.0F, 4.0F, 12.0F, 4.0F, limbDilation),
            PartPose.offset(legPivotX, 12.0F, 0.0F)
        );
        if (!isArmor) {
            CubeDeformation secondLayerLimbDilation = limbDilation.extend(0.25F);
            modelPartData.addOrReplaceChild("left_sleeve", CubeListBuilder.create()
                    .texOffs(48, 48)
                    .addBox(leftArmStartX, -2.0F, -2.0F, armWidth, 12.0F, 4.0F, secondLayerLimbDilation),
                PartPose.offset(5.0F, armPivotY, 0.0F)
            );
            modelPartData.addOrReplaceChild("right_sleeve", CubeListBuilder.create()
                    .texOffs(40, 32)
                    .addBox(rightArmStartX, -2.0F, -2.0F, armWidth, 12.0F, 4.0F, secondLayerLimbDilation),
                PartPose.offset(-5.0F, armPivotY, 10.0F)
            );
            modelPartData.addOrReplaceChild("left_pants", CubeListBuilder.create()
                    .texOffs(0, 48)
                    .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, secondLayerLimbDilation),
                PartPose.offset(legPivotX, 12.0F, 0.0F)
            );
            modelPartData.addOrReplaceChild("right_pants", CubeListBuilder.create()
                    .texOffs(0, 32)
                    .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, secondLayerLimbDilation),
                PartPose.offset(-legPivotX, 12.0F, 0.0F)
            );
        }

        return LayerDefinition.create(modelData, 64, isArmor ? 32 : 64);
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
