package dev.the_fireplace.overlord.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

public class WallArmySkullBlock extends AbstractArmySkullBlock
{
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final Map<Direction, VoxelShape> FACING_TO_SHAPE = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Block.box(4.0, 4.0, 8.0, 12.0, 12.0, 16.0), Direction.SOUTH, Block.box(4.0, 4.0, 0.0, 12.0, 12.0, 8.0), Direction.EAST, Block.box(0.0, 4.0, 4.0, 8.0, 12.0, 12.0), Direction.WEST, Block.box(8.0, 4.0, 4.0, 16.0, 12.0, 12.0)));

    public WallArmySkullBlock(SkullType skullType, Block.Properties settings) {
        super(skullType, settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public String getDescriptionId() {
        return this.asItem().getDescriptionId();
    }

    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return (VoxelShape) FACING_TO_SHAPE.get(state.getValue(FACING));
    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState blockState = this.defaultBlockState();
        BlockGetter blockView = ctx.getLevel();
        BlockPos blockPos = ctx.getClickedPos();
        Direction[] directions = ctx.getNearestLookingDirections();

        for (Direction direction : directions) {
            if (direction.getAxis().isHorizontal()) {
                Direction direction2 = direction.getOpposite();
                blockState = blockState.setValue(FACING, direction2);
                if (!blockView.getBlockState(blockPos.relative(direction)).canBeReplaced(ctx)) {
                    return blockState;
                }
            }
        }

        return null;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
