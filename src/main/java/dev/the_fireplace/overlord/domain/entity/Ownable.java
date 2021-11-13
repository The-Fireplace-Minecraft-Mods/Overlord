package dev.the_fireplace.overlord.domain.entity;

import net.minecraft.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.UUID;

public interface Ownable
{
    UUID getOwnerId();

    @Nullable
    LivingEntity getOwner();

    boolean canAttackWithOwner(LivingEntity target, LivingEntity owner);
}
