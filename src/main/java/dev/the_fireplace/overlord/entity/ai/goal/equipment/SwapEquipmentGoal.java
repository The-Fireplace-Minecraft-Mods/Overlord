package dev.the_fireplace.overlord.entity.ai.goal.equipment;

import dev.the_fireplace.overlord.entity.ArmyEntity;
import net.minecraft.entity.ai.goal.Goal;

public abstract class SwapEquipmentGoal extends Goal
{
    protected final ArmyEntity armyEntity;

    public SwapEquipmentGoal(ArmyEntity armyEntity) {
        this.armyEntity = armyEntity;
    }

    @Override
    public void start() {
        super.start();
        armyEntity.setSwappingEquipment(true);
    }

    @Override
    public void stop() {
        super.stop();
        armyEntity.setSwappingEquipment(false);
    }

    @Override
    public boolean canStart() {
        return !armyEntity.isSwappingEquipment();
    }

    @Override
    public abstract boolean shouldContinue();
}
