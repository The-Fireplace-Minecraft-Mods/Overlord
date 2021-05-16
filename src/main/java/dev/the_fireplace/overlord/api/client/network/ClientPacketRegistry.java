package dev.the_fireplace.overlord.api.client.network;

import dev.the_fireplace.overlord.client.network.ClientPacketRegistryImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ClientPacketRegistry {
    @SuppressWarnings("deprecation")
    static ClientPacketRegistry getInstance() {
        return ClientPacketRegistryImpl.INSTANCE;
    }
    void registerPacketHandlers();
}
