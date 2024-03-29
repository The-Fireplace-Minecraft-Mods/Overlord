package dev.the_fireplace.overlord.entity.ai.goal.combat;

import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.entity.ArmyEntity;
import dev.the_fireplace.overlord.entity.ai.goal.AIEquipmentHelper;
import dev.the_fireplace.overlord.mixin.MeleeAttackGoalAccessor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class ArmyMeleeAttackGoal extends MeleeAttackGoal
{
    protected final ArmyEntity armyEntity;
    protected final AIEquipmentHelper equipmentHelper;

    public ArmyMeleeAttackGoal(ArmyEntity armyEntity, double speed, boolean pauseWhenMobIdle) {
        super(armyEntity, speed, pauseWhenMobIdle);
        this.armyEntity = armyEntity;
        this.equipmentHelper = OverlordConstants.getInjector().getInstance(AIEquipmentHelper.class);
    }

    @Override
    public boolean canUse() {
        return super.canUse() && shouldAttackWithMelee();
    }

    private boolean shouldAttackWithMelee() {
        return !equipmentHelper.isUsingRanged(armyEntity) || !equipmentHelper.hasAmmoEquipped(armyEntity);
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && shouldAttackWithMelee();
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity target, double squaredDistance) {
        double d = this.getAttackReachSqr(target);
        if (squaredDistance <= d && this.isTimeToAttack()) {
            if (!this.mob.getCommandSenderWorld().isClientSide()) {
                double attackSpeed = this.mob.getAttributeValue(Attributes.ATTACK_SPEED);
                ((MeleeAttackGoalAccessor) this).setTicksUntilNextAttack(Math.max(1, (int) Math.ceil(20D / attackSpeed - 0.5)));
            }
            this.mob.swing(InteractionHand.MAIN_HAND);
            this.mob.doHurtTarget(target);
        }
    }
}
