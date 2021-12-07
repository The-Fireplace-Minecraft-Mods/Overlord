package dev.the_fireplace.overlord.network.server.builder;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

import java.util.Collection;

public final class SquadUpdateFailedBufferBuilder
{
    public static PacketByteBuf build(Collection<Text> errorMessages) {
        PacketByteBuf buffer = PacketByteBufs.create();
        for (Text errorMessage : errorMessages) {
            buffer.writeText(errorMessage);
        }
        return buffer;
    }
}
