package dev.the_fireplace.overlord.network.client;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.lib.api.network.injectables.ClientPacketReceiverRegistry;
import dev.the_fireplace.overlord.domain.network.client.ClientPacketRegistry;
import dev.the_fireplace.overlord.domain.network.client.OpenOrdersGUIPacketReceiver;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import javax.inject.Inject;

@Environment(EnvType.CLIENT)
@Implementation
public final class ClientPacketRegistryImpl implements ClientPacketRegistry {
    private final ClientPacketReceiverRegistry registry;
    private final OpenOrdersGUIPacketReceiver openOrdersGUIPacketReceiver;

    @Inject
    public ClientPacketRegistryImpl(
        ClientPacketReceiverRegistry registry,
        OpenOrdersGUIPacketReceiver openOrdersGUIPacketReceiver
    ) {
        this.registry = registry;
        this.openOrdersGUIPacketReceiver = openOrdersGUIPacketReceiver;
    }

    @Override
    public void registerPacketHandlers() {
        registry.register(openOrdersGUIPacketReceiver);
    }
}
