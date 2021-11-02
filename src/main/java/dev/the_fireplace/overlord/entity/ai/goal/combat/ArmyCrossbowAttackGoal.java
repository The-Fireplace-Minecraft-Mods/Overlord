package dev.the_fireplace.overlord.entity.ai.goal.combat;

import dev.the_fireplace.annotateddi.api.DIContainer;
import dev.the_fireplace.overlord.entity.ArmyEntity;
import dev.the_fireplace.overlord.entity.ai.goal.AIEquipmentHelper;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import java.util.EnumSet;

public class ArmyCrossbowAttackGoal<T extends ArmyEntity & RangedAttackMob & CrossbowUser> extends Goal
{
	private final T armyEntity;
	private Stage stage;
	private final double speed;
	private final float squaredRange;
	private int seeingTargetTicker;
	private int chargedTicksLeft;
	protected final AIEquipmentHelper equipmentHelper;
	private long lastUpdateTime;

	public ArmyCrossbowAttackGoal(T armyEntity, double speed, float range) {
		this.stage = Stage.UNCHARGED;
		this.armyEntity = armyEntity;
		this.speed = speed;
		this.squaredRange = range * range;
		this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
		this.equipmentHelper = DIContainer.get().getInstance(AIEquipmentHelper.class);
	}

	@Override
	public boolean canStart() {
		long worldTime = this.armyEntity.world.getTime();
		if (worldTime - this.lastUpdateTime < 20) {
			return false;
		}
		this.lastUpdateTime = worldTime;
		return this.hasAliveTarget() && this.isEntityHoldingCrossbow();
	}

	private boolean isEntityHoldingCrossbow() {
		return this.armyEntity.getMainHandStack().getItem() instanceof CrossbowItem && equipmentHelper.hasAmmoEquipped(this.armyEntity);
	}

	@Override
	public boolean shouldContinue() {
		return this.hasAliveTarget() && (this.canStart() || !this.armyEntity.getNavigation().isIdle()) && this.isEntityHoldingCrossbow();
	}

	private boolean hasAliveTarget() {
		return this.armyEntity.getTarget() != null && this.armyEntity.getTarget().isAlive();
	}

	@Override
	public void stop() {
		super.stop();
		this.armyEntity.setAttacking(false);
		this.armyEntity.setTarget(null);
		this.seeingTargetTicker = 0;
		if (this.armyEntity.isUsingItem()) {
			this.armyEntity.clearActiveItem();
			this.armyEntity.setCharging(false);
			CrossbowItem.setCharged(this.armyEntity.getActiveItem(), false);
		}
	}

	@Override
	public void tick() {
		LivingEntity target = this.armyEntity.getTarget();
		if (target == null) {
			return;
		}
		boolean canSeeTarget = this.armyEntity.getVisibilityCache().canSee(target);
		boolean remembersSeeingTarget = this.seeingTargetTicker > 0;
		if (canSeeTarget != remembersSeeingTarget) {
			this.seeingTargetTicker = 0;
		}

		if (canSeeTarget) {
			++this.seeingTargetTicker;
		} else {
			--this.seeingTargetTicker;
		}

		double squaredDistanceToTarget = this.armyEntity.squaredDistanceTo(target);
		boolean targetIsOutOfRange = (squaredDistanceToTarget > (double) this.squaredRange || this.seeingTargetTicker < 5) && this.chargedTicksLeft == 0;
		if (targetIsOutOfRange) {
			this.armyEntity.getNavigation().startMovingTo(target, this.isUncharged() ? this.speed : this.speed * 0.5D);
		} else {
			this.armyEntity.getNavigation().stop();
		}

		this.armyEntity.getLookControl().lookAt(target, 30.0F, 30.0F);
		if (this.stage == Stage.UNCHARGED) {
			if (!targetIsOutOfRange) {
				this.armyEntity.setCurrentHand(Hand.MAIN_HAND);
				this.stage = Stage.CHARGING;
				this.armyEntity.setCharging(true);
			}
		} else if (this.stage == Stage.CHARGING) {
			if (!this.armyEntity.isUsingItem()) {
				this.stage = Stage.UNCHARGED;
			}

			int itemUseTime = this.armyEntity.getItemUseTime();
			ItemStack crossbowStack = this.armyEntity.getActiveItem();
			if (itemUseTime >= CrossbowItem.getPullTime(crossbowStack)) {
				this.armyEntity.stopUsingItem();
				this.stage = Stage.CHARGED;
				this.chargedTicksLeft = 20 + this.armyEntity.getRandom().nextInt(20);
				this.armyEntity.setCharging(false);
			}
		} else if (this.stage == Stage.CHARGED) {
			--this.chargedTicksLeft;
			if (this.chargedTicksLeft == 0) {
				this.stage = Stage.READY_TO_ATTACK;
			}
		} else if (this.stage == Stage.READY_TO_ATTACK && canSeeTarget) {
			this.armyEntity.attack(target, 1.0F);
			ItemStack crossbowStack = this.armyEntity.getStackInHand(Hand.MAIN_HAND);
			CrossbowItem.setCharged(crossbowStack, false);
			this.stage = Stage.UNCHARGED;
		}
	}

	private boolean isUncharged() {
		return this.stage == Stage.UNCHARGED;
	}

	enum Stage
	{
		UNCHARGED,
		CHARGING,
		CHARGED,
		READY_TO_ATTACK
	}
}
