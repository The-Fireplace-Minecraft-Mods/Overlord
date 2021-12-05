package dev.the_fireplace.overlord.network.server.builder;

import dev.the_fireplace.overlord.entity.ai.aiconfig.AISettings;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.util.PacketByteBuf;

public final class OpenOrdersGUIBufferBuilder
{
    public static PacketByteBuf build(int aiEntityID, AISettings entitySettings) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeInt(aiEntityID);
        buffer.writeCompoundTag(entitySettings.toTag());
        return buffer;
    }
}
