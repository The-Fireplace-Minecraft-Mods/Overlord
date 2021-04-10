package dev.the_fireplace.overlord.api.inventory;

import dev.the_fireplace.overlord.impl.inventory.InventorySearcherImpl;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

public interface InventorySearcher {
    static InventorySearcher getInstance() {
        //noinspection deprecation
        return InventorySearcherImpl.INSTANCE;
    }
    List<Integer> findSlotsMatching(Inventory container, Predicate<ItemStack> matcher);
    Map<Integer, Integer> getSlotsByPriority(Inventory container, ToIntFunction<ItemStack> priorityMapper);
    Map<Integer, Integer> getSlotsByPriorityOverZero(Inventory container, ToIntFunction<ItemStack> priorityMapper);
}
