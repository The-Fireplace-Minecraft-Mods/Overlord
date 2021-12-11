package dev.the_fireplace.overlord.network.client;

import dev.the_fireplace.lib.api.network.injectables.ClientPacketReceiverRegistry;
import dev.the_fireplace.overlord.network.client.receiver.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import javax.inject.Inject;

@Environment(EnvType.CLIENT)
public final class OverlordClientPacketReceivers
{
    private final ClientPacketReceiverRegistry registry;

    private final OpenOrdersGUIPacketReceiver openOrdersGUIPacketReceiver;
    private final SyncSquadsPacketReceiver syncSquadsPacketReceiver;
    private final OpenTombstoneGUIPacketReceiver openTombstoneGUIPacketReceiver;
    private final OpenLocalOrdersGUIPacketReceiver openLocalOrdersGUIPacketReceiver;
    private final SquadUpdatedPacketReceiver squadUpdatedPacketReceiver;
    private final SquadUpdateFailedPacketReceiver squadUpdateFailedPacketReceiver;

    @Inject
    public OverlordClientPacketReceivers(
        ClientPacketReceiverRegistry registry,
        OpenOrdersGUIPacketReceiver openOrdersGUIPacketReceiver,
        SyncSquadsPacketReceiver syncSquadsPacketReceiver,
        OpenTombstoneGUIPacketReceiver openTombstoneGUIPacketReceiver,
        OpenLocalOrdersGUIPacketReceiver openLocalOrdersGUIPacketReceiver,
        SquadUpdatedPacketReceiver squadUpdatedPacketReceiver,
        SquadUpdateFailedPacketReceiver squadUpdateFailedPacketReceiver
    ) {
        this.registry = registry;

        this.openOrdersGUIPacketReceiver = openOrdersGUIPacketReceiver;
        this.syncSquadsPacketReceiver = syncSquadsPacketReceiver;
        this.openTombstoneGUIPacketReceiver = openTombstoneGUIPacketReceiver;
        this.openLocalOrdersGUIPacketReceiver = openLocalOrdersGUIPacketReceiver;
        this.squadUpdatedPacketReceiver = squadUpdatedPacketReceiver;
        this.squadUpdateFailedPacketReceiver = squadUpdateFailedPacketReceiver;
    }

    public void registerPacketHandlers() {
        registry.register(openOrdersGUIPacketReceiver);
        registry.register(syncSquadsPacketReceiver);
        registry.register(openTombstoneGUIPacketReceiver);
        registry.register(openLocalOrdersGUIPacketReceiver);
        registry.register(squadUpdatedPacketReceiver);
        registry.register(squadUpdateFailedPacketReceiver);
    }
}
