package dev.the_fireplace.overlord.domain.world;

import net.minecraft.entity.Entity;
import net.minecraft.world.ModifiableWorld;

public interface EntitySpawner {
    void spawn(ModifiableWorld world, Entity entity);
}
