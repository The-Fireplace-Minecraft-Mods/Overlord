package dev.the_fireplace.overlord.domain.entity;

public interface AnimatedMilkDrinker
{
    void startDrinkingMilkAnimation();

    void completeDrinkingMilk();

    boolean isDrinkingMilk();

    boolean canDrinkMilk();
}
