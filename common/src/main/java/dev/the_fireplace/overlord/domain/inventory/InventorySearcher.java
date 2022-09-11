package dev.the_fireplace.overlord.domain.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

public interface InventorySearcher
{
    boolean hasSlotMatching(Container container, Predicate<ItemStack> matcher);

    Integer getFirstSlotMatching(Container container, Predicate<ItemStack> matcher);

    List<Integer> getSlotsMatching(Container container, Predicate<ItemStack> matcher);

    Map<Integer, Integer> getSlotsMatchingByPriority(Container container, Predicate<ItemStack> matcher, ToIntFunction<ItemStack> priorityMapper);

    Map<Integer, Integer> getSlotsByPriority(Container container, ToIntFunction<ItemStack> priorityMapper);

    Map<Integer, Integer> getSlotsByPriorityOverZero(Container container, ToIntFunction<ItemStack> priorityMapper);
}
