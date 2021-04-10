package dev.the_fireplace.overlord.impl.inventory;

import dev.the_fireplace.overlord.api.inventory.InventorySearcher;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

public final class InventorySearcherImpl implements InventorySearcher {
    @Deprecated
    public static final InventorySearcher INSTANCE = new InventorySearcherImpl();
    private InventorySearcherImpl(){}

    @Override
    public List<Integer> findSlotsMatching(Inventory container, Predicate<ItemStack> matcher) {
        IntList slotList = new IntArrayList();

        for (int slot = 0; slot < container.getInvSize(); slot++) {
            if (matcher.test(container.getInvStack(slot))) {
                slotList.add(slot);
            }
        }
        
        return slotList;
    }

    @Override
    public Map<Integer, Integer> getSlotsByPriority(Inventory container, ToIntFunction<ItemStack> priorityMapper) {
        Map<Integer, Integer> slotPriorityMap = new Int2IntOpenHashMap();

        for (int slot = 0; slot < container.getInvSize(); slot++) {
            slotPriorityMap.put(slot, priorityMapper.applyAsInt(container.getInvStack(slot)));
        }

        slotPriorityMap = sortByValue(slotPriorityMap);

        return slotPriorityMap;
    }

    @Override
    public Map<Integer, Integer> getSlotsByPriorityOverZero(Inventory container, ToIntFunction<ItemStack> priorityMapper) {
        Map<Integer, Integer> slotPriorityMap = new Int2IntOpenHashMap();

        for (int slot = 0; slot < container.getInvSize(); slot++) {
            int priority = priorityMapper.applyAsInt(container.getInvStack(slot));
            if (priority > 0) {
                slotPriorityMap.put(slot, priority);
            }
        }

        slotPriorityMap = sortByValue(slotPriorityMap);

        return slotPriorityMap;
    }

    @Nonnull
    private Map<Integer, Integer> sortByValue(Map<Integer, Integer> slotPriorityMap) {
        slotPriorityMap = slotPriorityMap.entrySet().stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        return slotPriorityMap;
    }
}
