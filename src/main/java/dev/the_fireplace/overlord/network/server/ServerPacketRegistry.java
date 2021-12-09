package dev.the_fireplace.overlord.network.server;

import dev.the_fireplace.lib.api.network.injectables.ServerPacketReceiverRegistry;
import dev.the_fireplace.overlord.network.server.receiver.*;

import javax.inject.Inject;

public final class ServerPacketRegistry
{
    private final ServerPacketReceiverRegistry registry;
    private final GetOrdersPacketReceiver getOrdersPacketReceiver;
    private final UpdateSquadPacketReceiver updateSquadPacketReceiver;
    private final UpdateAIPacketReceiver updateAIPacketReceiver;
    private final SaveTombstonePacketReceiver saveTombstonePacketReceiver;
    private final SetSquadPacketReceiver setSquadPacketReceiver;

    @Inject
    public ServerPacketRegistry(
        ServerPacketReceiverRegistry registry,
        GetOrdersPacketReceiver getOrdersPacketReceiver,
        UpdateSquadPacketReceiver updateSquadPacketReceiver,
        UpdateAIPacketReceiver updateAIPacketReceiver,
        SaveTombstonePacketReceiver saveTombstonePacketReceiver,
        SetSquadPacketReceiver setSquadPacketReceiver
    ) {
        this.registry = registry;
        this.getOrdersPacketReceiver = getOrdersPacketReceiver;
        this.updateSquadPacketReceiver = updateSquadPacketReceiver;
        this.updateAIPacketReceiver = updateAIPacketReceiver;
        this.saveTombstonePacketReceiver = saveTombstonePacketReceiver;
        this.setSquadPacketReceiver = setSquadPacketReceiver;
    }

    public void registerPacketHandlers() {
        registry.register(getOrdersPacketReceiver);
        registry.register(updateSquadPacketReceiver);
        registry.register(updateAIPacketReceiver);
        registry.register(saveTombstonePacketReceiver);
        registry.register(setSquadPacketReceiver);
    }
}
