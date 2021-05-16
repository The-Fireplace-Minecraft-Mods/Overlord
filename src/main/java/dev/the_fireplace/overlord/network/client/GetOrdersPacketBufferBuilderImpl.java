package dev.the_fireplace.overlord.network.client;

import dev.the_fireplace.overlord.api.internal.network.client.GetOrdersPacketBufferBuilder;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.util.PacketByteBuf;

public final class GetOrdersPacketBufferBuilderImpl implements GetOrdersPacketBufferBuilder {
    @Deprecated
    public static final GetOrdersPacketBufferBuilder INSTANCE = new GetOrdersPacketBufferBuilderImpl();

    private GetOrdersPacketBufferBuilderImpl() {}

    @Override
    public PacketByteBuf build(int aiEntityID) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeInt(aiEntityID);
        return buffer;
    }
}
