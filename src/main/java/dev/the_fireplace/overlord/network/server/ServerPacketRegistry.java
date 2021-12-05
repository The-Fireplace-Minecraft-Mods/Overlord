package dev.the_fireplace.overlord.network.server;

import dev.the_fireplace.lib.api.network.injectables.ServerPacketReceiverRegistry;
import dev.the_fireplace.overlord.network.server.receiver.GetOrdersPacketReceiver;
import dev.the_fireplace.overlord.network.server.receiver.SaveTombstonePacketReceiver;
import dev.the_fireplace.overlord.network.server.receiver.UpdateAIPacketReceiver;
import dev.the_fireplace.overlord.network.server.receiver.UpdateSquadPacketReceiver;

import javax.inject.Inject;

public final class ServerPacketRegistry
{
    private final ServerPacketReceiverRegistry registry;
    private final GetOrdersPacketReceiver getOrdersPacketReceiver;
    private final UpdateSquadPacketReceiver updateSquadPacketReceiver;
    private final UpdateAIPacketReceiver updateAIPacketReceiver;
    private final SaveTombstonePacketReceiver saveTombstonePacketReceiver;

    @Inject
    public ServerPacketRegistry(
        ServerPacketReceiverRegistry registry,
        GetOrdersPacketReceiver getOrdersPacketReceiver,
        UpdateSquadPacketReceiver updateSquadPacketReceiver,
        UpdateAIPacketReceiver updateAIPacketReceiver,
        SaveTombstonePacketReceiver saveTombstonePacketReceiver
    ) {
        this.registry = registry;
        this.getOrdersPacketReceiver = getOrdersPacketReceiver;
        this.updateSquadPacketReceiver = updateSquadPacketReceiver;
        this.updateAIPacketReceiver = updateAIPacketReceiver;
        this.saveTombstonePacketReceiver = saveTombstonePacketReceiver;
    }

    public void registerPacketHandlers() {
        registry.register(getOrdersPacketReceiver);
        registry.register(updateSquadPacketReceiver);
        registry.register(updateAIPacketReceiver);
        registry.register(saveTombstonePacketReceiver);
    }
}
