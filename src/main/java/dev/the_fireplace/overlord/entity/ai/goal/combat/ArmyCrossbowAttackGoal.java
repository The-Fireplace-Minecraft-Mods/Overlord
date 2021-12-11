package dev.the_fireplace.overlord.entity.ai.goal.combat;

import dev.the_fireplace.overlord.entity.ArmyEntity;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

public class ArmyCrossbowAttackGoal<T extends ArmyEntity & RangedAttackMob & CrossbowUser> extends AbstractArmyCrossbowAttackGoal<T>
{
	private final double speed;

	public ArmyCrossbowAttackGoal(T armyEntity, double speed, float range) {
		super(armyEntity, range);
		this.speed = speed;
		this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
	}

	@Override
	protected void handleMoveToTarget(LivingEntity target, boolean targetIsOutOfRange) {
		if (targetIsOutOfRange) {
			this.armyEntity.getNavigation().startMovingTo(target, this.isUncharged() ? this.speed : this.speed * 0.5D);
		} else {
			this.armyEntity.getNavigation().stop();
		}
	}
}
