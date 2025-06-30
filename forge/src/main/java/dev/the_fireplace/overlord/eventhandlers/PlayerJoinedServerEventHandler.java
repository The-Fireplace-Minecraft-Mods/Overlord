package dev.the_fireplace.overlord.eventhandlers;

import com.google.inject.Inject;
import dev.the_fireplace.overlord.domain.event.PlayerJoinedServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class PlayerJoinedServerEventHandler
{
    private final PlayerJoinedServer playerJoinedServer;

    @Inject
    public PlayerJoinedServerEventHandler(PlayerJoinedServer playerJoinedServer) {
        this.playerJoinedServer = playerJoinedServer;
    }

    @SubscribeEvent
    public void onPlayerJoinedServer(net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            this.playerJoinedServer.onPlayerJoinedServer(serverPlayer);
        }
    }
}
