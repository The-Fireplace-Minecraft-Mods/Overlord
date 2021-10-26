package dev.the_fireplace.overlord.entity.ai.goal.target;

import dev.the_fireplace.overlord.entity.ArmyEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;

import java.util.EnumSet;

public class ArmyTrackOwnerAttackerGoal extends TrackTargetGoal
{
    private final ArmyEntity tameable;
    private LivingEntity attacker;
    private int lastAttackedTime;

    public ArmyTrackOwnerAttackerGoal(ArmyEntity tameable) {
        super(tameable, false);
        this.tameable = tameable;
        this.setControls(EnumSet.of(Goal.Control.TARGET));
    }

    public boolean canStart() {
        LivingEntity livingEntity = this.tameable.getOwner();
        if (livingEntity == null) {
            return false;
        } else {
            this.attacker = livingEntity.getAttacker();
            int i = livingEntity.getLastAttackedTime();
            return i != this.lastAttackedTime && this.canTrack(this.attacker, TargetPredicate.DEFAULT) && this.tameable.canAttackWithOwner(this.attacker, livingEntity);
        }
    }

    public void start() {
        this.mob.setTarget(this.attacker);
        LivingEntity livingEntity = this.tameable.getOwner();
        if (livingEntity != null) {
            this.lastAttackedTime = livingEntity.getLastAttackedTime();
        }

        super.start();
    }
}
