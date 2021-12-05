package dev.the_fireplace.overlord.network.server.builder;

import dev.the_fireplace.overlord.domain.data.objects.Squad;
import dev.the_fireplace.overlord.util.SquadSerialization;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;

public final class SquadUpdatedBufferBuilder
{
    public static PacketByteBuf build(Squad squad) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeNbt(SquadSerialization.toNbt(squad));
        return buffer;
    }
}
