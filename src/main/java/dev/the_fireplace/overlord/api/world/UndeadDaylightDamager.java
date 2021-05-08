package dev.the_fireplace.overlord.api.world;

import dev.the_fireplace.overlord.impl.world.DaylightDamager;
import net.minecraft.entity.LivingEntity;

public interface UndeadDaylightDamager {
    static UndeadDaylightDamager getInstance() {
        //noinspection deprecation
        return DaylightDamager.INSTANCE;
    }
    void applyDamage(LivingEntity entity);
}
