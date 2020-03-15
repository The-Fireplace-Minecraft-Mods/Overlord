package the_fireplace.overlord.fabric.particle;

import com.mojang.brigadier.StringReader;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

import java.util.Objects;

public class CustomParticleType extends ParticleType<CustomParticleType> implements ParticleEffect {
    private static final ParticleEffect.Factory<CustomParticleType> PARAMETER_FACTORY = new ParticleEffect.Factory<CustomParticleType>() {
        @Override
        public CustomParticleType read(ParticleType<CustomParticleType> particleType, StringReader stringReader) {
            return (CustomParticleType)particleType;
        }

        @Override
        public CustomParticleType read(ParticleType<CustomParticleType> particleType, PacketByteBuf packetByteBuf) {
            return (CustomParticleType)particleType;
        }
    };

    public CustomParticleType(boolean bl) {
        super(bl, PARAMETER_FACTORY);
    }

    @Override
    public ParticleType<CustomParticleType> getType() {
        return this;
    }

    @Override
    public void write(PacketByteBuf buf) {
    }

    @Override
    public String asString() {
        return Objects.requireNonNull(Registry.PARTICLE_TYPE.getId(this)).toString();
    }
}
