package dev.the_fireplace.overlord.entity.creation.ingredient;

import dev.the_fireplace.overlord.domain.entity.creation.SkeletonIngredient;

public abstract class AbstractIngredient implements SkeletonIngredient
{
    protected int requiredCount = 1;

    @Override
    public int getRequiredCount() {
        return requiredCount;
    }

    public void setRequiredCount(int requiredCount) {
        this.requiredCount = requiredCount;
    }
}
