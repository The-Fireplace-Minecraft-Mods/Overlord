package dev.the_fireplace.overlord.entrypoints;

import com.google.common.collect.Lists;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.compat.rei.OverlordReiCategories;
import dev.the_fireplace.overlord.compat.rei.SkeletonBuildingCategory;
import dev.the_fireplace.overlord.compat.rei.SkeletonBuildingDisplay;
import dev.the_fireplace.overlord.domain.entity.creation.SkeletonRecipeRegistry;
import dev.the_fireplace.overlord.entity.creation.SkeletonRecipe;
import dev.the_fireplace.overlord.item.OverlordItemTags;
import dev.the_fireplace.overlord.item.OverlordItems;
import me.shedaniel.rei.api.EntryRegistry;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Environment(EnvType.CLIENT)
public final class ReiClient implements REIPluginV0
{
    @Override
    public void registerPluginCategories(RecipeHelper recipeHelper) {
        recipeHelper.registerCategory(new SkeletonBuildingCategory());

        recipeHelper.registerWorkingStations(OverlordReiCategories.SKELETON_BUILDING_CATEGORY, entryStacksFromTag(OverlordItemTags.CASKETS).toArray(new EntryStack[0]));
    }

    @Override
    public void registerRecipeDisplays(RecipeHelper registry) {
        SkeletonRecipeRegistry skeletonRecipeRegistry = OverlordConstants.getInjector().getInstance(SkeletonRecipeRegistry.class);
        for (SkeletonRecipe recipe : skeletonRecipeRegistry.getRecipes()) {
            registry.registerDisplay(new SkeletonBuildingDisplay(recipe, false, false, false));
            if (recipe.hasMuscles()) {
                registry.registerDisplay(new SkeletonBuildingDisplay(recipe, true, false, false));
            }
            if (recipe.hasSkin()) {
                registry.registerDisplay(new SkeletonBuildingDisplay(recipe, false, true, false));
                if (recipe.hasMuscles()) {
                    registry.registerDisplay(new SkeletonBuildingDisplay(recipe, true, true, false));
                }
                if (recipe.hasPlayerColor()) {
                    registry.registerDisplay(new SkeletonBuildingDisplay(recipe, false, true, true));
                    if (recipe.hasMuscles()) {
                        registry.registerDisplay(new SkeletonBuildingDisplay(recipe, true, true, true));
                    }
                }
            }
        }
    }

    @Override
    public void registerEntries(EntryRegistry registry) {
        //noinspection UnstableApiUsage
        registry.removeEntry(EntryStack.create(OverlordConstants.getInjector().getInstance(OverlordItems.class).getSansMask()));
    }

    @Override
    public ResourceLocation getPluginIdentifier() {
        return new ResourceLocation(OverlordConstants.MODID, OverlordConstants.MODID);
    }

    private static List<EntryStack> entryStacksFromTag(Tag.Named<Item> tagKey) {
        Ingredient ingredient = Ingredient.of(tagKey);
        if (ingredient.isEmpty()) {
            return Collections.emptyList();
        }
        ItemStack[] matchingStacks = ingredient.getItems();
        if (matchingStacks.length == 0) {
            return Collections.emptyList();
        }
        if (matchingStacks.length == 1) {
            ItemStack matchingStack = matchingStacks[0];
            return Lists.newArrayList(EntryStack.create(matchingStack));
        }
        ArrayList<EntryStack> result = new ArrayList<>(matchingStacks.length);
        for (ItemStack matchingStack : matchingStacks) {
            if (!matchingStack.isEmpty()) {
                result.add(EntryStack.create(matchingStack));
            }
        }
        return result;
    }
}
