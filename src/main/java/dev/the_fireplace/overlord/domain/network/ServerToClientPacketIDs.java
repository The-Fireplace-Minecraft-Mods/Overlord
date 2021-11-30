package dev.the_fireplace.overlord.domain.network;

import net.minecraft.util.Identifier;

public interface ServerToClientPacketIDs {
    Identifier openOrdersGuiPacketID();

    Identifier openSquadsGuiPacketID();

    Identifier openTombstoneGuiPacketID();
}
