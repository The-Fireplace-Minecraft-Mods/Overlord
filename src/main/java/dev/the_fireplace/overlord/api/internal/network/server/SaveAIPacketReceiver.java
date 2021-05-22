package dev.the_fireplace.overlord.api.internal.network.server;

import dev.the_fireplace.lib.api.network.server.ServerPacketReceiver;
import dev.the_fireplace.overlord.network.server.SaveAIPacketReceiverImpl;

public interface SaveAIPacketReceiver extends ServerPacketReceiver {
    static SaveAIPacketReceiver getInstance() {
        //noinspection deprecation
        return SaveAIPacketReceiverImpl.INSTANCE;
    }
}
