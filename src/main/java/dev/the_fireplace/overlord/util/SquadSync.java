package dev.the_fireplace.overlord.util;

import dev.the_fireplace.overlord.domain.data.Squads;
import dev.the_fireplace.overlord.network.ServerToClientPacketIDs;
import dev.the_fireplace.overlord.network.server.builder.SyncSquadsBufferBuilder;
import net.fabricmc.fabric.api.networking.v1.PacketSender;

import javax.inject.Inject;

public class SquadSync
{
    private final Squads squads;

    @Inject
    public SquadSync(Squads squads) {
        this.squads = squads;
    }

    public void syncTo(PacketSender packetSender) {
        packetSender.sendPacket(
            ServerToClientPacketIDs.SYNC_SQUADS,
            SyncSquadsBufferBuilder.build(squads.getSquads())
        );
    }
}
