package dev.the_fireplace.overlord.impl.world;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.domain.world.DaylightDetector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

@Implementation
public final class DaylightDetectorImpl implements DaylightDetector {
    private final Random random = new Random();
    
    @Override
    public boolean isInDaylight(Entity entity) {
        World world = entity.getEntityWorld();
        if (world.isDay() && !world.isClient()) {
            float brightnessAtEyes = entity.getBrightnessAtEyes();
            BlockPos entityPosition = new BlockPos(entity.getX(), (double)Math.round(entity.getY()), entity.getZ());
            if (entity.getVehicle() instanceof BoatEntity) {
                entityPosition = entityPosition.up();
            }
            return brightnessAtEyes > 0.5F
                && random.nextFloat() * 30.0F < (brightnessAtEyes - 0.4F) * 2.0F
                && world.isSkyVisible(entityPosition);
        }
        
        return false;
    }
}
