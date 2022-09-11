package dev.the_fireplace.overlord.block;

import com.google.inject.Injector;
import dev.the_fireplace.lib.api.network.injectables.PacketSender;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.block.internal.AbstractTombstoneBlock;
import dev.the_fireplace.overlord.blockentity.OverlordBlockEntities;
import dev.the_fireplace.overlord.blockentity.TombstoneBlockEntity;
import dev.the_fireplace.overlord.blockentity.internal.AbstractTombstoneBlockEntity;
import dev.the_fireplace.overlord.network.ClientboundPackets;
import dev.the_fireplace.overlord.network.server.builder.OpenTombstoneGUIBufferBuilder;
import dev.the_fireplace.overlord.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.stream.Stream;

public class TombstoneBlock extends AbstractTombstoneBlock
{
    private static final VoxelShape NORTH_SOUTH_SHAPE = Stream.of(
        Block.box(4, 3, 6, 12, 14, 10),
        Block.box(2, 0, 4, 14, 3, 12)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape EAST_WEST_SHAPE = Stream.of(
        Block.box(6, 3, 4, 10, 14, 12),
        Block.box(4, 0, 2, 12, 3, 14)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private final Injector injector;
    private final PacketSender packetSender;
    private final ClientboundPackets clientboundPackets;
    private final OpenTombstoneGUIBufferBuilder openTombstoneGUIBufferBuilder;

    public TombstoneBlock(Properties settings) {
        super(settings);
        this.injector = OverlordConstants.getInjector();
        this.packetSender = injector.getInstance(PacketSender.class);
        this.clientboundPackets = injector.getInstance(ClientboundPackets.class);
        this.openTombstoneGUIBufferBuilder = injector.getInstance(OpenTombstoneGUIBufferBuilder.class);
    }

    @Override
    public BlockEntityType<? extends AbstractTombstoneBlockEntity> getType() {
        //Lazy load this intentionally, not during constructor.
        return injector.getInstance(OverlordBlockEntities.class).getTombstoneBlockEntityType();
    }

    @Override
    public AbstractTombstoneBlockEntity createTombstone(BlockGetter blockGetter) {
        return new TombstoneBlockEntity();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        Direction direction = state.getValue(FACING);
        return BlockUtils.getVoxelShape(direction, NORTH_SOUTH_SHAPE, NORTH_SOUTH_SHAPE, EAST_WEST_SHAPE, EAST_WEST_SHAPE);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(world, pos, state, placer, itemStack);
        if (!world.isClientSide() && placer instanceof ServerPlayer) {
            packetSender.sendToClient(
                ((ServerPlayer) placer).connection,
                clientboundPackets.openTombstoneScreen(),
                openTombstoneGUIBufferBuilder.build(pos)
            );
        }
    }
}
