package dev.the_fireplace.overlord.entity.logic;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.domain.entity.logic.EntityAlliances;
import dev.the_fireplace.overlord.domain.entity.logic.EntityOwnership;
import net.minecraft.entity.Entity;

import javax.inject.Inject;
import java.util.Collection;
import java.util.UUID;

@Implementation
public class EntityAlliancesImpl implements EntityAlliances
{
    private final EntityOwnership entityOwnership;

    @Inject
    public EntityAlliancesImpl(EntityOwnership entityOwnership) {
        this.entityOwnership = entityOwnership;
    }

    @Override
    public boolean isAlliedTo(Entity entity, UUID player) {
        Collection<UUID> owners = entityOwnership.getOwnersOrSelf(entity);

        //TODO player alliance check once that system is in place
        return owners.contains(player);
    }

    @Override
    public boolean isAlliedTo(Entity entity, Entity otherEntity) {
        Collection<UUID> owners = entityOwnership.getOwnersOrSelf(entity);
        Collection<UUID> otherOwners = entityOwnership.getOwnersOrSelf(otherEntity);

        return owners.stream().anyMatch(otherOwners::contains);
    }
}
