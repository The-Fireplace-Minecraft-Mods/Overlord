package dev.the_fireplace.overlord.entity.ai.goal.target;

import dev.the_fireplace.overlord.entity.ArmyEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.EnumSet;

public class ArmyAttackWithOwnerGoal extends TargetGoal
{
    private final ArmyEntity tameable;
    private LivingEntity attacking;
    private int lastAttackTime;

    public ArmyAttackWithOwnerGoal(ArmyEntity tameable) {
        super(tameable, false);
        this.tameable = tameable;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    public boolean canUse() {
        LivingEntity livingEntity = this.tameable.getOwner();
        if (livingEntity == null) {
            return false;
        } else {
            this.attacking = livingEntity.getLastHurtMob();
            int i = livingEntity.getLastHurtMobTimestamp();
            return i != this.lastAttackTime && this.canAttack(this.attacking, TargetingConditions.DEFAULT) && this.tameable.wantsToAttack(this.attacking, livingEntity);
        }
    }

    public void start() {
        this.mob.setTarget(this.attacking);
        LivingEntity livingEntity = this.tameable.getOwner();
        if (livingEntity != null) {
            this.lastAttackTime = livingEntity.getLastHurtMobTimestamp();
        }

        super.start();
    }
}
