package dev.the_fireplace.overlord.init;

import dev.the_fireplace.overlord.Overlord;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class OverlordParticleTypes {
    public static DefaultParticleType SCORCHED_FLAME;
    public static DefaultParticleType DEAD_FLAME;
    public static void register() {
        SCORCHED_FLAME = register("scorched_flame");
        DEAD_FLAME = register("dead_flame");
    }

    private static DefaultParticleType register(String name) {
        return Registry.register(Registry.PARTICLE_TYPE, new Identifier(Overlord.MODID, name), FabricParticleTypes.simple());
    }
}
