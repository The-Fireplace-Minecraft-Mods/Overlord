package dev.the_fireplace.overlord.domain.entity.logic;

import net.minecraft.entity.Entity;

import java.util.UUID;

public interface EntityAlliances
{
    boolean isAlliedTo(Entity entity, UUID player);

    boolean isAlliedTo(Entity entity, Entity otherEntity);
}
