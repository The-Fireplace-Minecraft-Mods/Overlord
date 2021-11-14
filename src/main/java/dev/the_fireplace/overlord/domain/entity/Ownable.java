package dev.the_fireplace.overlord.domain.entity;

import net.minecraft.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.UUID;

public interface Ownable
{
    @Nullable
    UUID getOwnerUuid();

    void setOwnerUuid(@Nullable UUID uuid);

    boolean canAttackWithOwner(LivingEntity target, LivingEntity owner);
}
