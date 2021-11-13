package dev.the_fireplace.overlord.domain.entity.logic;

import net.minecraft.entity.LivingEntity;

import java.util.Collection;
import java.util.UUID;

public interface EntityOwnership
{
    Collection<UUID> getOwners(LivingEntity entity);

    boolean isOwnedBy(LivingEntity entity, UUID ownerId);

    Collection<UUID> getOwnersOrSelf(LivingEntity entity);

    boolean isOwnedByOrSelf(LivingEntity entity, UUID ownerId);
}
