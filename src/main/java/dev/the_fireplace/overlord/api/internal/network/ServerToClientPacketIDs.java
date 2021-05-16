package dev.the_fireplace.overlord.api.internal.network;

import dev.the_fireplace.overlord.network.OverlordPackets;
import net.minecraft.util.Identifier;

public interface ServerToClientPacketIDs {
    static ServerToClientPacketIDs getInstance() {
        //noinspection deprecation
        return OverlordPackets.INSTANCE;
    }
    Identifier openOrdersGuiPacketID();
}
