package dev.the_fireplace.overlord.domain.network.server;

import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public interface OpenTombstoneGUIBufferBuilder
{
    PacketByteBuf build(BlockPos position);
}
