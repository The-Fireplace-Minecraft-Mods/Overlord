package dev.the_fireplace.overlord.domain.world;

import net.minecraft.entity.LivingEntity;

public interface BreakSpeedModifiers {
    float applyApplicable(LivingEntity entity, float baseBreakSpeed);
}
