package dev.the_fireplace.overlord.network.server.receiver;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.blockentity.TombstoneBlockEntity;
import dev.the_fireplace.overlord.domain.network.ClientToServerPacketIDs;
import dev.the_fireplace.overlord.domain.network.server.SaveTombstonePacketReceiver;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

import javax.inject.Inject;

@Implementation
public final class SaveTombstonePacketReceiverImpl implements SaveTombstonePacketReceiver
{
    private final ClientToServerPacketIDs clientToServerPacketIDs;

    @Inject
    public SaveTombstonePacketReceiverImpl(ClientToServerPacketIDs clientToServerPacketIDs) {
        this.clientToServerPacketIDs = clientToServerPacketIDs;
    }

    @Override
    public Identifier getId() {
        return clientToServerPacketIDs.saveTombstonePacketID();
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        BlockPos tombstonePosition = buf.readBlockPos();
        String tombstoneName = buf.readString();
        server.execute(() -> {
            BlockEntity blockEntity = player.world.getBlockEntity(tombstonePosition);
            if (blockEntity instanceof TombstoneBlockEntity) {
                if (player.getUuid().equals(((TombstoneBlockEntity) blockEntity).getOwner())) {
                    ((TombstoneBlockEntity) blockEntity).setNameText(tombstoneName);
                } else {
                    Overlord.getLogger().warn("Update Tombstone packet received with wrong player ID, expected {} and got {}.", ((TombstoneBlockEntity) blockEntity).getOwner(), player.getUuid());
                }
            } else {
                Overlord.getLogger().warn("Received save tombstone packet with missing tombstone block entity!");
            }
        });
    }
}
