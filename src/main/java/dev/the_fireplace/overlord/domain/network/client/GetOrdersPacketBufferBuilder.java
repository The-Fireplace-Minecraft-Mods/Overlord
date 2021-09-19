package dev.the_fireplace.overlord.domain.network.client;

import net.minecraft.util.PacketByteBuf;

public interface GetOrdersPacketBufferBuilder {
    PacketByteBuf build(int aiEntityID);
}
