package dev.the_fireplace.overlord.network.server.receiver;

import dev.the_fireplace.lib.api.network.interfaces.ServerPacketReceiver;
import dev.the_fireplace.overlord.domain.data.Squads;
import dev.the_fireplace.overlord.network.ClientToServerPacketIDs;
import dev.the_fireplace.overlord.network.ServerToClientPacketIDs;
import dev.the_fireplace.overlord.network.server.builder.SyncSquadsBufferBuilder;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import javax.inject.Inject;
import java.util.UUID;

public final class DeleteSquadPacketReceiver implements ServerPacketReceiver
{
    private final Squads squads;

    @Inject
    public DeleteSquadPacketReceiver(Squads squads) {
        this.squads = squads;
    }

    @Override
    public Identifier getId() {
        return ClientToServerPacketIDs.DELETE_SQUAD;
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        UUID squadId = buf.readUuid();
        squads.removeSquad(player.getUuid(), squadId);
        server.getPlayerManager().sendToAll(
            responseSender.createPacket(
                ServerToClientPacketIDs.SYNC_SQUADS,
                SyncSquadsBufferBuilder.buildForOneOwner(player.getUuid(), squads.getSquadsWithOwner(player.getUuid()))
            )
        );
    }
}
