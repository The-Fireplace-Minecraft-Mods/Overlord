package dev.the_fireplace.overlord.impl.world;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.domain.world.EntitySpawner;
import net.minecraft.entity.Entity;
import net.minecraft.world.ModifiableWorld;

@Implementation
public final class EntitySpawnerImpl implements EntitySpawner {
    @Override
    public void spawn(ModifiableWorld world, Entity entity) {
        world.spawnEntity(entity);
    }
}
