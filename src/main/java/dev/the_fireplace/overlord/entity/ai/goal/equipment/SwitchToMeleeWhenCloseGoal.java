package dev.the_fireplace.overlord.entity.ai.goal.equipment;

import dev.the_fireplace.annotateddi.api.DIContainer;
import dev.the_fireplace.overlord.entity.ArmyEntity;
import dev.the_fireplace.overlord.entity.ai.goal.AIEquipmentHelper;
import net.minecraft.entity.ai.goal.Goal;

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
        return isInMeleeDistance() && equipmentHelper.isUsingRanged(armyEntity);//TODO we still need AI to equip shield after picking one up - probably should avoid extra scan every tick so don't do it here
    }

    private boolean isInMeleeDistance() {
        return armyEntity.getTarget() != null
            && armyEntity.squaredDistanceTo(armyEntity.getTarget()) < (switchDistance * switchDistance);
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
