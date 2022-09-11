package dev.the_fireplace.overlord.impl.inventory;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.domain.inventory.InventorySearcher;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

@Implementation
public final class InventorySearcherImpl implements InventorySearcher
{
    @Override
    public boolean hasSlotMatching(Container container, Predicate<ItemStack> matcher) {
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            if (matcher.test(container.getItem(slot))) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Integer getFirstSlotMatching(Container container, Predicate<ItemStack> matcher) {
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            if (matcher.test(container.getItem(slot))) {
                return slot;
            }
        }

        return null;
    }

    @Override
    public List<Integer> getSlotsMatching(Container container, Predicate<ItemStack> matcher) {
        IntList slotList = new IntArrayList();

        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            if (matcher.test(container.getItem(slot))) {
                slotList.add(slot);
            }
        }

        return slotList;
    }

    /**
     * @return Map of slot -> priority
     */
    @Override
    public Map<Integer, Integer> getSlotsMatchingByPriority(Container container, Predicate<ItemStack> matcher, ToIntFunction<ItemStack> priorityMapper) {
        IntList slotList = new IntArrayList();

        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            if (matcher.test(container.getItem(slot))) {
                slotList.add(slot);
            }
        }

        Map<Integer, Integer> slotPriorityMap = new Int2IntOpenHashMap();

        for (int slot : slotList) {
            slotPriorityMap.put(slot, priorityMapper.applyAsInt(container.getItem(slot)));
        }

        slotPriorityMap = sortByValue(slotPriorityMap);

        return slotPriorityMap;
    }

    /**
     * @return Map of slot -> priority
     */
    @Override
    public Map<Integer, Integer> getSlotsByPriority(Container container, ToIntFunction<ItemStack> priorityMapper) {
        Map<Integer, Integer> slotPriorityMap = new Int2IntOpenHashMap();

        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            slotPriorityMap.put(slot, priorityMapper.applyAsInt(container.getItem(slot)));
        }

        slotPriorityMap = sortByValue(slotPriorityMap);

        return slotPriorityMap;
    }

    /**
     * @return Map of slot -> priority
     */
    @Override
    public Map<Integer, Integer> getSlotsByPriorityOverZero(Container container, ToIntFunction<ItemStack> priorityMapper) {
        Map<Integer, Integer> slotPriorityMap = new Int2IntOpenHashMap();

        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            int priority = priorityMapper.applyAsInt(container.getItem(slot));
            if (priority > 0) {
                slotPriorityMap.put(slot, priority);
            }
        }

        slotPriorityMap = sortByValue(slotPriorityMap);

        return slotPriorityMap;
    }

    /**
     * @return Map of slot -> priority
     */
    @Nonnull
    private Map<Integer, Integer> sortByValue(Map<Integer, Integer> slotPriorityMap) {
        slotPriorityMap = slotPriorityMap.entrySet().stream()
            .sorted((i1, i2) -> Integer.compare(i2.getValue(), i1.getValue()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        return slotPriorityMap;
    }
}
