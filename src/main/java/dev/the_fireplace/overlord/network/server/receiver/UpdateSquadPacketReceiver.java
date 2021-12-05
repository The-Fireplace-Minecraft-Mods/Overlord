package dev.the_fireplace.overlord.network.server.receiver;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.lib.api.network.interfaces.ServerPacketReceiver;
import dev.the_fireplace.overlord.domain.data.Squads;
import dev.the_fireplace.overlord.network.ClientToServerPacketIDs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import javax.inject.Inject;

@Implementation
public final class UpdateSquadPacketReceiver implements ServerPacketReceiver
{
    private final Squads squads;

    @Inject
    public UpdateSquadPacketReceiver(
        Squads squads
    ) {
        this.squads = squads;
    }

    @Override
    public Identifier getId() {
        return ClientToServerPacketIDs.UPDATE_SQUAD;
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        //TODO verify that received squad data is valid
        //TODO put together error messages
        //TODO respond with updated squad data packet or failed update packet
    }
}
