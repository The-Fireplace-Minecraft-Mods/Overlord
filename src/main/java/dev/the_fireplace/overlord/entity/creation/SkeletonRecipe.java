package dev.the_fireplace.overlord.entity.creation;

import com.google.common.collect.Maps;
import dev.the_fireplace.overlord.domain.entity.creation.SkeletonIngredient;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class SkeletonRecipe
{
    private final SkeletonComponent essentialsComponent;
    private final SkeletonComponent musclesComponent;
    private final SkeletonComponent skinComponent;
    private final SkeletonComponent playerColorComponent;

    public SkeletonRecipe(
        SkeletonComponent essentialsComponent,
        SkeletonComponent musclesComponent,
        SkeletonComponent skinComponent,
        SkeletonComponent playerColorComponent
    ) {
        this.essentialsComponent = essentialsComponent;
        this.musclesComponent = musclesComponent;
        this.skinComponent = skinComponent;
        this.playerColorComponent = playerColorComponent;
    }

    public boolean hasEssentialContents(Inventory inventory) {
        return hasIngredients(inventory, essentialsComponent.getIngredients());
    }

    public Collection<ItemStack> processEssentialIngredients(Inventory inventory) {
        processIngredients(inventory, essentialsComponent.getIngredients());
        return essentialsComponent.getByproducts();
    }

    public boolean hasSkinContents(Inventory inventory) {
        return hasIngredients(inventory, skinComponent.getIngredients());
    }

    public Collection<ItemStack> processSkinIngredients(Inventory inventory) {
        processIngredients(inventory, skinComponent.getIngredients());
        return skinComponent.getByproducts();
    }

    public boolean hasMuscleContents(Inventory inventory) {
        return hasIngredients(inventory, musclesComponent.getIngredients());
    }

    public Collection<ItemStack> processMuscleIngredients(Inventory inventory) {
        processIngredients(inventory, musclesComponent.getIngredients());
        return musclesComponent.getByproducts();
    }

    public boolean hasPlayerColorContents(Inventory inventory) {
        return hasIngredients(inventory, playerColorComponent.getIngredients());
    }

    public Collection<ItemStack> processPlayerColorIngredients(Inventory inventory) {
        processIngredients(inventory, playerColorComponent.getIngredients());
        return playerColorComponent.getByproducts();
    }

    public int getTotalMaxHealth(boolean hasSkin, boolean hasMuscles, boolean hasPlayerColor) {
        return essentialsComponent.getMaxHealth()
            + (hasSkin ? skinComponent.getMaxHealth() : 0)
            + (hasMuscles ? musclesComponent.getMaxHealth() : 0)
            + (hasPlayerColor ? playerColorComponent.getMaxHealth() : 0);
    }

    private boolean hasIngredients(Inventory inventory, Collection<SkeletonIngredient> ingredients) {
        Map<SkeletonIngredient, Integer> ingredientCounts = Maps.newHashMap();
        for (int slot = 0; slot < inventory.getInvSize(); slot++) {
            ItemStack stack = inventory.getInvStack(slot);
            if (!stack.isEmpty()) {
                for (SkeletonIngredient ingredient : ingredients) {
                    if (ingredient.matches(stack)) {
                        int existingCount = ingredientCounts.getOrDefault(ingredient, 0);
                        ingredientCounts.put(ingredient, existingCount + stack.getCount());
                        break;
                    }
                }
            }
        }
        for (SkeletonIngredient ingredient : ingredients) {
            if (ingredientCounts.getOrDefault(ingredient, 0) < ingredient.getRequiredCount()) {
                return false;
            }
        }
        return true;
    }

    private void processIngredients(Inventory inventory, Collection<SkeletonIngredient> ingredients) {
        Map<SkeletonIngredient, Integer> ingredientCountsRemaining = ingredients.stream().collect(Collectors.toConcurrentMap(ingredient -> ingredient, SkeletonIngredient::getRequiredCount));
        for (int slot = 0; slot < inventory.getInvSize(); slot++) {
            ItemStack stack = inventory.getInvStack(slot);
            if (!stack.isEmpty()) {
                for (Map.Entry<SkeletonIngredient, Integer> ingredientEntry : ingredientCountsRemaining.entrySet()) {
                    SkeletonIngredient ingredient = ingredientEntry.getKey();
                    Integer remainingCount = ingredientEntry.getValue();
                    if (remainingCount > 0 && ingredient.matches(stack)) {
                        ItemStack takenStack;
                        if (stack.getCount() > remainingCount) {
                            takenStack = stack.split(remainingCount);
                        } else {
                            takenStack = inventory.removeInvStack(slot);
                        }
                        int newRemainingCount = remainingCount - takenStack.getCount();
                        ingredientCountsRemaining.put(ingredient, newRemainingCount);
                    }
                }
            }
        }
    }

    public SkeletonComponent getEssentialsComponent() {
        return essentialsComponent.copy();
    }

    public SkeletonComponent getMusclesComponent() {
        return musclesComponent.copy();
    }

    public SkeletonComponent getSkinComponent() {
        return skinComponent.copy();
    }

    public SkeletonComponent getPlayerColorComponent() {
        return playerColorComponent.copy();
    }

    public boolean hasMuscles() {
        return !musclesComponent.getIngredients().isEmpty();
    }

    public boolean hasSkin() {
        return !skinComponent.getIngredients().isEmpty();
    }

    public boolean hasPlayerColor() {
        return !playerColorComponent.getIngredients().isEmpty();
    }
}
