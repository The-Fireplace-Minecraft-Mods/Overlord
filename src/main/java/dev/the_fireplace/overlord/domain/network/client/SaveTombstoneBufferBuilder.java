package dev.the_fireplace.overlord.domain.network.client;

import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public interface SaveTombstoneBufferBuilder
{
    PacketByteBuf build(BlockPos position, String text);
}
