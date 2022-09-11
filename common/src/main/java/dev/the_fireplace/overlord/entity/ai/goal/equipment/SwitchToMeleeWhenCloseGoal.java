package dev.the_fireplace.overlord.entity.ai.goal.equipment;

import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.entity.ArmyEntity;
import dev.the_fireplace.overlord.entity.ai.goal.AIEquipmentHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.pathfinder.Path;

public class SwitchToMeleeWhenCloseGoal extends SwapEquipmentGoal
{
    protected final byte switchDistance;
    protected final boolean useShield;
    protected final AIEquipmentHelper equipmentHelper;

    protected byte postSwapCooldownTicks;

    public SwitchToMeleeWhenCloseGoal(ArmyEntity armyEntity, byte switchDistance, boolean useShield) {
        super(armyEntity);
        this.switchDistance = switchDistance;
        this.useShield = useShield;
        this.equipmentHelper = OverlordConstants.getInjector().getInstance(AIEquipmentHelper.class);
    }

    @Override
    public boolean canUse() {
        if (!super.canUse()) {
            return false;
        }
        LivingEntity target = armyEntity.getTarget();
        if (!isInSwitchToMeleeDistance(target) || !equipmentHelper.isUsingRanged(armyEntity)) {
            return false;
        }
        Path path = this.armyEntity.getNavigation().createPath(target, 0);
        if (path != null) {
            return true;
        } else {
            return this.getSquaredMaxAttackDistance(target) >= this.armyEntity.distanceToSqr(target);
        }
    }

    private boolean isInSwitchToMeleeDistance(LivingEntity target) {
        return target != null
            && armyEntity.distanceToSqr(target) < (switchDistance * switchDistance);
    }

    protected double getSquaredMaxAttackDistance(LivingEntity entity) {
        return this.armyEntity.getBbWidth() * 2.0F * this.armyEntity.getBbWidth() * 2.0F + entity.getBbWidth();
    }

    @Override
    public void start() {
        super.start();
        this.postSwapCooldownTicks = 0;
    }

    @Override
    public boolean canContinueToUse() {
        return this.postSwapCooldownTicks > 0
            || equipmentHelper.isUsingRanged(armyEntity)
            || (this.useShield && equipmentHelper.shouldEquipShield(armyEntity));
    }

    @Override
    public void tick() {
        if (this.postSwapCooldownTicks > 0) {
            this.postSwapCooldownTicks--;
            return;
        }

        if (equipmentHelper.isUsingRanged(armyEntity)) {
            equipmentHelper.equipMeleeWeapon(armyEntity);
            this.postSwapCooldownTicks = this.armyEntity.getEquipmentSwapTicks();
            return;
        }
        if (this.useShield && equipmentHelper.shouldEquipShield(armyEntity)) {
            equipmentHelper.equipUsableShield(armyEntity);
            this.postSwapCooldownTicks = this.armyEntity.getEquipmentSwapTicks();
        }
    }
}
