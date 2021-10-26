package dev.the_fireplace.overlord.domain.inventory;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

public interface InventorySearcher
{
    boolean hasSlotMatching(Inventory container, Predicate<ItemStack> matcher);

    Integer getFirstSlotMatching(Inventory container, Predicate<ItemStack> matcher);

    List<Integer> getSlotsMatching(Inventory container, Predicate<ItemStack> matcher);

    Map<Integer, Integer> getSlotsMatchingByPriority(Inventory container, Predicate<ItemStack> matcher, ToIntFunction<ItemStack> priorityMapper);

    Map<Integer, Integer> getSlotsByPriority(Inventory container, ToIntFunction<ItemStack> priorityMapper);

    Map<Integer, Integer> getSlotsByPriorityOverZero(Inventory container, ToIntFunction<ItemStack> priorityMapper);
}
