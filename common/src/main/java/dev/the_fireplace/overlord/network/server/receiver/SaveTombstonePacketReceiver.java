package dev.the_fireplace.overlord.network.server.receiver;

import dev.the_fireplace.lib.api.network.interfaces.ServerboundPacketReceiver;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.blockentity.TombstoneBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.inject.Singleton;

@Singleton
public final class SaveTombstonePacketReceiver implements ServerboundPacketReceiver
{
    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf) {
        BlockPos tombstonePosition = buf.readBlockPos();
        String tombstoneName = buf.readUtf();
        server.execute(() -> {
            BlockEntity blockEntity = player.level.getBlockEntity(tombstonePosition);
            if (blockEntity instanceof TombstoneBlockEntity) {
                if (player.getUUID().equals(((TombstoneBlockEntity) blockEntity).getOwner())) {
                    ((TombstoneBlockEntity) blockEntity).setNameText(tombstoneName);
                } else {
                    OverlordConstants.getLogger().warn("Update Tombstone packet received with wrong player ID, expected {} and got {}.", ((TombstoneBlockEntity) blockEntity).getOwner(), player.getUUID());
                }
            } else {
                OverlordConstants.getLogger().warn("Received save tombstone packet with missing tombstone block entity!");
            }
        });
    }
}
