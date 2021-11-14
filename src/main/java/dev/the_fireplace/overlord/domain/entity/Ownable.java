package dev.the_fireplace.overlord.domain.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;

public interface Ownable extends Tameable
{
    boolean canAttackWithOwner(LivingEntity target, LivingEntity owner);
}
