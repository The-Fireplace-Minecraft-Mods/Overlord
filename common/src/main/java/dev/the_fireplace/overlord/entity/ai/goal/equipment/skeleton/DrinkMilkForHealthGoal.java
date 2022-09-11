package dev.the_fireplace.overlord.entity.ai.goal.equipment.skeleton;

import dev.the_fireplace.overlord.domain.entity.AnimatedMilkDrinker;
import dev.the_fireplace.overlord.entity.ArmyEntity;

public class DrinkMilkForHealthGoal<T extends ArmyEntity & AnimatedMilkDrinker> extends DrinkMilkGoal<T>
{
    public DrinkMilkForHealthGoal(T armyEntity) {
        super(armyEntity);
    }

    @Override
    public boolean canUse() {
        return super.canUse() && armyEntity.getHealth() < armyEntity.getMaxHealth() / 4.0;
    }
}
