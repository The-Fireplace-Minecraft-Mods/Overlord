package dev.the_fireplace.overlord.domain.client;

import dev.the_fireplace.overlord.domain.entity.OrderableEntity;
import dev.the_fireplace.overlord.entity.ArmyEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import javax.annotation.Nullable;

@Environment(EnvType.CLIENT)
public interface ScreenOpener
{
    void openOrdersGUI(OrderableEntity entity);

    void openSquadSelectorGUI(@Nullable ArmyEntity entity);
}
