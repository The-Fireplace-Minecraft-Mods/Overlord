package dev.the_fireplace.overlord.api.internal.network.client;

import dev.the_fireplace.overlord.network.client.GetOrdersPacketBufferBuilderImpl;
import net.minecraft.util.PacketByteBuf;

public interface GetOrdersPacketBufferBuilder {
    static GetOrdersPacketBufferBuilder getInstance() {
        //noinspection deprecation
        return GetOrdersPacketBufferBuilderImpl.INSTANCE;
    }
    PacketByteBuf build(int aiEntityID);
}
