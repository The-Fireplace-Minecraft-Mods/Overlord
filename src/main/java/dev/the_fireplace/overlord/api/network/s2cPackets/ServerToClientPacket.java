package dev.the_fireplace.overlord.api.network.s2cPackets;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

public interface ServerToClientPacket extends ClientPlayNetworking.PlayChannelHandler {
    Identifier getId();
}
