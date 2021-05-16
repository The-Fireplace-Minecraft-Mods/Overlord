package dev.the_fireplace.overlord.network.server;

import dev.the_fireplace.lib.api.network.server.ServerPacketReceiverRegistry;
import dev.the_fireplace.overlord.api.internal.network.server.GetOrdersPacketReceiver;
import dev.the_fireplace.overlord.api.internal.network.server.ServerPacketRegistry;

public final class ServerPacketRegistryImpl implements ServerPacketRegistry {
    @Deprecated
    public static final ServerPacketRegistry INSTANCE = new ServerPacketRegistryImpl();

    private final ServerPacketReceiverRegistry registry = ServerPacketReceiverRegistry.getInstance();

    private ServerPacketRegistryImpl() {}

    @Override
    public void registerPacketHandlers() {
        registry.register(GetOrdersPacketReceiver.getInstance());
    }
}
