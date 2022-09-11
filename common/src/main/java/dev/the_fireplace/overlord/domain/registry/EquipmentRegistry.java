package dev.the_fireplace.overlord.domain.registry;

import net.minecraft.resources.ResourceLocation;

import java.util.Collection;

public interface EquipmentRegistry
{
    boolean isEquipment(ResourceLocation identifier);

    Collection<ResourceLocation> getEquipmentIds();
}
