package dev.the_fireplace.overlord.entity.ai.goal.equipment;

import dev.the_fireplace.annotateddi.api.DIContainer;
import dev.the_fireplace.overlord.entity.ArmyEntity;
import dev.the_fireplace.overlord.entity.ai.goal.AIEquipmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;

public class SwitchToMeleeWhenCloseGoal extends Goal
{
    protected final ArmyEntity armyEntity;
    protected final byte switchDistance;
    protected final boolean useShield;
    protected final AIEquipmentHelper equipmentHelper;

    protected byte postSwapCooldownTicks;

    public SwitchToMeleeWhenCloseGoal(ArmyEntity armyEntity, byte switchDistance, boolean useShield) {
        this.armyEntity = armyEntity;
        this.switchDistance = switchDistance;
        this.useShield = useShield;
        this.equipmentHelper = DIContainer.get().getInstance(AIEquipmentHelper.class);
    }

    @Override
    public boolean canStart() {
        LivingEntity target = armyEntity.getTarget();
        if (!isInSwitchToMeleeDistance(target) || !equipmentHelper.isUsingRanged(armyEntity)) {
            return false;
        }
        Path path = this.armyEntity.getNavigation().findPathTo(target, 0);
        if (path != null) {
            return true;
        } else {
            return this.getSquaredMaxAttackDistance(target) >= this.armyEntity.squaredDistanceTo(target);
        }
    }

    private boolean isInSwitchToMeleeDistance(LivingEntity target) {
        return target != null
            && armyEntity.squaredDistanceTo(target) < (switchDistance * switchDistance);
    }

    protected double getSquaredMaxAttackDistance(LivingEntity entity) {
        return this.armyEntity.getWidth() * 2.0F * this.armyEntity.getWidth() * 2.0F + entity.getWidth();
    }

    @Override
    public void start() {
        super.start();
        this.postSwapCooldownTicks = 0;
    }

    @Override
    public boolean shouldContinue() {
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
