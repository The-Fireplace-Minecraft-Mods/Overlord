package dev.the_fireplace.overlord.impl.rule;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.domain.data.objects.Squad;
import dev.the_fireplace.overlord.domain.rule.SquadEligibleItems;
import dev.the_fireplace.overlord.entity.ArmyEntity;
import dev.the_fireplace.overlord.entity.OwnedSkeletonEntity;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;

@Implementation
public final class SquadEligibleItemsImpl implements SquadEligibleItems
{
    //TODO once proxying is possible
    /*private final Squads squads;

    @Inject
    public SquadEligibleItems(Squads squads) {
        this.squads = squads;
    }*/

    @Override
    public Collection<ItemStack> getEligibleItems(Collection<Squad> squads, @Nullable Player player, @Nullable Entity armyEntity) {
        Collection<ItemStack> squadItems = new ArrayList<>();
        if (armyEntity instanceof ArmyEntity) {
            if (armyEntity instanceof OwnedSkeletonEntity) {
                squadItems.add(((OwnedSkeletonEntity) armyEntity).getAugmentBlockStack().copy());
            }
            Container entityInventory = ((ArmyEntity) armyEntity).getInventory();
            squadItems.addAll(getStacksFromInventory(entityInventory));
        }
        if (player != null) {
            Container playerInventory = player.inventory;
            squadItems.addAll(getStacksFromInventory(playerInventory));
        }
        for (Squad squad : squads) {
            squadItems.add(squad.getItem());
        }
        return reduceAndDeduplicate(squadItems);
    }

    @Override
    public ItemStack convertToSquadItem(ItemStack stack) {
        stack = stack.copy();
        stack.setCount(1);
        return stack;
    }

    private Collection<ItemStack> getStacksFromInventory(Container inventory) {
        Collection<ItemStack> inventoryItems = new ArrayList<>(inventory.getContainerSize() / 2);
        for (int slotIndex = 0; slotIndex < inventory.getContainerSize(); slotIndex++) {
            ItemStack stack = inventory.getItem(slotIndex);
            if (!stack.isEmpty()) {
                inventoryItems.add(stack.copy());
            }
        }
        return inventoryItems;
    }

    private Collection<ItemStack> reduceAndDeduplicate(Collection<ItemStack> itemStacks) {
        Collection<ItemStack> reducedStacks = new ArrayList<>(itemStacks.size() - 2);
        for (ItemStack stack : itemStacks) {
            if (stack.isEmpty()) {
                continue;
            }
            stack = convertToSquadItem(stack);

            ItemStack finalStack = stack;
            if (reducedStacks.stream().noneMatch(reducedStack -> ItemStack.matches(reducedStack, finalStack))) {
                reducedStacks.add(stack);
            }
        }

        return reducedStacks;
    }
}
