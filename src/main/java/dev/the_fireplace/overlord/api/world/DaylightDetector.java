package dev.the_fireplace.overlord.api.world;

import dev.the_fireplace.overlord.impl.world.DaylightDetectorImpl;
import net.minecraft.entity.Entity;

public interface DaylightDetector {
    static DaylightDetector getInstance() {
        //noinspection deprecation
        return DaylightDetectorImpl.INSTANCE;
    }

    boolean isInDaylight(Entity entity);
}
