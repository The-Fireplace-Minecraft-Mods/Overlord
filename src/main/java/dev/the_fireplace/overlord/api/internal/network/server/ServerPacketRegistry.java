package dev.the_fireplace.overlord.api.internal.network.server;

import dev.the_fireplace.overlord.network.server.ServerPacketRegistryImpl;

public interface ServerPacketRegistry {
    @SuppressWarnings("deprecation")
    static ServerPacketRegistry getInstance() {
        return ServerPacketRegistryImpl.INSTANCE;
    }
    void registerPacketHandlers();
}
