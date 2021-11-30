package dev.the_fireplace.overlord.network.client.builder;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.domain.network.client.SaveTombstoneBufferBuilder;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

@Implementation
public final class SaveTombstoneBufferBuilderImpl implements SaveTombstoneBufferBuilder
{
    @Override
    public PacketByteBuf build(BlockPos position, String text) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeBlockPos(position);
        buffer.writeString(text);
        return buffer;
    }
}
