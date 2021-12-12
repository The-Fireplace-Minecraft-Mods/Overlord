package dev.the_fireplace.overlord.block;

import dev.the_fireplace.overlord.block.internal.AbstractTombstoneBlock;
import dev.the_fireplace.overlord.blockentity.TombstoneBlockEntity;
import dev.the_fireplace.overlord.blockentity.internal.AbstractTombstoneBlockEntity;
import dev.the_fireplace.overlord.network.ServerToClientPacketIDs;
import dev.the_fireplace.overlord.network.server.builder.OpenTombstoneGUIBufferBuilder;
import dev.the_fireplace.overlord.util.BlockUtils;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class TombstoneBlock extends AbstractTombstoneBlock
{
    private static final VoxelShape NORTH_SOUTH_SHAPE = Stream.of(
        Block.createCuboidShape(4, 3, 6, 12, 14, 10),
        Block.createCuboidShape(2, 0, 4, 14, 3, 12)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

    private static final VoxelShape EAST_WEST_SHAPE = Stream.of(
        Block.createCuboidShape(6, 3, 4, 10, 14, 12),
        Block.createCuboidShape(4, 0, 2, 12, 3, 14)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

    public TombstoneBlock(Settings settings) {
        super(settings);
    }

    @Override
    public AbstractTombstoneBlockEntity createTombstone(BlockView view) {
        return new TombstoneBlockEntity();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext ePos) {
        Direction direction = state.get(FACING);
        return BlockUtils.getVoxelShape(direction, NORTH_SOUTH_SHAPE, NORTH_SOUTH_SHAPE, EAST_WEST_SHAPE, EAST_WEST_SHAPE);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (!world.isClient() && placer instanceof ServerPlayerEntity) {
            ServerPlayNetworkHandler networkHandler = ((ServerPlayerEntity) placer).networkHandler;
            networkHandler.sendPacket(ServerPlayNetworking.createS2CPacket(
                ServerToClientPacketIDs.OPEN_TOMBSTONE_GUI,
                OpenTombstoneGUIBufferBuilder.build(pos)
            ));
        }
    }
}
