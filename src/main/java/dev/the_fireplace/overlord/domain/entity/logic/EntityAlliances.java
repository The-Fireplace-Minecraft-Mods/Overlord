package dev.the_fireplace.overlord.domain.entity.logic;

import net.minecraft.entity.LivingEntity;

import java.util.UUID;

public interface EntityAlliances
{
    boolean isAlliedTo(LivingEntity entity, UUID player);

    boolean isAlliedTo(LivingEntity entity, LivingEntity otherEntity);
}
