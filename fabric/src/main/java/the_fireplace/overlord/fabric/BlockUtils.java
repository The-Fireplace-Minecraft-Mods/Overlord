package the_fireplace.overlord.fabric;

import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

public class BlockUtils {
    public static VoxelShape getVoxelShape(Direction direction, VoxelShape northShape, VoxelShape southShape, VoxelShape westShape, VoxelShape eastShape) {
        switch(direction) {
            case NORTH:
                return northShape;
            case SOUTH:
                return southShape;
            case WEST:
                return westShape;
            default:
                return eastShape;
        }
    }
}
