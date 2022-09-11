package dev.the_fireplace.overlord.entity.logic;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.domain.entity.logic.EntityOwnership;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

@Implementation
public class EntityOwnershipImpl implements EntityOwnership
{
    @Override
    public Collection<UUID> getOwners(Entity entity) {
        Collection<UUID> owners = new HashSet<>();

        if (entity instanceof OwnableEntity) {
            UUID ownerUuid = ((OwnableEntity) entity).getOwnerUUID();
            if (ownerUuid != null) {
                owners.add(ownerUuid);
            }
        }
        if (entity.isVehicle()) {
            for (Entity passenger : entity.getPassengers()) {
                owners.addAll(getOwnersOrSelf(passenger));
            }
        }
        // Safety check in case some other mod does anything strange
        owners.remove(entity.getUUID());

        return owners;
    }

    @Override
    public boolean isOwnedBy(Entity entity, UUID ownerId) {
        return getOwners(entity).contains(ownerId);
    }

    @Override
    public Collection<UUID> getOwnersOrSelf(Entity entity) {
        Collection<UUID> owners = getOwners(entity);
        if (owners.isEmpty()) {
            owners.add(entity.getUUID());
        }

        return owners;
    }

    @Override
    public boolean isOwnedByOrSelf(Entity entity, UUID ownerId) {
        return getOwnersOrSelf(entity).contains(ownerId);
    }
}
