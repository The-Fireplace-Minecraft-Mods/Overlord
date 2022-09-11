package dev.the_fireplace.overlord.domain.client;

import dev.the_fireplace.overlord.domain.entity.OrderableEntity;
import dev.the_fireplace.overlord.entity.ArmyEntity;

import javax.annotation.Nullable;

public interface ScreenOpener
{
    void openOrdersGUI(OrderableEntity entity);

    void openSquadSelectorGUI(@Nullable ArmyEntity entity);
}
