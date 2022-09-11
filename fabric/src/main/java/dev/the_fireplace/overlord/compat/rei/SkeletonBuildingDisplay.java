package dev.the_fireplace.overlord.compat.rei;

import com.google.inject.Injector;
import dev.the_fireplace.lib.api.chat.injectables.TextStyles;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.domain.entity.creation.SkeletonIngredient;
import dev.the_fireplace.overlord.entity.creation.SkeletonRecipe;
import dev.the_fireplace.overlord.entity.creation.ingredient.ItemIngredient;
import dev.the_fireplace.overlord.entity.creation.ingredient.TagIngredient;
import dev.the_fireplace.overlord.item.OverlordItems;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeDisplay;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.*;

public class SkeletonBuildingDisplay implements RecipeDisplay
{
    private final Collection<List<EntryStack>> essentialIngredients;
    private final Collection<List<EntryStack>> skinIngredients;
    private final Collection<List<EntryStack>> muscleIngredients;
    private final Collection<List<EntryStack>> playerColorIngredients;
    private final Collection<List<EntryStack>> essentialByproducts;
    private final Collection<List<EntryStack>> skinByproducts;
    private final Collection<List<EntryStack>> muscleByproducts;
    private final Collection<List<EntryStack>> playerColorByproducts;

    private final boolean includeMuscle;
    private final boolean includeSkin;
    private final boolean includePlayerColor;

    private final int totalMaxHealth;

    private final TextStyles textStyles;
    private final OverlordItems overlordItems;

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

        Injector injector = OverlordConstants.getInjector();
        this.textStyles = injector.getInstance(TextStyles.class);
        this.overlordItems = injector.getInstance(OverlordItems.class);
    }

    @Override
    public List<List<EntryStack>> getInputEntries() {
        List<List<EntryStack>> entries = new ArrayList<>(essentialIngredients);
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
    public List<List<EntryStack>> getResultingEntries() {
        List<List<EntryStack>> entries = new ArrayList<>(essentialByproducts);
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
    public ResourceLocation getRecipeCategory() {
        return OverlordReiCategories.SKELETON_BUILDING_CATEGORY;
    }

    public EntryStack getSkeletonHead() {
        EntryStack entryStack = EntryStack.create(getSkullItemStack());
        entryStack.addSetting(EntryStack.Settings.TOOLTIP_APPEND_EXTRA, stack -> getSkullTooltip());
        return entryStack;
    }

    private ItemStack getSkullItemStack() {
        ItemStack skullStack;
        if (includeSkin) {
            if (includePlayerColor) {
                skullStack = new ItemStack(Items.PLAYER_HEAD);
            } else if (includeMuscle) {
                skullStack = new ItemStack(overlordItems.getFleshMuscleSkeletonSkull());
            } else {
                skullStack = new ItemStack(overlordItems.getFleshSkeletonSkull());
            }
        } else if (includeMuscle) {
            skullStack = new ItemStack(overlordItems.getMuscleSkeletonSkull());
        } else {
            skullStack = new ItemStack(Items.SKELETON_SKULL);
        }

        skullStack.setHoverName(new TranslatableComponent("entity.overlord.owned_skeleton"));

        return skullStack;
    }

    private List<Component> getSkullTooltip() {
        Component yes = new TranslatableComponent("gui.overlord.rei.tooltip.yes");
        Component no = new TranslatableComponent("gui.overlord.rei.tooltip.no");
        List<Component> components = new ArrayList<>(4);
        components.add(new TranslatableComponent("gui.overlord.rei.tooltip.health", this.totalMaxHealth).setStyle(textStyles.red()));
        components.add(new TranslatableComponent("gui.overlord.rei.tooltip.muscles", this.includeMuscle ? yes : no).setStyle(getStyleByBoolean(this.includeMuscle)));
        components.add(new TranslatableComponent("gui.overlord.rei.tooltip.skin", this.includeSkin ? yes : no).setStyle(getStyleByBoolean(this.includeSkin)));
        components.add(new TranslatableComponent("gui.overlord.rei.tooltip.player_appearance", this.includePlayerColor ? yes : no).setStyle(getStyleByBoolean(this.includePlayerColor)));

        return components;
    }

    private Style getStyleByBoolean(boolean isPresent) {
        return isPresent ? textStyles.green() : textStyles.redDark();
    }

    private Collection<List<EntryStack>> convertSkeletonIngredientsToEntryIngredients(Collection<SkeletonIngredient> ingredients) {
        Collection<List<EntryStack>> entryIngredients = new ArrayList<>(ingredients.size());

        for (SkeletonIngredient ingredient : ingredients) {
            if (ingredient instanceof ItemIngredient) {
                ItemIngredient itemIngredient = (ItemIngredient) ingredient;
                Item item = itemIngredient.getItem();
                int remainingCount = itemIngredient.getRequiredCount();
                while (remainingCount > 0) {
                    int stackCount = Math.min(remainingCount, item.getMaxStackSize());
                    entryIngredients.add(Collections.singletonList(EntryStack.create(new ItemStack(item, stackCount))));
                    remainingCount -= stackCount;
                }
            } else if (ingredient instanceof TagIngredient) {
                TagIngredient tagIngredient = (TagIngredient) ingredient;
                Ingredient recipeIngredient = Ingredient.of(tagIngredient.getTag());
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

    private static List<EntryStack> ingredientStacksWithCount(Ingredient ingredient, int count) {
        if (ingredient.isEmpty()) {
            return Collections.emptyList();
        }
        ItemStack[] matchingStacks = ingredient.getItems();
        if (matchingStacks.length == 0) {
            return Collections.emptyList();
        }
        if (matchingStacks.length == 1) {
            ItemStack matchingStack = matchingStacks[0];
            matchingStack.setCount(count);
            return Collections.singletonList(EntryStack.create(matchingStack));
        }
        List<EntryStack> result = new ArrayList<>(matchingStacks.length);
        for (ItemStack matchingStack : matchingStacks) {
            if (!matchingStack.isEmpty()) {
                matchingStack.setCount(count);
                result.add(EntryStack.create(matchingStack));
            }
        }
        return result;
    }

    private Collection<List<EntryStack>> convertStacksToEntryIngredients(Collection<ItemStack> itemStacks) {
        Collection<List<EntryStack>> entryIngredients = new HashSet<>(itemStacks.size());
        //TODO verify that these have been appropriately split into stacks by max stack size

        for (ItemStack stack : itemStacks) {
            entryIngredients.add(Collections.singletonList(EntryStack.create(stack)));
        }

        return entryIngredients;
    }
}
