package dev.the_fireplace.overlord.block.internal;

import dev.the_fireplace.overlord.blockentity.CasketBlockEntity;
import dev.the_fireplace.overlord.blockentity.OverlordBlockEntities;
import dev.the_fireplace.overlord.util.BlockUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.container.Container;
import net.minecraft.container.NameableContainerFactory;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import java.util.stream.Stream;

public class CasketBlock extends HorizontalFacingBlock implements BlockEntityProvider, Waterloggable
{
    public static final EnumProperty<BedPart> PART = Properties.BED_PART;
    private static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    private static final VoxelShape SOUTH_SHAPE = Stream.of(
        Block.createCuboidShape(0, 0, 0, 16, 1, 16),
        Block.createCuboidShape(0, 15, 0, 16, 16, 16),
        Block.createCuboidShape(1, 1, 0, 2, 15, 15),
        Block.createCuboidShape(2, 1, 14, 14, 15, 15),
        Block.createCuboidShape(14, 1, 0, 15, 15, 15)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

    private static final VoxelShape NORTH_SHAPE = Stream.of(
        Block.createCuboidShape(0, 0, 0, 16, 1, 16),
        Block.createCuboidShape(0, 15, 0, 16, 16, 16),
        Block.createCuboidShape(14, 1, 1, 15, 15, 16),
        Block.createCuboidShape(2, 1, 1, 14, 15, 2),
        Block.createCuboidShape(1, 1, 1, 2, 15, 16)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

    private static final VoxelShape EAST_SHAPE = Stream.of(
        Block.createCuboidShape(0, 0, 0, 16, 1, 16),
        Block.createCuboidShape(0, 15, 0, 16, 16, 16),
        Block.createCuboidShape(0, 1, 14, 15, 15, 15),
        Block.createCuboidShape(14, 1, 2, 15, 15, 14),
        Block.createCuboidShape(0, 1, 1, 15, 15, 2)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

    private static final VoxelShape WEST_SHAPE = Stream.of(
        Block.createCuboidShape(0, 0, 0, 16, 1, 16),
        Block.createCuboidShape(0, 15, 0, 16, 16, 16),
        Block.createCuboidShape(1, 1, 1, 16, 15, 2),
        Block.createCuboidShape(1, 1, 2, 2, 15, 14),
        Block.createCuboidShape(1, 1, 14, 16, 15, 15)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

    public CasketBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(PART, BedPart.FOOT).with(WATERLOGGED, false));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        if (facing == getDirectionTowardsOtherPart(state.get(PART), state.get(FACING))) {
            return neighborState.getBlock() == this && neighborState.get(PART) != state.get(PART) ? state : Blocks.AIR.getDefaultState();
        } else {
            return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
        }
    }

    public static Direction getDirectionTowardsOtherPart(BedPart part, Direction facing) {
        return part == BedPart.FOOT ? facing : facing.getOpposite();
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack stack) {
        super.afterBreak(world, player, pos, Blocks.AIR.getDefaultState(), blockEntity, stack);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BedPart bedPart = state.get(PART);
        BlockPos otherHalfPos = pos.offset(getDirectionTowardsOtherPart(bedPart, state.get(FACING)));
        BlockState otherHalfState = world.getBlockState(otherHalfPos);
        boolean isCompleteCasket = isCompleteCasket(pos, bedPart, otherHalfPos, otherHalfState);

        if (isCompleteCasket) {
            world.setBlockState(otherHalfPos, Blocks.AIR.getDefaultState(), 35);
            world.playLevelEvent(player, 2001, otherHalfPos, Block.getRawIdFromState(otherHalfState));
            if (!world.isClient && !player.isCreative() && bedPart == BedPart.FOOT) {
                ItemStack itemStack = player.getMainHandStack();
                dropStacks(state, world, pos, null, player, itemStack);
                dropStacks(otherHalfState, world, otherHalfPos, null, player, itemStack);
            }

            player.incrementStat(Stats.MINED.getOrCreateStat(this));
        }

        super.onBreak(world, pos, state, player);
    }

    private boolean isCompleteCasket(BlockPos pos, BedPart bedPart, BlockPos otherHalfPos, BlockState otherHalf) {
        boolean otherHalfExists = otherHalf.getBlock() == this;
        if (otherHalfExists) {
            BedPart otherHalfPart = otherHalf.get(PART);
            BlockPos otherHalfCounterpartPos = otherHalfPos.offset(getDirectionTowardsOtherPart(otherHalfPart, otherHalf.get(FACING)));
            return otherHalfPart != bedPart && otherHalfCounterpartPos.equals(pos);
        }

        return false;
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction = ctx.getPlayerFacing();
        BlockPos blockPos = ctx.getBlockPos();
        BlockPos blockPos2 = blockPos.offset(direction);
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        return ctx.getWorld().getBlockState(blockPos2).canReplace(ctx) ? this.getDefaultState().with(FACING, direction).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER) : null;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState state) {
        return PistonBehavior.BLOCK;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return new CasketBlockEntity();
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (world.isClient) {
            return;
        }
        BlockPos headPos = pos.offset(state.get(FACING));
        world.setBlockState(headPos, state.with(PART, BedPart.HEAD), 3);
        world.updateNeighbors(pos, Blocks.AIR);
        state.updateNeighborStates(world, pos, 3);
        if (itemStack.hasCustomName()) {
            BlockEntity blockEntity = world.getBlockEntity(headPos);
            if (blockEntity instanceof CasketBlockEntity) {
                ((CasketBlockEntity) blockEntity).setCustomName(itemStack.getName());
            }
        }
    }

    @Override
    public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() == newState.getBlock()) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof Inventory) {
            ItemScatterer.spawn(world, pos, (Inventory) blockEntity);
            world.updateHorizontalAdjacent(pos, this);
        }

        super.onBlockRemoved(state, world, pos, newState, moved);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof CasketBlockEntity) {
            ContainerProviderRegistry.INSTANCE.openContainer(OverlordBlockEntities.CASKET_BLOCK_ENTITY_ID, player, buf -> buf.writeBlockPos(pos));
        }
        return ActionResult.SUCCESS;
    }

    public static CasketBlockEntity getBlockEntity(BlockState state, IWorld world, BlockPos pos) {
        if (state.get(PART).equals(BedPart.FOOT)) {
            pos = pos.offset(state.get(FACING));
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof CasketBlockEntity) {
            return (CasketBlockEntity) blockEntity;
        }
        return null;
    }

    public static Inventory getInventory(BlockState blockState, World world, BlockPos blockPos) {
        return getBlockEntity(blockState, world, blockPos);
    }

    @Override
    public NameableContainerFactory createContainerFactory(BlockState state, World world, BlockPos pos) {
        return getBlockEntity(state, world, pos);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public long getRenderingSeed(BlockState state, BlockPos pos) {
        BlockPos blockPos = pos.offset(state.get(FACING), state.get(PART) == BedPart.HEAD ? 0 : 1);
        return MathHelper.hashCode(blockPos.getX(), pos.getY(), blockPos.getZ());
    }

    @Override
    public boolean canPlaceAtSide(BlockState world, BlockView view, BlockPos pos, BlockPlacementEnvironment env) {
        return false;
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return Container.calculateComparatorOutput(getInventory(state, world, pos));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext ePos) {
        Direction direction = state.get(FACING);
        Direction direction2 = state.get(PART) == BedPart.HEAD ? direction : direction.getOpposite();
        return BlockUtils.getVoxelShape(direction2, NORTH_SHAPE, SOUTH_SHAPE, WEST_SHAPE, EAST_SHAPE);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(PART);
        stateManager.add(WATERLOGGED);
        stateManager.add(FACING);
    }
}
