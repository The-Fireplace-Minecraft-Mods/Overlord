package dev.the_fireplace.overlord.api.internal.network.client;

import dev.the_fireplace.lib.api.network.client.ClientPacketReceiver;
import dev.the_fireplace.overlord.network.client.OpenOrdersGUIPacketReceiverImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface OpenOrdersGUIPacketReceiver extends ClientPacketReceiver {
    static OpenOrdersGUIPacketReceiver getInstance() {
        //noinspection deprecation
        return OpenOrdersGUIPacketReceiverImpl.INSTANCE;
    }
}
