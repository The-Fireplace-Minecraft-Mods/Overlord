package dev.the_fireplace.overlord.util;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockUtils {
    public static VoxelShape getVoxelShape(Direction direction, VoxelShape northShape, VoxelShape southShape, VoxelShape westShape, VoxelShape eastShape) {
        switch(direction) {
            case NORTH:
                return northShape;
            case SOUTH:
                return southShape;
            case WEST:
                return westShape;
            case EAST:
            default:
                return eastShape;
        }
    }
}
