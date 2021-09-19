package dev.the_fireplace.overlord.network.client;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.domain.network.client.GetOrdersPacketBufferBuilder;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.util.PacketByteBuf;

@Implementation
public final class GetOrdersPacketBufferBuilderImpl implements GetOrdersPacketBufferBuilder {
    @Override
    public PacketByteBuf build(int aiEntityID) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeInt(aiEntityID);
        return buffer;
    }
}
