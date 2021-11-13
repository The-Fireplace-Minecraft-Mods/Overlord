package dev.the_fireplace.overlord.entity.logic;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.domain.entity.Ownable;
import dev.the_fireplace.overlord.domain.entity.logic.EntityOwnership;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

@Implementation
public class EntityOwnershipImpl implements EntityOwnership
{
    @Override
    public Collection<UUID> getOwners(LivingEntity entity) {
        Collection<UUID> owners = new HashSet<>();

        if (entity instanceof Ownable) {
            owners.add(((Ownable) entity).getOwnerId());
        }
        if (entity instanceof Tameable) {
            owners.add(((Tameable) entity).getOwnerUuid());
        }
        if (entity.hasPassengers()) {
            for (Entity passenger : entity.getPassengerList()) {
                if (passenger instanceof LivingEntity) {
                    owners.addAll(getOwnersOrSelf((LivingEntity) passenger));
                }
            }
        }
        // Safety check in case some other mod does anything strange
        owners.remove(entity.getUuid());

        return owners;
    }

    @Override
    public boolean isOwnedBy(LivingEntity entity, UUID ownerId) {
        return getOwners(entity).contains(ownerId);
    }

    @Override
    public Collection<UUID> getOwnersOrSelf(LivingEntity entity) {
        Collection<UUID> owners = getOwners(entity);
        if (owners.isEmpty()) {
            owners.add(entity.getUuid());
        }

        return owners;
    }

    @Override
    public boolean isOwnedByOrSelf(LivingEntity entity, UUID ownerId) {
        return getOwnersOrSelf(entity).contains(ownerId);
    }
}
