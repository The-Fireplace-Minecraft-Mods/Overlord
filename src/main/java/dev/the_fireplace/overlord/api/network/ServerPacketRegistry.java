package dev.the_fireplace.overlord.api.network;

import dev.the_fireplace.overlord.network.ServerPacketRegistryImpl;

public interface ServerPacketRegistry {
    @SuppressWarnings("deprecation")
    static ServerPacketRegistry getInstance() {
        return ServerPacketRegistryImpl.INSTANCE;
    }
    void registerPacketHandlers();
}
