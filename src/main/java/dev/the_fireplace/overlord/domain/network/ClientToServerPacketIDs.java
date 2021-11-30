package dev.the_fireplace.overlord.domain.network;

import net.minecraft.util.Identifier;

public interface ClientToServerPacketIDs {
    Identifier getOrdersPacketID();

    Identifier getSquadsPacketID();

    Identifier saveAiPacketID();

    Identifier saveTombstonePacketID();
}
