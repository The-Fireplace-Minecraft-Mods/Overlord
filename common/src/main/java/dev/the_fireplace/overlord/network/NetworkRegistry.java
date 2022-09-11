package dev.the_fireplace.overlord.network;

import dev.the_fireplace.lib.api.network.injectables.PacketSpecificationRegistry;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class NetworkRegistry
{
    private final PacketSpecificationRegistry registry;
    private final ServerboundPackets serverboundPackets;
    private final ClientboundPackets clientboundPackets;

    @Inject
    public NetworkRegistry(PacketSpecificationRegistry registry, ServerboundPackets serverboundPackets, ClientboundPackets clientboundPackets) {
        this.registry = registry;
        this.serverboundPackets = serverboundPackets;
        this.clientboundPackets = clientboundPackets;
    }

    public void register() {
        registry.register(clientboundPackets.openLocalOrdersScreen());
        registry.register(clientboundPackets.openOrdersScreen());
        registry.register(clientboundPackets.openTombstoneScreen());
        registry.register(clientboundPackets.squadUpdated());
        registry.register(clientboundPackets.squadUpdateFailed());
        registry.register(clientboundPackets.syncSquads());
        registry.register(serverboundPackets.updateOrders());
        registry.register(serverboundPackets.deleteSquad());
        registry.register(serverboundPackets.getOrders());
        registry.register(serverboundPackets.saveTombstone());
        registry.register(serverboundPackets.setSquad());
        registry.register(serverboundPackets.issueLocalOrders());
        registry.register(serverboundPackets.updateSquad());
    }
}
