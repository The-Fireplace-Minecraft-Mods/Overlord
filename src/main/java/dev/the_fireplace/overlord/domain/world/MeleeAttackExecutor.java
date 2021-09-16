package dev.the_fireplace.overlord.domain.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public interface MeleeAttackExecutor {
    void attack(LivingEntity attacker, Entity target);
    void attack(LivingEntity attacker, Entity target, float cooldownProgress);
}
