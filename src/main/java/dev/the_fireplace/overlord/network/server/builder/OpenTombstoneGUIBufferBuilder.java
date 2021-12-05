package dev.the_fireplace.overlord.network.server.builder;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public final class OpenTombstoneGUIBufferBuilder
{
    public static PacketByteBuf build(BlockPos position) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeBlockPos(position);
        return buffer;
    }
}
