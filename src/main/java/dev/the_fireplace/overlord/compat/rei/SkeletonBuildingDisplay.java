package dev.the_fireplace.overlord.compat.rei;

import dev.the_fireplace.annotateddi.api.DIContainer;
import dev.the_fireplace.lib.api.chat.injectables.TextStyles;
import dev.the_fireplace.overlord.domain.entity.creation.SkeletonIngredient;
import dev.the_fireplace.overlord.entity.creation.SkeletonRecipe;
import dev.the_fireplace.overlord.entity.creation.ingredient.ItemIngredient;
import dev.the_fireplace.overlord.entity.creation.ingredient.TagIngredient;
import dev.the_fireplace.overlord.item.OverlordItems;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class SkeletonBuildingDisplay implements Display
{
    private final Collection<EntryIngredient> essentialIngredients;
    private final Collection<EntryIngredient> skinIngredients;
    private final Collection<EntryIngredient> muscleIngredients;
    private final Collection<EntryIngredient> playerColorIngredients;
    private final Collection<EntryIngredient> essentialByproducts;
    private final Collection<EntryIngredient> skinByproducts;
    private final Collection<EntryIngredient> muscleByproducts;
    private final Collection<EntryIngredient> playerColorByproducts;

    private final boolean includeMuscle;
    private final boolean includeSkin;
    private final boolean includePlayerColor;

    private final int totalMaxHealth;

    private final TextStyles textStyles;

    public SkeletonBuildingDisplay(SkeletonRecipe skeletonRecipe, boolean includeMuscle, boolean includeSkin, boolean includePlayerColor) {
        this.essentialIngredients = this.convertSkeletonIngredientsToEntryIngredients(skeletonRecipe.getEssentialsComponent().getIngredients());
        this.skinIngredients = this.convertSkeletonIngredientsToEntryIngredients(skeletonRecipe.getSkinComponent().getIngredients());
        this.muscleIngredients = this.convertSkeletonIngredientsToEntryIngredients(skeletonRecipe.getMusclesComponent().getIngredients());
        this.playerColorIngredients = this.convertSkeletonIngredientsToEntryIngredients(skeletonRecipe.getPlayerColorComponent().getIngredients());
        this.essentialByproducts = convertStacksToEntryIngredients(skeletonRecipe.getEssentialsComponent().getByproducts());
        this.skinByproducts = convertStacksToEntryIngredients(skeletonRecipe.getSkinComponent().getByproducts());
        this.muscleByproducts = convertStacksToEntryIngredients(skeletonRecipe.getMusclesComponent().getByproducts());
        this.playerColorByproducts = convertStacksToEntryIngredients(skeletonRecipe.getPlayerColorComponent().getByproducts());
        this.includeMuscle = includeMuscle;
        this.includeSkin = includeSkin;
        this.includePlayerColor = includePlayerColor;
        this.totalMaxHealth = skeletonRecipe.getTotalMaxHealth(includeSkin, includeMuscle, includePlayerColor);

        this.textStyles = DIContainer.get().getInstance(TextStyles.class);
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        List<EntryIngredient> entries = new ArrayList<>(essentialIngredients);
        if (includeSkin) {
            entries.addAll(skinIngredients);
        }
        if (includeMuscle) {
            entries.addAll(muscleIngredients);
        }
        if (includePlayerColor) {
            entries.addAll(playerColorIngredients);
        }

        return entries;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        List<EntryIngredient> entries = new ArrayList<>(essentialByproducts);
        if (includeSkin) {
            entries.addAll(skinByproducts);
        }
        if (includeMuscle) {
            entries.addAll(muscleByproducts);
        }
        if (includePlayerColor) {
            entries.addAll(playerColorByproducts);
        }

        return entries;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return OverlordReiCategories.SKELETON_BUILDING_CATEGORY;
    }

    public EntryStack<ItemStack> getSkeletonHead() {
        EntryStack<ItemStack> entryStack = EntryStacks.of(getSkullItemStack());
        entryStack.tooltip(getSkullTooltip());
        return entryStack;
    }

    private ItemStack getSkullItemStack() {
        ItemStack skullStack;
        if (includeSkin) {
            if (includePlayerColor) {
                skullStack = new ItemStack(Items.PLAYER_HEAD);
            } else if (includeMuscle) {
                skullStack = new ItemStack(OverlordItems.FLESH_MUSCLE_SKELETON_SKULL);
            } else {
                skullStack = new ItemStack(OverlordItems.FLESH_SKELETON_SKULL);
            }
        } else if (includeMuscle) {
            skullStack = new ItemStack(OverlordItems.MUSCLE_SKELETON_SKULL);
        } else {
            skullStack = new ItemStack(Items.SKELETON_SKULL);
        }

        skullStack.setCustomName(new TranslatableText("entity.overlord.owned_skeleton"));

        return skullStack;
    }

    private List<Text> getSkullTooltip() {
        Text yes = new TranslatableText("gui.overlord.rei.tooltip.yes");
        Text no = new TranslatableText("gui.overlord.rei.tooltip.no");
        return List.of(
            new TranslatableText("gui.overlord.rei.tooltip.health", this.totalMaxHealth).setStyle(textStyles.red()),
            new TranslatableText("gui.overlord.rei.tooltip.muscles", this.includeMuscle ? yes : no).setStyle(getStyleByBoolean(this.includeMuscle)),
            new TranslatableText("gui.overlord.rei.tooltip.skin", this.includeSkin ? yes : no).setStyle(getStyleByBoolean(this.includeSkin)),
            new TranslatableText("gui.overlord.rei.tooltip.player_appearance", this.includePlayerColor ? yes : no).setStyle(getStyleByBoolean(this.includePlayerColor))
        );
    }

    private Style getStyleByBoolean(boolean isPresent) {
        return isPresent ? textStyles.green() : textStyles.redDark();
    }

    private Collection<EntryIngredient> convertSkeletonIngredientsToEntryIngredients(Collection<SkeletonIngredient> ingredients) {
        Collection<EntryIngredient> entryIngredients = new ArrayList<>(ingredients.size());

        for (SkeletonIngredient ingredient : ingredients) {
            if (ingredient instanceof ItemIngredient itemIngredient) {
                Item item = itemIngredient.getItem();
                int remainingCount = itemIngredient.getRequiredCount();
                while (remainingCount > 0) {
                    int stackCount = Math.min(remainingCount, item.getMaxCount());
                    entryIngredients.add(EntryIngredients.of(new ItemStack(item, stackCount)));
                    remainingCount -= stackCount;
                }
            } else if (ingredient instanceof TagIngredient tagIngredient) {
                Ingredient recipeIngredient = Ingredient.fromTag(tagIngredient.getTag());
                int remainingCount = tagIngredient.getRequiredCount();
                while (remainingCount > 0) {
                    int stackCount = Math.min(remainingCount, 64);
                    entryIngredients.add(ingredientStacksWithCount(recipeIngredient, stackCount));
                    remainingCount -= stackCount;
                }
            } else {
                throw new IllegalStateException("Unknown skeleton ingredient type!");
            }
        }

        return entryIngredients;
    }

    private static EntryIngredient ingredientStacksWithCount(Ingredient ingredient, int count) {
        if (ingredient.isEmpty()) {
            return EntryIngredient.empty();
        }
        ItemStack[] matchingStacks = ingredient.getMatchingStacks();
        if (matchingStacks.length == 0) {
            return EntryIngredient.empty();
        }
        if (matchingStacks.length == 1) {
            ItemStack matchingStack = matchingStacks[0];
            matchingStack.setCount(count);
            return EntryIngredient.of(EntryStacks.of(matchingStack));
        }
        EntryIngredient.Builder result = EntryIngredient.builder(matchingStacks.length);
        for (ItemStack matchingStack : matchingStacks) {
            if (!matchingStack.isEmpty()) {
                matchingStack.setCount(count);
                result.add(EntryStacks.of(matchingStack));
            }
        }
        return result.build();
    }

    private Collection<EntryIngredient> convertStacksToEntryIngredients(Collection<ItemStack> itemStacks) {
        Collection<EntryIngredient> entryIngredients = new HashSet<>(itemStacks.size());
        //TODO verify that these have been appropriately split into stacks by max stack size

        for (ItemStack stack : itemStacks) {
            entryIngredients.add(EntryIngredients.of(stack));
        }

        return entryIngredients;
    }
}
