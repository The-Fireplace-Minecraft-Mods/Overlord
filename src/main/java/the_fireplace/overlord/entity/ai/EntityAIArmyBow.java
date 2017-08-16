package the_fireplace.overlord.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.util.EnumHand;
import the_fireplace.overlord.entity.EntityArmyMember;

/**
 * @author The_Fireplace
 */
public class EntityAIArmyBow extends EntityAIBase {
	private final EntityArmyMember armyMember;
	private final double moveSpeedAmp;
	private int attackCooldown;
	private final float maxAttackDistance;
	private int attackTime = -1;
	private int seeTime;
	private boolean strafingClockwise;
	private boolean strafingBackwards;
	private int strafingTime = -1;

	public EntityAIArmyBow(EntityArmyMember armyMember, double speedAmplifier, int delay, float maxDistance) {
		this.armyMember = armyMember;
		this.moveSpeedAmp = speedAmplifier;
		this.attackCooldown = delay;
		this.maxAttackDistance = maxDistance * maxDistance;
		this.setMutexBits(3);
	}

	@Override
	public boolean shouldExecute() {
		return this.armyMember.getAttackTarget() != null && this.isBowInMainhand() && this.isArrowInOffhand();
	}

	protected boolean isBowInMainhand() {
		return !this.armyMember.getHeldItemMainhand().isEmpty() && this.armyMember.getHeldItemMainhand().getItem() instanceof ItemBow;
	}

	protected boolean isArrowInOffhand() {
		return !this.armyMember.getHeldItemOffhand().isEmpty() && this.armyMember.getHeldItemOffhand().getItem() instanceof ItemArrow;
	}

	@Override
	public boolean shouldContinueExecuting() {
		return (this.shouldExecute() || (!this.armyMember.getNavigator().noPath() && this.isBowInMainhand() && this.isArrowInOffhand()));
	}

	@Override
	public void startExecuting() {
		super.startExecuting();
		this.armyMember.setSwingingArms(true);
	}

	@Override
	public void resetTask() {
		super.resetTask();
		this.armyMember.setSwingingArms(false);
		this.seeTime = 0;
		this.attackTime = -1;
		this.armyMember.resetActiveHand();
	}

	@Override
	public void updateTask() {
		EntityLivingBase attackTarget = this.armyMember.getAttackTarget();

		if (attackTarget != null) {
			double distanceSq = this.armyMember.getDistanceSq(attackTarget.posX, attackTarget.getEntityBoundingBox().minY, attackTarget.posZ);
			boolean canSee = this.armyMember.getEntitySenses().canSee(attackTarget);
			boolean targetWithinVisibleTime = this.seeTime > 0;

			if (canSee != targetWithinVisibleTime) {
				this.seeTime = 0;
			}

			if (canSee)
				++this.seeTime;
			else
				--this.seeTime;
			if (armyMember.getMovementMode() > 0)
				if (distanceSq <= (double) this.maxAttackDistance && this.seeTime >= 20) {
					this.armyMember.getNavigator().clearPathEntity();
					++this.strafingTime;
				} else {
					this.armyMember.getNavigator().tryMoveToEntityLiving(attackTarget, this.moveSpeedAmp);
					this.strafingTime = -1;
				}
			if (armyMember.getMovementMode() > 0)
				if (this.strafingTime >= 20) {
					if ((double) this.armyMember.getRNG().nextFloat() < 0.3D)
						this.strafingClockwise = !this.strafingClockwise;

					if ((double) this.armyMember.getRNG().nextFloat() < 0.3D)
						this.strafingBackwards = !this.strafingBackwards;

					this.strafingTime = 0;
				}
			if (this.strafingTime > -1) {
				if (armyMember.getMovementMode() > 0)
					if (distanceSq > (double) (this.maxAttackDistance * 0.75F))
						this.strafingBackwards = false;
					else if (distanceSq < (double) (this.maxAttackDistance * 0.25F))
						this.strafingBackwards = true;

				if (armyMember.getMovementMode() > 0)
					this.armyMember.getMoveHelper().strafe(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
				this.armyMember.faceEntity(attackTarget, 30.0F, 30.0F);
			} else
				this.armyMember.getLookHelper().setLookPositionWithEntity(attackTarget, 30.0F, 30.0F);

			if (this.armyMember.isHandActive()) {
				if (!canSee && this.seeTime < -60) {
					this.armyMember.resetActiveHand();
				} else if (canSee) {
					int i = this.armyMember.getItemInUseMaxCount();

					if (i >= 20) {
						this.armyMember.resetActiveHand();
						this.armyMember.attackEntityWithRangedAttack(attackTarget, ItemBow.getArrowVelocity(i));
						this.attackTime = this.attackCooldown;
					}
				}
			} else if (--this.attackTime <= 0 && this.seeTime >= -60)
				this.armyMember.setActiveHand(EnumHand.MAIN_HAND);
		}
	}
}