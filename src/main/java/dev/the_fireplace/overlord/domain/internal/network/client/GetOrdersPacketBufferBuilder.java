package dev.the_fireplace.overlord.domain.internal.network.client;

import net.minecraft.util.PacketByteBuf;

public interface GetOrdersPacketBufferBuilder {
    PacketByteBuf build(int aiEntityID);
}
