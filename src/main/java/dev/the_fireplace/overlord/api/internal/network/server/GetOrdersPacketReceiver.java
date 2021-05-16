package dev.the_fireplace.overlord.api.internal.network.server;

import dev.the_fireplace.lib.api.network.server.ServerPacketReceiver;
import dev.the_fireplace.overlord.network.server.GetOrdersPacketReceiverImpl;

public interface GetOrdersPacketReceiver extends ServerPacketReceiver {
    static GetOrdersPacketReceiver getInstance() {
        //noinspection deprecation
        return GetOrdersPacketReceiverImpl.INSTANCE;
    }
}
