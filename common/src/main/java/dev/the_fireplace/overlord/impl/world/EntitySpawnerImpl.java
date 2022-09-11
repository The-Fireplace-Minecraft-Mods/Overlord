package dev.the_fireplace.overlord.impl.world;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.domain.world.EntitySpawner;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelWriter;

@Implementation
public final class EntitySpawnerImpl implements EntitySpawner {
    @Override
    public void spawn(LevelWriter world, Entity entity) {
        world.addFreshEntity(entity);
    }
}
