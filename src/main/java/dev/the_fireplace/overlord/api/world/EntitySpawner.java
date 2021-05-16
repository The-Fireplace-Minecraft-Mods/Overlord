package dev.the_fireplace.overlord.api.world;

import dev.the_fireplace.overlord.impl.world.EntitySpawnerImpl;
import net.minecraft.entity.Entity;
import net.minecraft.world.ModifiableWorld;

public interface EntitySpawner {
    static EntitySpawner getInstance() {
        //noinspection deprecation
        return EntitySpawnerImpl.INSTANCE;
    }
    void spawn(ModifiableWorld world, Entity entity);
}
