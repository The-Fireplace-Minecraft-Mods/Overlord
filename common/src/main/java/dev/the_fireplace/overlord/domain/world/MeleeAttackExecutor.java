package dev.the_fireplace.overlord.domain.world;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public interface MeleeAttackExecutor {
    void attack(LivingEntity attacker, Entity target);
    void attack(LivingEntity attacker, Entity target, float cooldownProgress);
}
