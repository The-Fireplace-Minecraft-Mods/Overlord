package dev.the_fireplace.overlord.network.client.builder;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.util.PacketByteBuf;

@Environment(EnvType.CLIENT)
public final class GetOrdersBufferBuilder
{
    public static PacketByteBuf build(int entityId) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeInt(entityId);
        return buffer;
    }
}
