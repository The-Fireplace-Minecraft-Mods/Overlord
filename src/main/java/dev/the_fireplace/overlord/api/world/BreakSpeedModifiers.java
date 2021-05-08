package dev.the_fireplace.overlord.api.world;

import dev.the_fireplace.overlord.impl.world.BreakSpeedModifiersImpl;
import net.minecraft.entity.LivingEntity;

public interface BreakSpeedModifiers {
    static BreakSpeedModifiers getInstance() {
        //noinspection deprecation
        return BreakSpeedModifiersImpl.INSTANCE;
    }
    float applyApplicable(LivingEntity entity, float baseBreakSpeed);
}
