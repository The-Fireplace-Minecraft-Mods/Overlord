package dev.the_fireplace.overlord.entity.creation;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.domain.entity.creation.SkeletonRecipeRegistry;

import javax.inject.Singleton;
import java.util.Collection;
import java.util.HashSet;

@Implementation
@Singleton
public final class SkeletonRecipeRegistryImpl implements SkeletonRecipeRegistry
{
    private final Collection<SkeletonRecipe> skeletonRecipes = new HashSet<>();

    @Override
    public Collection<SkeletonRecipe> getRecipes() {
        return new HashSet<>(skeletonRecipes);
    }

    public void setSkeletonRecipes(Collection<SkeletonRecipe> skeletonRecipes) {
        this.skeletonRecipes.clear();
        this.skeletonRecipes.addAll(skeletonRecipes);
    }
}
