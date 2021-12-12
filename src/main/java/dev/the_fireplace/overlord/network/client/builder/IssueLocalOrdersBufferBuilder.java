package dev.the_fireplace.overlord.network.client.builder;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.util.PacketByteBuf;

@Environment(EnvType.CLIENT)
public final class IssueLocalOrdersBufferBuilder
{
    public static PacketByteBuf build() {
        PacketByteBuf buffer = PacketByteBufs.create();
        return buffer;
    }
}
