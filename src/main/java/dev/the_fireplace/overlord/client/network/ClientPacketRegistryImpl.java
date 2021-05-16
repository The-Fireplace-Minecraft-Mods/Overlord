package dev.the_fireplace.overlord.client.network;

import dev.the_fireplace.overlord.api.client.network.ClientPacketRegistry;
import dev.the_fireplace.overlord.api.network.s2cPackets.OpenOrdersGUIPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class ClientPacketRegistryImpl implements ClientPacketRegistry {
    @Deprecated
    public static final ClientPacketRegistry INSTANCE = new ClientPacketRegistryImpl();

    private ClientPacketRegistryImpl() {}

    @Override
    public void registerPacketHandlers() {
        ClientPlayNetworking.registerGlobalReceiver(OpenOrdersGUIPacket.getInstance().getId(), OpenOrdersGUIPacket.getInstance());
    }
}
