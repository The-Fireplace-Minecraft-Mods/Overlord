package dev.the_fireplace.overlord.domain.registry;

import net.minecraft.resources.ResourceLocation;

import java.util.Collection;

public interface EntityRegistry
{
    Collection<ResourceLocation> getMonsterIds();

    Collection<ResourceLocation> getAnimalIds();
}
