package dev.the_fireplace.overlord.entity.ai.goal.task;

import dev.the_fireplace.annotateddi.api.DIContainer;
import dev.the_fireplace.overlord.domain.inventory.InventorySearcher;
import dev.the_fireplace.overlord.entity.ArmyEntity;
import dev.the_fireplace.overlord.entity.ai.goal.equipment.SwapEquipmentGoal;

public abstract class TaskGoal extends SwapEquipmentGoal
{
    protected final InventorySearcher inventorySearcher;

    public TaskGoal(ArmyEntity armyEntity) {
        super(armyEntity);
        this.inventorySearcher = DIContainer.get().getInstance(InventorySearcher.class);
    }

    @Override
    public boolean canStart() {
        return super.canStart()
            && notInCombat();
    }

    protected boolean notInCombat() {
        return armyEntity.getTarget() == null;
    }
}
