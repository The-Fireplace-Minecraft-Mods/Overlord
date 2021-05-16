package dev.the_fireplace.overlord.network;

import dev.the_fireplace.overlord.api.network.ServerPacketRegistry;
import dev.the_fireplace.overlord.api.network.c2sPackets.GetOrdersPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public final class ServerPacketRegistryImpl implements ServerPacketRegistry {
    @Deprecated
    public static final ServerPacketRegistry INSTANCE = new ServerPacketRegistryImpl();

    private ServerPacketRegistryImpl() {}

    @Override
    public void registerPacketHandlers() {
        ServerPlayNetworking.registerGlobalReceiver(GetOrdersPacket.getInstance().getId(), GetOrdersPacket.getInstance());
    }
}
