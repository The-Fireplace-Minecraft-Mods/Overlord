package dev.the_fireplace.overlord.domain.world;

import net.minecraft.entity.Entity;

public interface DaylightDetector {
    boolean isInDaylight(Entity entity);
}
