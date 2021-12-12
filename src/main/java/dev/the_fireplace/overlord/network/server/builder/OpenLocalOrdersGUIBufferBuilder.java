package dev.the_fireplace.overlord.network.server.builder;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.util.PacketByteBuf;

public final class OpenLocalOrdersGUIBufferBuilder
{
    public static PacketByteBuf build(int localOrdersDistance) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeInt(localOrdersDistance);
        return buffer;
    }
}
