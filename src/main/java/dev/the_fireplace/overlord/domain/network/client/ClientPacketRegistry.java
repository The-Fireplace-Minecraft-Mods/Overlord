package dev.the_fireplace.overlord.domain.network.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ClientPacketRegistry {
    void registerPacketHandlers();
}