package dev.the_fireplace.overlord.entity.ai.goal.combat;

import dev.the_fireplace.annotateddi.api.DIContainer;
import dev.the_fireplace.overlord.entity.ArmyEntity;
import dev.the_fireplace.overlord.entity.ai.goal.AIEquipmentHelper;
import dev.the_fireplace.overlord.mixin.MeleeAttackGoalAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.Hand;

public class ArmyMeleeAttackGoal extends MeleeAttackGoal
{
    protected final ArmyEntity armyEntity;
    protected final AIEquipmentHelper equipmentHelper;

    public ArmyMeleeAttackGoal(ArmyEntity armyEntity, double speed, boolean pauseWhenMobIdle) {
        super(armyEntity, speed, pauseWhenMobIdle);
        this.armyEntity = armyEntity;
        this.equipmentHelper = DIContainer.get().getInstance(AIEquipmentHelper.class);
    }

    @Override
    public boolean canStart() {
        return super.canStart() && shouldAttackWithMelee();
    }

    private boolean shouldAttackWithMelee() {
        return !equipmentHelper.isUsingRanged(armyEntity) || !equipmentHelper.hasAmmoEquipped(armyEntity);
    }

    @Override
    public boolean shouldContinue() {
        return super.shouldContinue() && shouldAttackWithMelee();
    }

    @Override
    protected void attack(LivingEntity target, double squaredDistance) {
        double d = this.getSquaredMaxAttackDistance(target);
        if (squaredDistance <= d && this.isCooledDown()) {
            if (!this.mob.getEntityWorld().isClient()) {
                double attackSpeed = this.mob.getAttributeValue(EntityAttributes.GENERIC_ATTACK_SPEED);
                ((MeleeAttackGoalAccessor) this).setCooldown(Math.max(1, (int) Math.ceil(20D / attackSpeed - 0.5)));
            }
            this.mob.swingHand(Hand.MAIN_HAND);
            this.mob.tryAttack(target);
        }
    }
}
