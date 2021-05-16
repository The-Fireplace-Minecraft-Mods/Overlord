package dev.the_fireplace.overlord.api.internal.network.client;

import dev.the_fireplace.overlord.network.client.ClientPacketRegistryImpl;
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
