package dev.the_fireplace.overlord.entity.ai;

import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.mixin.GoalSelectorAccessor;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;

public class GoalSelectorHelper
{
    public static void clear(GoalSelector goalSelector) {
        if (goalSelector instanceof GoalSelectorAccessor) {
            ((GoalSelectorAccessor) goalSelector).getAvailableGoals().stream()
                .filter(WrappedGoal::isRunning)
                .forEach(WrappedGoal::stop);
            ((GoalSelectorAccessor) goalSelector).getAvailableGoals().clear();
        } else {
            OverlordConstants.getLogger().error("Goal Selector does not have the accessor! " + goalSelector.getClass().toString());
        }
    }
}
