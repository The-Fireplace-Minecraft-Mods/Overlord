package the_fireplace.overlord.fabric.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import the_fireplace.overlord.fabric.entity.OwnedSkeletonEntity;

@Environment(EnvType.CLIENT)
public class OwnedSkeletonModel extends BipedEntityModel<OwnedSkeletonEntity> {
    private boolean thicc;
    public OwnedSkeletonModel(float scale) {
        super(scale);
        setThicc(false);
    }

    public void setThicc(boolean thicc) {
        this.thicc = thicc;
        float scale = thicc ? 4.0F : 2.0F;
        this.rightArm = new ModelPart(this, 40, 16);
        this.rightArm.addCuboid(-1.0F, -2.0F, -1.0F, scale, 12.0F, scale, false);
        this.rightArm.setPivot(-5.0F, 2.0F, 0.0F);
        this.leftArm = new ModelPart(this, 40, 16);
        this.leftArm.mirror = true;
        this.leftArm.addCuboid(-1.0F, -2.0F, -1.0F, scale, 12.0F, scale, false);
        this.leftArm.setPivot(5.0F, 2.0F, 0.0F);
        this.rightLeg = new ModelPart(this, 0, 16);
        this.rightLeg.addCuboid(-1.0F, 0.0F, -1.0F, scale, 12.0F, scale, false);
        this.rightLeg.setPivot(-2.0F, 12.0F, 0.0F);
        this.leftLeg = new ModelPart(this, 0, 16);
        this.leftLeg.mirror = true;
        this.leftLeg.addCuboid(-1.0F, 0.0F, -1.0F, scale, 12.0F, scale, false);
        this.leftLeg.setPivot(2.0F, 12.0F, 0.0F);
    }

    @Override
    public void setAngles(OwnedSkeletonEntity mobEntity, float f, float g, float h, float i, float j) {
        super.setAngles(mobEntity, f, g, h, i, j);
        if (mobEntity.isMeleeAttacking()) {
            float k = MathHelper.sin(this.handSwingProgress * 3.1415927F);
            float l = MathHelper.sin((1.0F - (1.0F - this.handSwingProgress) * (1.0F - this.handSwingProgress)) * 3.1415927F);
            this.rightArm.roll = 0.0F;
            this.leftArm.roll = 0.0F;
            this.rightArm.yaw = -(0.1F - k * 0.6F);
            this.leftArm.yaw = 0.1F - k * 0.6F;
            this.rightArm.pitch = -1.5707964F;
            this.leftArm.pitch = -1.5707964F;
            ModelPart arm = this.rightArm;
            arm.pitch -= k * 1.2F - l * 0.4F;
            arm = this.leftArm;
            arm.pitch -= k * 1.2F - l * 0.4F;
            arm = this.rightArm;
            arm.roll += MathHelper.cos(h * 0.09F) * 0.05F + 0.05F;
            arm = this.leftArm;
            arm.roll -= MathHelper.cos(h * 0.09F) * 0.05F + 0.05F;
            arm = this.rightArm;
            arm.pitch += MathHelper.sin(h * 0.067F) * 0.05F;
            arm = this.leftArm;
            arm.pitch -= MathHelper.sin(h * 0.067F) * 0.05F;
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

    public boolean isThicc() {
        return thicc;
    }
}
