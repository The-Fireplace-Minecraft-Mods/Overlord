package dev.the_fireplace.overlord.network.server.builder;

import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

import javax.inject.Singleton;

@Singleton
public final class OpenTombstoneGUIBufferBuilder
{
    public FriendlyByteBuf build(BlockPos position) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        buffer.writeBlockPos(position);
        return buffer;
    }
}
