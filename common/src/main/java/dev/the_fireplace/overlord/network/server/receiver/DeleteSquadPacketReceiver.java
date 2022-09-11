package dev.the_fireplace.overlord.network.server.receiver;

import dev.the_fireplace.lib.api.network.injectables.PacketSender;
import dev.the_fireplace.lib.api.network.interfaces.ClientboundPacketSpecification;
import dev.the_fireplace.lib.api.network.interfaces.ServerboundPacketReceiver;
import dev.the_fireplace.overlord.domain.data.Squads;
import dev.the_fireplace.overlord.network.ClientboundPackets;
import dev.the_fireplace.overlord.network.server.builder.SyncSquadsBufferBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.UUID;

@Singleton
public final class DeleteSquadPacketReceiver implements ServerboundPacketReceiver
{
    private final Squads squads;
    private final ClientboundPackets clientboundPackets;
    private final PacketSender packetSender;
    private final SyncSquadsBufferBuilder syncSquadsBufferBuilder;

    @Inject
    public DeleteSquadPacketReceiver(Squads squads, SyncSquadsBufferBuilder syncSquadsBufferBuilder, ClientboundPackets clientboundPackets, PacketSender packetSender) {
        this.squads = squads;
        this.clientboundPackets = clientboundPackets;
        this.packetSender = packetSender;
        this.syncSquadsBufferBuilder = syncSquadsBufferBuilder;
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf) {
        UUID squadId = buf.readUUID();
        squads.removeSquad(player.getUUID(), squadId);
        syncSquadChangeToClients(server, player.getUUID());
    }

    private void syncSquadChangeToClients(MinecraftServer server, UUID squadOwner) {
        ClientboundPacketSpecification specification = clientboundPackets.syncSquads();
        FriendlyByteBuf packetContents = syncSquadsBufferBuilder.buildForOneOwner(squadOwner, squads.getSquadsWithOwner(squadOwner));
        for (ServerPlayer onlinePlayer : server.getPlayerList().getPlayers()) {
            this.packetSender.sendToClient(
                onlinePlayer.connection,
                specification,
                packetContents
            );
        }
    }
}
