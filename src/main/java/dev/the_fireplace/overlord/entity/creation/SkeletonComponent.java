package dev.the_fireplace.overlord.entity.creation;

import dev.the_fireplace.overlord.domain.entity.creation.SkeletonIngredient;
import net.minecraft.item.ItemStack;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class SkeletonComponent
{
    protected Collection<SkeletonIngredient> ingredients = Collections.emptySet();
    protected Collection<ItemStack> byproducts = Collections.emptySet();

    protected int maxHealth = 0;

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

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public SkeletonComponent copy() {
        SkeletonComponent copy = new SkeletonComponent();
        copy.setIngredients(new HashSet<>(ingredients));
        copy.setByproducts(new HashSet<>(byproducts));
        copy.setMaxHealth(maxHealth);
        return copy;
    }
}
