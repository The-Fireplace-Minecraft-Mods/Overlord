package dev.the_fireplace.overlord.domain.entity.logic;

import net.minecraft.entity.Entity;

import java.util.Collection;
import java.util.UUID;

public interface EntityOwnership
{
    Collection<UUID> getOwners(Entity entity);

    boolean isOwnedBy(Entity entity, UUID ownerId);

    Collection<UUID> getOwnersOrSelf(Entity entity);

    boolean isOwnedByOrSelf(Entity entity, UUID ownerId);
}
