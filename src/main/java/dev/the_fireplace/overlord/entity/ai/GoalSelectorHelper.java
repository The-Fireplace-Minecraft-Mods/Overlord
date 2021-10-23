package dev.the_fireplace.overlord.entity.ai;

import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.mixin.GoalSelectorAccessor;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.WeightedGoal;

public class GoalSelectorHelper
{
    public static void clear(GoalSelector goalSelector) {
        if (goalSelector instanceof GoalSelectorAccessor) {
            ((GoalSelectorAccessor) goalSelector).getGoals().stream()
                .filter(WeightedGoal::isRunning)
                .forEach(WeightedGoal::stop);
            ((GoalSelectorAccessor) goalSelector).getGoals().clear();
        } else {
            Overlord.getLogger().error("Goal Selector does not have the accessor! " + goalSelector.getClass().toString());
        }
    }
}
