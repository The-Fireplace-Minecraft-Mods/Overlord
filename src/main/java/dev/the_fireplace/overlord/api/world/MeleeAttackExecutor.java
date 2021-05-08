package dev.the_fireplace.overlord.api.world;

import dev.the_fireplace.overlord.impl.world.MeleeAttackExecutorImpl;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public interface MeleeAttackExecutor {
    static MeleeAttackExecutor getInstance() {
        //noinspection deprecation
        return MeleeAttackExecutorImpl.INSTANCE;
    }

    void attack(LivingEntity attacker, Entity target);
    void attack(LivingEntity attacker, Entity target, float cooldownProgress);
}
