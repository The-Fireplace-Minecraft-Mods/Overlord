package the_fireplace.overlord.fabric.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityContext;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import the_fireplace.overlord.fabric.block.internal.AbstractTombstoneBlock;
import the_fireplace.overlord.fabric.blockentity.GraveMarkerBlockEntity;
import the_fireplace.overlord.fabric.blockentity.internal.TombstoneBlockEntity;
import the_fireplace.overlord.fabric.util.BlockUtils;

import java.util.stream.Stream;

public class GraveMarkerBlock extends AbstractTombstoneBlock {
    private static final VoxelShape SOUTH_SHAPE = Stream.of(
            Block.createCuboidShape(7, 0, 7, 9, 12, 9),
            Block.createCuboidShape(3, 7, 9, 13, 10, 10)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

    private static final VoxelShape NORTH_SHAPE = Stream.of(
            Block.createCuboidShape(7, 0, 7, 9, 12, 9),
            Block.createCuboidShape(3, 7, 6, 13, 10, 7)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

    private static final VoxelShape EAST_SHAPE = Stream.of(
            Block.createCuboidShape(7, 0, 7, 9, 12, 9),
            Block.createCuboidShape(9, 7, 3, 10, 10, 13)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

    private static final VoxelShape WEST_SHAPE = Stream.of(
            Block.createCuboidShape(7, 0, 7, 9, 12, 9),
            Block.createCuboidShape(6, 7, 3, 7, 10, 13)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

    public GraveMarkerBlock(Settings settings) {
        super(settings);
    }

    @Override
    public TombstoneBlockEntity createTombstone(BlockView view) {
        return new GraveMarkerBlockEntity();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext ePos) {
        Direction direction = state.get(FACING);
        return BlockUtils.getVoxelShape(direction, NORTH_SHAPE, SOUTH_SHAPE, WEST_SHAPE, EAST_SHAPE);
    }
}
