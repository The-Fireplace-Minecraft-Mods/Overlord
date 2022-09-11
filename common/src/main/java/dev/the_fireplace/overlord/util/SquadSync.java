package dev.the_fireplace.overlord.util;

import dev.the_fireplace.lib.api.network.injectables.PacketSender;
import dev.the_fireplace.overlord.domain.data.Squads;
import dev.the_fireplace.overlord.network.ClientboundPackets;
import dev.the_fireplace.overlord.network.server.builder.SyncSquadsBufferBuilder;
import net.minecraft.server.level.ServerPlayer;

import javax.inject.Inject;

public class SquadSync
{
    private final Squads squads;
    private final PacketSender packetSender;
    private final ClientboundPackets clientboundPackets;
    private final SyncSquadsBufferBuilder syncSquadsBufferBuilder;

    @Inject
    public SquadSync(Squads squads, PacketSender packetSender, ClientboundPackets clientboundPackets, SyncSquadsBufferBuilder syncSquadsBufferBuilder) {
        this.squads = squads;
        this.packetSender = packetSender;
        this.clientboundPackets = clientboundPackets;
        this.syncSquadsBufferBuilder = syncSquadsBufferBuilder;
    }

    public void syncTo(ServerPlayer player) {
        packetSender.sendToClient(
            player.connection,
            clientboundPackets.syncSquads(),
            syncSquadsBufferBuilder.build(squads.getSquads())
        );
    }
}
