package dev.the_fireplace.overlord.entity.ai.goal.equipment;

import dev.the_fireplace.overlord.entity.ArmyEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public abstract class SwapEquipmentGoal extends Goal
{
    protected final ArmyEntity armyEntity;
    private long lastUpdateTime;

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

    /**
     * DO NOT call this from shouldContinue, it has a delay built in to reduce lag. Always call super.canStart() before your own canStart conditions
     */
    @Override
    public boolean canUse() {
        long worldTime = this.armyEntity.level.getGameTime();
        if (worldTime - this.lastUpdateTime < 10) {
            return false;
        }
        this.lastUpdateTime = worldTime;
        return !armyEntity.isSwappingEquipment();
    }

    @Override
    public abstract boolean canContinueToUse();
}
