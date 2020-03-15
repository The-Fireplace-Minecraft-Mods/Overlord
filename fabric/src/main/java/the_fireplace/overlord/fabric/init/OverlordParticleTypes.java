package the_fireplace.overlord.fabric.init;

import net.minecraft.util.registry.Registry;
import the_fireplace.overlord.fabric.particle.CustomParticleType;

public class OverlordParticleTypes {
    public static final CustomParticleType SCORCHED_FLAME = register("scorched_flame", false);
    public static final CustomParticleType DEAD_FLAME = register("dead_flame", false);

    private static CustomParticleType register(String name, boolean alwaysShow) {
        return Registry.register(Registry.PARTICLE_TYPE, name, new CustomParticleType(alwaysShow));
    }
}
