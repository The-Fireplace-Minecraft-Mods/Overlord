package dev.the_fireplace.overlord.network.server;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.lib.api.network.injectables.ServerPacketReceiverRegistry;
import dev.the_fireplace.overlord.domain.network.server.*;

import javax.inject.Inject;

@Implementation
public final class ServerPacketRegistryImpl implements ServerPacketRegistry
{
    private final ServerPacketReceiverRegistry registry;
    private final GetOrdersPacketReceiver getOrdersPacketReceiver;
    private final GetSquadsPacketReceiver getSquadsPacketReceiver;
    private final SaveAIPacketReceiver saveAIPacketReceiver;
    private final SaveTombstonePacketReceiver saveTombstonePacketReceiver;

    @Inject
    public ServerPacketRegistryImpl(
        ServerPacketReceiverRegistry registry,
        GetOrdersPacketReceiver getOrdersPacketReceiver,
        GetSquadsPacketReceiver getSquadsPacketReceiver, SaveAIPacketReceiver saveAIPacketReceiver,
        SaveTombstonePacketReceiver saveTombstonePacketReceiver
    ) {
        this.registry = registry;
        this.getOrdersPacketReceiver = getOrdersPacketReceiver;
        this.getSquadsPacketReceiver = getSquadsPacketReceiver;
        this.saveAIPacketReceiver = saveAIPacketReceiver;
        this.saveTombstonePacketReceiver = saveTombstonePacketReceiver;
    }

    @Override
    public void registerPacketHandlers() {
        registry.register(getOrdersPacketReceiver);
        registry.register(getSquadsPacketReceiver);
        registry.register(saveAIPacketReceiver);
        registry.register(saveTombstonePacketReceiver);
    }
}
