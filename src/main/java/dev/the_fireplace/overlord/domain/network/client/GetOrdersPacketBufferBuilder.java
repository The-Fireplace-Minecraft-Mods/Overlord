package dev.the_fireplace.overlord.domain.network.client;

import net.minecraft.network.PacketByteBuf;

public interface GetOrdersPacketBufferBuilder {
    PacketByteBuf build(int aiEntityID);
}
