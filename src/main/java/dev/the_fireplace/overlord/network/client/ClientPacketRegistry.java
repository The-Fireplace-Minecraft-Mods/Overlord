package dev.the_fireplace.overlord.network.client;

import dev.the_fireplace.lib.api.network.injectables.ClientPacketReceiverRegistry;
import dev.the_fireplace.overlord.network.client.receiver.OpenOrdersGUIPacketReceiver;
import dev.the_fireplace.overlord.network.client.receiver.OpenTombstoneGUIPacketReceiver;
import dev.the_fireplace.overlord.network.client.receiver.SyncSquadsPacketReceiver;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import javax.inject.Inject;

@Environment(EnvType.CLIENT)
public final class ClientPacketRegistry
{
    private final ClientPacketReceiverRegistry registry;
    private final OpenOrdersGUIPacketReceiver openOrdersGUIPacketReceiver;
    private final SyncSquadsPacketReceiver syncSquadsPacketReceiver;
    private final OpenTombstoneGUIPacketReceiver openTombstoneGUIPacketReceiver;

    @Inject
    public ClientPacketRegistry(
        ClientPacketReceiverRegistry registry,
        OpenOrdersGUIPacketReceiver openOrdersGUIPacketReceiver,
        SyncSquadsPacketReceiver syncSquadsPacketReceiver,
        OpenTombstoneGUIPacketReceiver openTombstoneGUIPacketReceiver
    ) {
        this.registry = registry;
        this.openOrdersGUIPacketReceiver = openOrdersGUIPacketReceiver;
        this.syncSquadsPacketReceiver = syncSquadsPacketReceiver;
        this.openTombstoneGUIPacketReceiver = openTombstoneGUIPacketReceiver;
    }

    public void registerPacketHandlers() {
        registry.register(openOrdersGUIPacketReceiver);
        registry.register(syncSquadsPacketReceiver);
        registry.register(openTombstoneGUIPacketReceiver);
    }
}
