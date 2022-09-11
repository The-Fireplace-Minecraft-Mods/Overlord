package dev.the_fireplace.overlord.entity.ai.goal.target;

import dev.the_fireplace.overlord.entity.ArmyEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.EnumSet;

public class ArmyTrackOwnerAttackerGoal extends TargetGoal
{
    private final ArmyEntity tameable;
    private LivingEntity attacker;
    private int lastAttackedTime;

    public ArmyTrackOwnerAttackerGoal(ArmyEntity tameable) {
        super(tameable, false);
        this.tameable = tameable;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    public boolean canUse() {
        LivingEntity livingEntity = this.tameable.getOwner();
        if (livingEntity == null) {
            return false;
        } else {
            this.attacker = livingEntity.getLastHurtByMob();
            int i = livingEntity.getLastHurtByMobTimestamp();
            return i != this.lastAttackedTime && this.canAttack(this.attacker, TargetingConditions.DEFAULT) && this.tameable.wantsToAttack(this.attacker, livingEntity);
        }
    }

    public void start() {
        this.mob.setTarget(this.attacker);
        LivingEntity livingEntity = this.tameable.getOwner();
        if (livingEntity != null) {
            this.lastAttackedTime = livingEntity.getLastHurtByMobTimestamp();
        }

        super.start();
    }
}
