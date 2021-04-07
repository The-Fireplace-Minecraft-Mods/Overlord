package dev.the_fireplace.overlord.api.inventory;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;
import java.util.function.ToIntFunction;

public interface InventorySearcher {

    IntList findSlotsMatching(Inventory container, Predicate<ItemStack> matcher);
    Int2IntMap getSlotsByPriority(Inventory container, ToIntFunction<ItemStack> priorityMapper);
    Int2IntMap getSlotsByPriorityOverZero(Inventory container, ToIntFunction<ItemStack> priorityMapper);
}
