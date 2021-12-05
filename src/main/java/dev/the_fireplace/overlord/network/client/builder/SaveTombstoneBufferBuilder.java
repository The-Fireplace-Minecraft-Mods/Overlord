package dev.the_fireplace.overlord.network.client.builder;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public final class SaveTombstoneBufferBuilder
{
    public static PacketByteBuf build(BlockPos position, String text) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeBlockPos(position);
        buffer.writeString(text);
        return buffer;
    }
}
