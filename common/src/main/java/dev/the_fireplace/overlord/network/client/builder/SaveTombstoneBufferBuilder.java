package dev.the_fireplace.overlord.network.client.builder;

import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

import javax.inject.Singleton;

@Singleton
public final class SaveTombstoneBufferBuilder
{
    public FriendlyByteBuf build(BlockPos position, String text) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        buffer.writeBlockPos(position);
        buffer.writeUtf(text);
        return buffer;
    }
}
