package dev.the_fireplace.overlord.entrypoints;

import com.google.common.collect.Lists;
import dev.the_fireplace.annotateddi.api.DIContainer;
import dev.the_fireplace.overlord.compat.rei.OverlordReiCategories;
import dev.the_fireplace.overlord.compat.rei.SkeletonBuildingCategory;
import dev.the_fireplace.overlord.compat.rei.SkeletonBuildingDisplay;
import dev.the_fireplace.overlord.domain.entity.creation.SkeletonRecipeRegistry;
import dev.the_fireplace.overlord.entity.creation.SkeletonRecipe;
import dev.the_fireplace.overlord.item.OverlordItemTags;
import dev.the_fireplace.overlord.item.OverlordItems;
import me.shedaniel.rei.api.EntryRegistry;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.TagKey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Environment(EnvType.CLIENT)
public final class ReiClient implements REIPluginV0
{
    @Override
    public String getPluginProviderName() {
        return "Overlord";
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new SkeletonBuildingCategory());

        registry.addWorkstations(OverlordReiCategories.SKELETON_BUILDING_CATEGORY, entryStacksFromTag(OverlordItemTags.CASKETS).toArray(new EntryStack[0]));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        SkeletonRecipeRegistry skeletonRecipeRegistry = DIContainer.get().getInstance(SkeletonRecipeRegistry.class);
        for (SkeletonRecipe recipe : skeletonRecipeRegistry.getRecipes()) {
            registry.add(new SkeletonBuildingDisplay(recipe, false, false, false));
            if (recipe.hasMuscles()) {
                registry.add(new SkeletonBuildingDisplay(recipe, true, false, false));
            }
            if (recipe.hasSkin()) {
                registry.add(new SkeletonBuildingDisplay(recipe, false, true, false));
                if (recipe.hasMuscles()) {
                    registry.add(new SkeletonBuildingDisplay(recipe, true, true, false));
                }
                if (recipe.hasPlayerColor()) {
                    registry.add(new SkeletonBuildingDisplay(recipe, false, true, true));
                    if (recipe.hasMuscles()) {
                        registry.add(new SkeletonBuildingDisplay(recipe, true, true, true));
                    }
                }
            }
        }
    }

    @Override
    public void registerEntries(EntryRegistry registry) {
        //noinspection UnstableApiUsage
        registry.removeEntry(EntryStack.create(OverlordItems.SANS_MASK));
    }

    @Override
    public Identifier getPluginIdentifier() {
        return new Identifier(Overlord.MODID, Overlord.MODID);
    }

    private static List<EntryStack<ItemStack>> entryStacksFromTag(TagKey<Item> tagKey) {
        Ingredient ingredient = Ingredient.fromTag(tagKey);
        if (ingredient.isEmpty()) {
            return Collections.emptyList();
        }
        ItemStack[] matchingStacks = ingredient.getMatchingStacks();
        if (matchingStacks.length == 0) {
            return Collections.emptyList();
        }
        if (matchingStacks.length == 1) {
            ItemStack matchingStack = matchingStacks[0];
            return Lists.newArrayList(EntryStacks.of(matchingStack));
        }
        ArrayList<EntryStack<ItemStack>> result = new ArrayList<>(matchingStacks.length);
        for (ItemStack matchingStack : matchingStacks) {
            if (!matchingStack.isEmpty()) {
                result.add(EntryStacks.of(matchingStack));
            }
        }
        return result;
    }
}
