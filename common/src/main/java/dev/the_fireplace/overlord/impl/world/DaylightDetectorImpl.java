package dev.the_fireplace.overlord.impl.world;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.domain.world.DaylightDetector;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;

import java.util.Random;

@Implementation
public final class DaylightDetectorImpl implements DaylightDetector {
    private final Random random = new Random();
    
    @Override
    public boolean isInDaylight(Entity entity) {
        Level world = entity.getCommandSenderWorld();
        if (world.isDay() && !world.isClientSide()) {
            float brightnessAtEyes = entity.getBrightness();
            BlockPos entityPosition = new BlockPos(entity.getX(), (double) Math.round(entity.getY()), entity.getZ());
            if (entity.getVehicle() instanceof Boat) {
                entityPosition = entityPosition.above();
            }
            return brightnessAtEyes > 0.5F
                && random.nextFloat() * 30.0F < (brightnessAtEyes - 0.4F) * 2.0F
                && world.canSeeSky(entityPosition);
        }

        return false;
    }
}
