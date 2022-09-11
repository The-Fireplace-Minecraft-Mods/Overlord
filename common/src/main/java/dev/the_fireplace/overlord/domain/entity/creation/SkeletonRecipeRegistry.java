package dev.the_fireplace.overlord.domain.entity.creation;

import dev.the_fireplace.overlord.entity.creation.SkeletonRecipe;

import java.util.Collection;

public interface SkeletonRecipeRegistry
{
    Collection<SkeletonRecipe> getRecipes();
}
