package dev.the_fireplace.overlord.network.client;

import dev.the_fireplace.lib.api.network.client.ClientPacketReceiverRegistry;
import dev.the_fireplace.overlord.api.internal.network.client.ClientPacketRegistry;
import dev.the_fireplace.overlord.api.internal.network.client.OpenOrdersGUIPacketReceiver;

public final class ClientPacketRegistryImpl implements ClientPacketRegistry {
    @Deprecated
    public static final ClientPacketRegistry INSTANCE = new ClientPacketRegistryImpl();

    private final ClientPacketReceiverRegistry registry = ClientPacketReceiverRegistry.getInstance();

    private ClientPacketRegistryImpl() {}

    @Override
    public void registerPacketHandlers() {
        registry.register(OpenOrdersGUIPacketReceiver.getInstance());
    }
}
