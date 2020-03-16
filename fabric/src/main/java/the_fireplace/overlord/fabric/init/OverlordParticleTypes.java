package the_fireplace.overlord.fabric.init;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import the_fireplace.overlord.OverlordHelper;
import the_fireplace.overlord.fabric.particle.CustomParticleType;

public class OverlordParticleTypes {
    public static CustomParticleType SCORCHED_FLAME;
    public static CustomParticleType DEAD_FLAME;
    public static void register() {
        SCORCHED_FLAME = register("scorched_flame", false);
        DEAD_FLAME = register("dead_flame", false);
    }

    private static CustomParticleType register(String name, boolean alwaysShow) {
        return Registry.register(Registry.PARTICLE_TYPE, new Identifier(OverlordHelper.MODID, name), new CustomParticleType(alwaysShow));
    }
}
