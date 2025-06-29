package dev.the_fireplace.overlord.entity.logic;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.domain.data.PlayerAlliances;
import dev.the_fireplace.overlord.domain.entity.logic.EntityAlliances;
import dev.the_fireplace.overlord.domain.entity.logic.EntityOwnership;
import net.minecraft.world.entity.Entity;

import javax.inject.Inject;
import java.util.Collection;
import java.util.UUID;

@Implementation
public class EntityAlliancesImpl implements EntityAlliances
{
    private final EntityOwnership entityOwnership;
    private final PlayerAlliances playerAlliances;

    @Inject
    public EntityAlliancesImpl(EntityOwnership entityOwnership, PlayerAlliances playerAlliances) {
        this.entityOwnership = entityOwnership;
        this.playerAlliances = playerAlliances;
    }

    @Override
    public boolean isAlliedTo(Entity entity, UUID player) {
        Collection<UUID> owners = entityOwnership.getOwnersOrSelf(entity);

        return owners.stream().anyMatch((entityOwner) -> entityOwner.equals(player)
            || playerAlliances.hasAllianceWith(player, entityOwner)
        );
    }

    @Override
    public boolean isAlliedTo(Entity entity, Entity otherEntity) {
        Collection<UUID> owners = entityOwnership.getOwnersOrSelf(entity);
        Collection<UUID> otherOwners = entityOwnership.getOwnersOrSelf(otherEntity);

        return otherOwners.stream().anyMatch((other) ->
            owners.stream().anyMatch((owner) -> owner.equals(other) || playerAlliances.hasAllianceWith(owner, other))
        );
    }
}
