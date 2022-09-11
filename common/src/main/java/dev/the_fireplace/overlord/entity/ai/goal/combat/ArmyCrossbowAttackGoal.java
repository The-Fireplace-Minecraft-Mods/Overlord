package dev.the_fireplace.overlord.entity.ai.goal.combat;

import dev.the_fireplace.overlord.entity.ArmyEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.RangedAttackMob;

import java.util.EnumSet;

public class ArmyCrossbowAttackGoal<T extends ArmyEntity & RangedAttackMob & CrossbowAttackMob> extends AbstractArmyCrossbowAttackGoal<T>
{
    private final double speed;

    public ArmyCrossbowAttackGoal(T armyEntity, double speed, float range) {
        super(armyEntity, range);
        this.speed = speed;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    protected void handleMoveToTarget(LivingEntity target, boolean targetIsOutOfRange) {
        if (targetIsOutOfRange) {
            this.armyEntity.getNavigation().moveTo(target, this.isUncharged() ? this.speed : this.speed * 0.5D);
        } else {
            this.armyEntity.getNavigation().stop();
        }
    }
}
