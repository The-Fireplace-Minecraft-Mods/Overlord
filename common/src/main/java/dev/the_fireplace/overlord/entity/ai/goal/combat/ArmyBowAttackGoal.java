package dev.the_fireplace.overlord.entity.ai.goal.combat;

import dev.the_fireplace.overlord.entity.ArmyEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;

import java.util.EnumSet;

public class ArmyBowAttackGoal<T extends ArmyEntity & RangedAttackMob> extends AbstractArmyBowAttackGoal<T>
{
    protected final double speed;
    protected boolean movingToLeft;
    protected boolean backward;

    public ArmyBowAttackGoal(T armyEntity, double speed, int attackInterval, float range) {
        super(armyEntity, attackInterval, range);
        this.speed = speed;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    protected void handleCombatMovement(LivingEntity target, double squaredDistanceToTarget) {
        if (!(squaredDistanceToTarget > (double) this.squaredRange) && this.targetSeeingTicker >= 20) {
            this.armyEntity.getNavigation().stop();
        } else {
            this.armyEntity.getNavigation().moveTo(target, this.speed);
        }

        if (this.combatTicks >= 20) {
            if ((double) this.armyEntity.getRandom().nextFloat() < 0.3D) {
                this.movingToLeft = !this.movingToLeft;
            }

            if ((double) this.armyEntity.getRandom().nextFloat() < 0.3D) {
                this.backward = !this.backward;
            }

            this.combatTicks = 0;
        }

        if (this.combatTicks > -1) {
            if (squaredDistanceToTarget > (double) (this.squaredRange * 0.75F)) {
                this.backward = false;
            } else if (squaredDistanceToTarget < (double) (this.squaredRange * 0.25F)) {
                this.backward = true;
            }

            this.armyEntity.getMoveControl().strafe(this.backward ? -0.5F : 0.5F, this.movingToLeft ? 0.5F : -0.5F);
        }
    }
}
