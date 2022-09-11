package dev.the_fireplace.overlord.block.internal;

import dev.the_fireplace.overlord.blockentity.CasketBlockEntity;
import dev.the_fireplace.overlord.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.stream.Stream;

public class CasketBlock extends HorizontalDirectionalBlock implements EntityBlock, SimpleWaterloggedBlock
{
    public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;
    private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final VoxelShape SOUTH_SHAPE = Stream.of(
        Block.box(0, 0, 0, 16, 1, 16),
        Block.box(0, 15, 0, 16, 16, 16),
        Block.box(1, 1, 0, 2, 15, 15),
        Block.box(2, 1, 14, 14, 15, 15),
        Block.box(14, 1, 0, 15, 15, 15)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape NORTH_SHAPE = Stream.of(
        Block.box(0, 0, 0, 16, 1, 16),
        Block.box(0, 15, 0, 16, 16, 16),
        Block.box(14, 1, 1, 15, 15, 16),
        Block.box(2, 1, 1, 14, 15, 2),
        Block.box(1, 1, 1, 2, 15, 16)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape EAST_SHAPE = Stream.of(
        Block.box(0, 0, 0, 16, 1, 16),
        Block.box(0, 15, 0, 16, 16, 16),
        Block.box(0, 1, 14, 15, 15, 15),
        Block.box(14, 1, 2, 15, 15, 14),
        Block.box(0, 1, 1, 15, 15, 2)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape WEST_SHAPE = Stream.of(
        Block.box(0, 0, 0, 16, 1, 16),
        Block.box(0, 15, 0, 16, 16, 16),
        Block.box(1, 1, 1, 16, 15, 2),
        Block.box(1, 1, 2, 2, 15, 14),
        Block.box(1, 1, 14, 16, 15, 15)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public CasketBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.getStateDefinition().any().setValue(PART, BedPart.FOOT).setValue(WATERLOGGED, false));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        if (facing == getDirectionTowardsOtherPart(state.getValue(PART), state.getValue(FACING))) {
            return neighborState.getBlock() == this && neighborState.getValue(PART) != state.getValue(PART) ? state : Blocks.AIR.defaultBlockState();
        } else {
            return super.updateShape(state, facing, neighborState, world, pos, neighborPos);
        }
    }

    public static Direction getDirectionTowardsOtherPart(BedPart part, Direction facing) {
        return part == BedPart.FOOT ? facing : facing.getOpposite();
    }

    @Override
    public void playerDestroy(Level world, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack stack) {
        super.playerDestroy(world, player, pos, Blocks.AIR.defaultBlockState(), blockEntity, stack);
    }

    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        BedPart bedPart = state.getValue(PART);
        BlockPos otherHalfPos = pos.relative(getDirectionTowardsOtherPart(bedPart, state.getValue(FACING)));
        BlockState otherHalfState = world.getBlockState(otherHalfPos);
        boolean isCompleteCasket = isCompleteCasket(pos, bedPart, otherHalfPos, otherHalfState);

        if (isCompleteCasket) {
            world.setBlock(otherHalfPos, Blocks.AIR.defaultBlockState(), 35);
            world.levelEvent(player, 2001, otherHalfPos, Block.getId(otherHalfState));
            if (!world.isClientSide && !player.isCreative() && bedPart == BedPart.FOOT) {
                ItemStack itemStack = player.getMainHandItem();
                dropResources(state, world, pos, null, player, itemStack);
                dropResources(otherHalfState, world, otherHalfPos, null, player, itemStack);
            }

            player.awardStat(Stats.BLOCK_MINED.get(this));
        }

        super.playerWillDestroy(world, pos, state, player);
    }

    private boolean isCompleteCasket(BlockPos pos, BedPart bedPart, BlockPos otherHalfPos, BlockState otherHalf) {
        boolean otherHalfExists = otherHalf.is(this);
        if (otherHalfExists) {
            BedPart otherHalfPart = otherHalf.getValue(PART);
            BlockPos otherHalfCounterpartPos = otherHalfPos.relative(getDirectionTowardsOtherPart(otherHalfPart, otherHalf.getValue(FACING)));
            return otherHalfPart != bedPart && otherHalfCounterpartPos.equals(pos);
        }

        return false;
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction direction = ctx.getHorizontalDirection();
        BlockPos blockPos = ctx.getClickedPos();
        BlockPos blockPos2 = blockPos.relative(direction);
        FluidState fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());
        return ctx.getLevel().getBlockState(blockPos2).canBeReplaced(ctx) ? this.defaultBlockState().setValue(FACING, direction).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER) : null;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CasketBlockEntity(pos, state, state.getValue(PART).equals(BedPart.FOOT));
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(world, pos, state, placer, itemStack);
        if (world.isClientSide) {
            return;
        }
        BlockPos headPos = pos.relative(state.getValue(FACING));
        world.setBlock(headPos, state.setValue(PART, BedPart.HEAD), 3);
        world.blockUpdated(pos, Blocks.AIR);
        state.updateNeighbourShapes(world, pos, 3);
        if (itemStack.hasCustomHoverName()) {
            BlockEntity blockEntity = world.getBlockEntity(headPos);
            if (blockEntity instanceof CasketBlockEntity) {
                ((CasketBlockEntity) blockEntity).setCustomName(itemStack.getHoverName());
            }
        }
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() == newState.getBlock()) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof Container) {
            Containers.dropContents(world, pos, (Container) blockEntity);
            world.updateNeighbourForOutputSignal(pos, this);
        }

        super.onRemove(state, world, pos, newState, moved);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof CasketBlockEntity) {
            player.openMenu((CasketBlockEntity) blockEntity);
        }
        return InteractionResult.SUCCESS;
    }

    public static CasketBlockEntity getBlockEntity(BlockState state, LevelAccessor world, BlockPos pos) {
        if (state.getValue(PART).equals(BedPart.FOOT)) {
            pos = pos.relative(state.getValue(FACING));
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof CasketBlockEntity) {
            return (CasketBlockEntity) blockEntity;
        }
        return null;
    }

    public static Container getInventory(BlockState blockState, Level world, BlockPos blockPos) {
        return getBlockEntity(blockState, world, blockPos);
    }

    @Override
    public MenuProvider getMenuProvider(BlockState state, Level world, BlockPos pos) {
        return getBlockEntity(state, world, pos);
    }

    @Override
    public long getSeed(BlockState state, BlockPos pos) {
        BlockPos blockPos = pos.relative(state.getValue(FACING), state.getValue(PART) == BedPart.HEAD ? 0 : 1);
        return Mth.getSeed(blockPos.getX(), pos.getY(), blockPos.getZ());
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromContainer(getInventory(state, world, pos));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        Direction direction = state.getValue(FACING);
        Direction direction2 = state.getValue(PART) == BedPart.HEAD ? direction : direction.getOpposite();
        return BlockUtils.getVoxelShape(direction2, NORTH_SHAPE, SOUTH_SHAPE, WEST_SHAPE, EAST_SHAPE);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(PART);
        stateManager.add(WATERLOGGED);
        stateManager.add(FACING);
    }
}
