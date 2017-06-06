package the_fireplace.overlord.client.render;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import the_fireplace.overlord.entity.EntitySkeletonWarrior;

import javax.annotation.Nonnull;

/**
 * @author The_Fireplace
 */
@SideOnly(Side.CLIENT)
public class ModelSkeletonWarrior extends ModelBiped {
	public ModelRenderer skinsuitHead;
	public ModelRenderer skinsuitHeadwear;
	public ModelRenderer skinsuitBody;
	public ModelRenderer skinsuitRightArm;
	public ModelRenderer skinsuitLeftArm;
	public ModelRenderer skinsuitRightLeg;
	public ModelRenderer skinsuitLeftLeg;
	public ModelRenderer skinsuitLeftArmwear;
	public ModelRenderer skinsuitRightArmwear;
	public ModelRenderer skinsuitLeftLegwear;
	public ModelRenderer skinsuitRightLegwear;
	public ModelRenderer skinsuitBodyWear;
	public boolean smallSkinsuitArms;
	public boolean skinsuit;

	public ModelSkeletonWarrior() {
		this(0.0F, false, false, 1);
	}

	public ModelSkeletonWarrior(float modelSize, boolean notmain, boolean hasSkinsuit, int texScale) {
		super(modelSize, 0.0F, 64, 32 * texScale);

		this.smallSkinsuitArms = false;
		this.skinsuit = hasSkinsuit;

		if (!notmain) {
			this.bipedRightArm = new ModelRenderer(this, 40, 16);
			this.bipedRightArm.addBox(-1.0F, -2.0F, -1.0F, 2, 12, 2, modelSize);
			this.bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
			this.bipedLeftArm = new ModelRenderer(this, 40, 16);
			this.bipedLeftArm.mirror = true;
			this.bipedLeftArm.addBox(-1.0F, -2.0F, -1.0F, 2, 12, 2, modelSize);
			this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
			this.bipedRightLeg = new ModelRenderer(this, 0, 16);
			this.bipedRightLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2, modelSize);
			this.bipedRightLeg.setRotationPoint(-2.0F, 12.0F, 0.0F);
			this.bipedLeftLeg = new ModelRenderer(this, 0, 16);
			this.bipedLeftLeg.mirror = true;
			this.bipedLeftLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2, modelSize);
			this.bipedLeftLeg.setRotationPoint(2.0F, 12.0F, 0.0F);
		}
		if (skinsuit) {
			if (smallSkinsuitArms) {
				this.skinsuitLeftArm = new ModelRenderer(this, 32, 48);
				this.skinsuitLeftArm.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, modelSize);
				this.skinsuitLeftArm.setRotationPoint(5.0F, 2.5F, 0.0F);
				this.skinsuitRightArm = new ModelRenderer(this, 40, 16);
				this.skinsuitRightArm.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, modelSize);
				this.skinsuitRightArm.setRotationPoint(-5.0F, 2.5F, 0.0F);
				this.skinsuitLeftArmwear = new ModelRenderer(this, 48, 48);
				this.skinsuitLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, modelSize + 0.25F);
				this.skinsuitLeftArmwear.setRotationPoint(5.0F, 2.5F, 0.0F);
				this.skinsuitRightArmwear = new ModelRenderer(this, 40, 32);
				this.skinsuitRightArmwear.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, modelSize + 0.25F);
				this.skinsuitRightArmwear.setRotationPoint(-5.0F, 2.5F, 10.0F);
			} else {
				this.skinsuitLeftArm = new ModelRenderer(this, 32, 48);
				this.skinsuitLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, modelSize);
				this.skinsuitLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
				this.skinsuitRightArm = new ModelRenderer(this, 40, 16);
				this.skinsuitRightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, modelSize);
				this.skinsuitRightArm.setRotationPoint(-5.0F, 2.5F, 0.0F);
				this.skinsuitLeftArmwear = new ModelRenderer(this, 48, 48);
				this.skinsuitLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, modelSize + 0.25F);
				this.skinsuitLeftArmwear.setRotationPoint(5.0F, 2.0F, 0.0F);
				this.skinsuitRightArmwear = new ModelRenderer(this, 40, 32);
				this.skinsuitRightArmwear.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, modelSize + 0.25F);
				this.skinsuitRightArmwear.setRotationPoint(-5.0F, 2.0F, 10.0F);
			}

			this.skinsuitLeftLeg = new ModelRenderer(this, 16, 48);
			this.skinsuitLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize);
			this.skinsuitLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
			this.skinsuitLeftLegwear = new ModelRenderer(this, 0, 48);
			this.skinsuitLeftLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize + 0.25F);
			this.skinsuitLeftLegwear.setRotationPoint(1.9F, 12.0F, 0.0F);
			this.skinsuitRightLegwear = new ModelRenderer(this, 0, 32);
			this.skinsuitRightLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize + 0.25F);
			this.skinsuitRightLegwear.setRotationPoint(-1.9F, 12.0F, 0.0F);
			this.skinsuitBodyWear = new ModelRenderer(this, 16, 32);
			this.skinsuitBodyWear.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, modelSize + 0.25F);
			this.skinsuitBodyWear.setRotationPoint(0.0F, 0.0F, 0.0F);
			this.skinsuitHead = new ModelRenderer(this, 0, 0);
			this.skinsuitHead.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, modelSize);
			this.skinsuitHead.setRotationPoint(0.0F, 0.0F, 0.0F);
			this.skinsuitHeadwear = new ModelRenderer(this, 32, 0);
			this.skinsuitHeadwear.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, modelSize + 0.5F);
			this.skinsuitHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
			this.skinsuitBody = new ModelRenderer(this, 16, 16);
			this.skinsuitBody.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, modelSize);
			this.skinsuitBody.setRotationPoint(0.0F, 0.0F, 0.0F);
			this.skinsuitRightLeg = new ModelRenderer(this, 0, 16);
			this.skinsuitRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize);
			this.skinsuitRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
		}
	}

	@Override
	public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float p_78086_2_, float p_78086_3_, float partialTickTime) {
		this.rightArmPose = ModelBiped.ArmPose.EMPTY;
		this.leftArmPose = ModelBiped.ArmPose.EMPTY;
		ItemStack itemstack = entitylivingbaseIn.getHeldItem(EnumHand.MAIN_HAND);

		if (!itemstack.isEmpty() && itemstack.getItem() == Items.BOW && ((EntitySkeletonWarrior) entitylivingbaseIn).isSwingingArms()) {
			if (entitylivingbaseIn.getPrimaryHand() == EnumHandSide.RIGHT) {
				this.rightArmPose = ModelBiped.ArmPose.BOW_AND_ARROW;
			} else {
				this.leftArmPose = ModelBiped.ArmPose.BOW_AND_ARROW;
			}
		}

		super.setLivingAnimations(entitylivingbaseIn, p_78086_2_, p_78086_3_, partialTickTime);
	}

	@Override
	public void render(@Nonnull Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
		GlStateManager.pushMatrix();
		if (skinsuit) {
			if (entityIn.isSneaking()) {
				GlStateManager.translate(0.0F, 0.2F, 0.0F);
			}
			this.skinsuitHead.render(scale);
			this.skinsuitBody.render(scale);
			this.skinsuitRightArm.render(scale);
			this.skinsuitLeftArm.render(scale);
			this.skinsuitRightLeg.render(scale);
			this.skinsuitLeftLeg.render(scale);
			this.skinsuitHeadwear.render(scale);
			this.skinsuitLeftLegwear.render(scale);
			this.skinsuitRightLegwear.render(scale);
			this.skinsuitLeftArmwear.render(scale);
			this.skinsuitRightArmwear.render(scale);
			this.skinsuitBodyWear.render(scale);
		}
		GlStateManager.popMatrix();
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, @Nonnull Entity entityIn) {
		super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
		ItemStack itemstack = ((EntityLivingBase) entityIn).getHeldItemMainhand();
		EntitySkeletonWarrior entityskeleton = (EntitySkeletonWarrior) entityIn;

		if (entityskeleton.isSwingingArms() && (itemstack.isEmpty() || itemstack.getItem() != Items.BOW)) {
			float f = MathHelper.sin(this.swingProgress * (float) Math.PI);
			float f1 = MathHelper.sin((1.0F - (1.0F - this.swingProgress) * (1.0F - this.swingProgress)) * (float) Math.PI);
			this.bipedRightArm.rotateAngleZ = 0.0F;
			this.bipedLeftArm.rotateAngleZ = 0.0F;
			this.bipedRightArm.rotateAngleY = -(0.1F - f * 0.6F);
			this.bipedLeftArm.rotateAngleY = 0.1F - f * 0.6F;
			this.bipedRightArm.rotateAngleX = -((float) Math.PI / 2F);
			this.bipedLeftArm.rotateAngleX = -((float) Math.PI / 2F);
			this.bipedRightArm.rotateAngleX -= f * 1.2F - f1 * 0.4F;
			this.bipedLeftArm.rotateAngleX -= f * 1.2F - f1 * 0.4F;
			this.bipedRightArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
			this.bipedLeftArm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
			this.bipedRightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
			this.bipedLeftArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
		}
		if (skinsuit) {
			copyModelAngles(this.bipedLeftLeg, this.skinsuitLeftLegwear);
			copyModelAngles(this.bipedRightLeg, this.skinsuitRightLegwear);
			copyModelAngles(this.bipedLeftArm, this.skinsuitLeftArmwear);
			copyModelAngles(this.bipedRightArm, this.skinsuitRightArmwear);
			copyModelAngles(this.bipedBody, this.skinsuitBodyWear);
			copyModelAngles(this.bipedLeftLeg, this.skinsuitLeftLeg);
			copyModelAngles(this.bipedRightLeg, this.skinsuitRightLeg);
			copyModelAngles(this.bipedLeftArm, this.skinsuitLeftArm);
			copyModelAngles(this.bipedRightArm, this.skinsuitRightArm);
			copyModelAngles(this.bipedBody, this.skinsuitBody);
			copyModelAngles(this.bipedHead, this.skinsuitHead);
			copyModelAngles(this.bipedHead, this.skinsuitHeadwear);
		}
	}

	@Override
	public void postRenderArm(float scale, @Nonnull EnumHandSide side) {
		float f = side == EnumHandSide.RIGHT ? 1.0F : -1.0F;
		ModelRenderer modelrenderer = this.getArmForSide(side);
		modelrenderer.rotationPointX += f;
		modelrenderer.postRender(scale);
		modelrenderer.rotationPointX -= f;
	}
}
