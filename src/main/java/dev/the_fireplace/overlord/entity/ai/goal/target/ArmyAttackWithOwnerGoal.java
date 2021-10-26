package dev.the_fireplace.overlord.entity.ai.goal.target;

import dev.the_fireplace.overlord.entity.ArmyEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;

import java.util.EnumSet;

public class ArmyAttackWithOwnerGoal extends TrackTargetGoal
{
    private final ArmyEntity tameable;
    private LivingEntity attacking;
    private int lastAttackTime;

    public ArmyAttackWithOwnerGoal(ArmyEntity tameable) {
        super(tameable, false);
        this.tameable = tameable;
        this.setControls(EnumSet.of(Goal.Control.TARGET));
    }

    public boolean canStart() {
        LivingEntity livingEntity = this.tameable.getOwner();
        if (livingEntity == null) {
            return false;
        } else {
            this.attacking = livingEntity.getAttacking();
            int i = livingEntity.getLastAttackTime();
            return i != this.lastAttackTime && this.canTrack(this.attacking, TargetPredicate.DEFAULT) && this.tameable.canAttackWithOwner(this.attacking, livingEntity);
        }
    }

    public void start() {
        this.mob.setTarget(this.attacking);
        LivingEntity livingEntity = this.tameable.getOwner();
        if (livingEntity != null) {
            this.lastAttackTime = livingEntity.getLastAttackTime();
        }

        super.start();
    }
}
