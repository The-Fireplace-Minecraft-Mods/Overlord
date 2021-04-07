package dev.the_fireplace.overlord.api.world;

import net.minecraft.entity.Entity;

public interface MeleeAttackExecutor {
    boolean attack(Entity attacker, Entity target);
}
