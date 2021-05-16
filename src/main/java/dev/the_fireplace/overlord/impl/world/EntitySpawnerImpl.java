package dev.the_fireplace.overlord.impl.world;

import dev.the_fireplace.overlord.api.world.EntitySpawner;
import net.minecraft.entity.Entity;
import net.minecraft.world.ModifiableWorld;

public final class EntitySpawnerImpl implements EntitySpawner {
    @Deprecated
    public static final EntitySpawner INSTANCE = new EntitySpawnerImpl();

    private EntitySpawnerImpl() {}

    @Override
    public void spawn(ModifiableWorld world, Entity entity) {
        world.spawnEntity(entity);
    }
}
