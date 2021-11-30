package dev.the_fireplace.overlord.network.server.builder;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.domain.network.server.OpenTombstoneGUIBufferBuilder;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

@Implementation
public final class OpenTombstoneGUIBufferBuilderImpl implements OpenTombstoneGUIBufferBuilder
{
    @Override
    public PacketByteBuf build(BlockPos position) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeBlockPos(position);
        return buffer;
    }
}
