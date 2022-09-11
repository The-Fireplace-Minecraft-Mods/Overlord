package dev.the_fireplace.overlord.domain.world;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelWriter;

public interface EntitySpawner
{
    void spawn(LevelWriter world, Entity entity);
}
