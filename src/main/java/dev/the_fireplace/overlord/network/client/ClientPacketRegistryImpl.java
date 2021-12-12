package dev.the_fireplace.overlord.network.client;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.lib.api.network.injectables.ClientPacketReceiverRegistry;
import dev.the_fireplace.overlord.domain.network.client.ClientPacketRegistry;
import dev.the_fireplace.overlord.domain.network.client.OpenOrdersGUIPacketReceiver;
import dev.the_fireplace.overlord.domain.network.client.OpenSquadsGUIPacketReceiver;
import dev.the_fireplace.overlord.domain.network.client.OpenTombstoneGUIPacketReceiver;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import javax.inject.Inject;

@Environment(EnvType.CLIENT)
@Implementation
public final class ClientPacketRegistryImpl implements ClientPacketRegistry
{
    private final ClientPacketReceiverRegistry registry;
    private final OpenOrdersGUIPacketReceiver openOrdersGUIPacketReceiver;
    private final OpenSquadsGUIPacketReceiver openSquadsGUIPacketReceiver;
    private final OpenTombstoneGUIPacketReceiver openTombstoneGUIPacketReceiver;

    @Inject
    public ClientPacketRegistryImpl(
        ClientPacketReceiverRegistry registry,
        OpenOrdersGUIPacketReceiver openOrdersGUIPacketReceiver,
        OpenSquadsGUIPacketReceiver openSquadsGUIPacketReceiver,
        OpenTombstoneGUIPacketReceiver openTombstoneGUIPacketReceiver
    ) {
        this.registry = registry;
        this.openOrdersGUIPacketReceiver = openOrdersGUIPacketReceiver;
        this.openSquadsGUIPacketReceiver = openSquadsGUIPacketReceiver;
        this.openTombstoneGUIPacketReceiver = openTombstoneGUIPacketReceiver;
    }

    @Override
    public void registerPacketHandlers() {
        registry.register(openOrdersGUIPacketReceiver);
        registry.register(openSquadsGUIPacketReceiver);
        registry.register(openTombstoneGUIPacketReceiver);
    }
}
