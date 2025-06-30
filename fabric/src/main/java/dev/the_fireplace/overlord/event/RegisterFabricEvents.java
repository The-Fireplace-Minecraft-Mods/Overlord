package dev.the_fireplace.overlord.event;

import com.google.inject.Inject;
import dev.the_fireplace.overlord.domain.event.PlayerJoinedServer;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public final class RegisterFabricEvents
{
    private final PlayerJoinedServer playerJoinedServer;

    @Inject
    public RegisterFabricEvents(PlayerJoinedServer playerJoinedServer)
    {
        this.playerJoinedServer = playerJoinedServer;
    }

    public void registerEvents()
    {
        ServerPlayConnectionEvents.JOIN.register((ServerGamePacketListenerImpl serverGamePacketListener, PacketSender packetSender, MinecraftServer minecraftServer) -> {
            this.playerJoinedServer.onPlayerJoinedServer(serverGamePacketListener.getPlayer());
        });
    }
}
