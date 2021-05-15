package dev.the_fireplace.overlord.api.network.c2sPackets;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public interface ClientToServerPacket extends ServerPlayNetworking.PlayChannelHandler {
    Identifier getId();
}
