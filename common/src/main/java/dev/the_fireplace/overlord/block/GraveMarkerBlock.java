package dev.the_fireplace.overlord.block;

import com.google.inject.Injector;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.block.internal.AbstractTombstoneBlock;
import dev.the_fireplace.overlord.blockentity.GraveMarkerBlockEntity;
import dev.the_fireplace.overlord.blockentity.OverlordBlockEntities;
import dev.the_fireplace.overlord.blockentity.internal.AbstractTombstoneBlockEntity;
import dev.the_fireplace.overlord.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.stream.Stream;

public class GraveMarkerBlock extends AbstractTombstoneBlock
{
    private static final VoxelShape SOUTH_SHAPE = Stream.of(
        Block.box(7, 0, 7, 9, 12, 9),
        Block.box(3, 7, 9, 13, 10, 10)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape NORTH_SHAPE = Stream.of(
        Block.box(7, 0, 7, 9, 12, 9),
        Block.box(3, 7, 6, 13, 10, 7)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape EAST_SHAPE = Stream.of(
        Block.box(7, 0, 7, 9, 12, 9),
        Block.box(9, 7, 3, 10, 10, 13)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape WEST_SHAPE = Stream.of(
        Block.box(7, 0, 7, 9, 12, 9),
        Block.box(6, 7, 3, 7, 10, 13)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private final Injector injector;

    public GraveMarkerBlock(Properties settings) {
        super(settings);
        this.injector = OverlordConstants.getInjector();
    }

    @Override
    public BlockEntityType<? extends AbstractTombstoneBlockEntity> getType() {
        //Lazy load this intentionally
        return injector.getInstance(OverlordBlockEntities.class).getGraveMarkerBlockEntityType();
    }

    @Override
    public AbstractTombstoneBlockEntity createTombstone(BlockPos pos, BlockState state) {
        return new GraveMarkerBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(FACING);
        return BlockUtils.getVoxelShape(direction, NORTH_SHAPE, SOUTH_SHAPE, WEST_SHAPE, EAST_SHAPE);
    }
}
