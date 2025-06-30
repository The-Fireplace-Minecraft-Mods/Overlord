package dev.the_fireplace.overlord.domain.event;

import net.minecraft.server.level.ServerPlayer;

public interface PlayerJoinedServer
{
    void onPlayerJoinedServer(ServerPlayer player);
}
