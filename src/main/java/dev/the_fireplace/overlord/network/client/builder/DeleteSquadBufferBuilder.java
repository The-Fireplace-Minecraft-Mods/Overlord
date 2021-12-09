package dev.the_fireplace.overlord.network.client.builder;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;

import java.util.UUID;

@Environment(EnvType.CLIENT)
public final class DeleteSquadBufferBuilder
{
    public static PacketByteBuf build(UUID squadId) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeUuid(squadId);
        return buffer;
    }
}
