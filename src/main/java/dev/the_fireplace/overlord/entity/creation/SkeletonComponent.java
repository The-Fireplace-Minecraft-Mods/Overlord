package dev.the_fireplace.overlord.entity.creation;

import dev.the_fireplace.overlord.domain.entity.creation.SkeletonIngredient;
import net.minecraft.item.ItemStack;

import java.util.Collection;
import java.util.Collections;

public class SkeletonComponent
{
    protected Collection<SkeletonIngredient> ingredients = Collections.emptySet();
    protected Collection<ItemStack> byproducts = Collections.emptySet();

    public Collection<SkeletonIngredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(Collection<SkeletonIngredient> ingredients) {
        this.ingredients = ingredients;
    }

    public Collection<ItemStack> getByproducts() {
        return byproducts;
    }

    public void setByproducts(Collection<ItemStack> byproducts) {
        this.byproducts = byproducts;
    }
}
