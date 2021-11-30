package dev.the_fireplace.overlord.network.client.receiver;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.blockentity.TombstoneBlockEntity;
import dev.the_fireplace.overlord.client.gui.block.TombstoneGui;
import dev.the_fireplace.overlord.domain.network.ServerToClientPacketIDs;
import dev.the_fireplace.overlord.domain.network.client.OpenTombstoneGUIPacketReceiver;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import javax.inject.Inject;

@Environment(EnvType.CLIENT)
@Implementation
public final class OpenTombstoneGUIPacketReceiverImpl implements OpenTombstoneGUIPacketReceiver
{
    private final ServerToClientPacketIDs serverToClientPacketIDs;

    @Inject
    public OpenTombstoneGUIPacketReceiverImpl(ServerToClientPacketIDs serverToClientPacketIDs) {
        this.serverToClientPacketIDs = serverToClientPacketIDs;
    }

    @Override
    public Identifier getId() {
        return serverToClientPacketIDs.openTombstoneGuiPacketID();
    }

    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        if (client.world == null) {
            Overlord.getLogger().error("Received open tombstone packet with null client world!");
            return;
        }
        BlockEntity blockEntity = client.world.getBlockEntity(buf.readBlockPos());
        if (blockEntity instanceof TombstoneBlockEntity) {
            client.submit(() -> client.openScreen(new TombstoneGui((TombstoneBlockEntity) blockEntity)));
        } else {
            Overlord.getLogger().error("Received open tombstone packet with missing tombstone block entity!");
        }
    }
}
