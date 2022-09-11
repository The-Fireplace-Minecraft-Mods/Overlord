package dev.the_fireplace.overlord.loader;

import net.minecraft.resources.ResourceLocation;

public interface RegistryHelper<V>
{
    void register(ResourceLocation id, V value);
}
