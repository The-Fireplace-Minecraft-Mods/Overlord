package dev.the_fireplace.overlord.network.server;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.domain.network.server.OpenOrdersGUIBufferBuilder;
import dev.the_fireplace.overlord.entity.ai.aiconfig.AISettings;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.util.PacketByteBuf;

@Implementation
public final class OpenOrdersGUIBufferBuilderImpl implements OpenOrdersGUIBufferBuilder {
    @Override
    public PacketByteBuf build(int aiEntityID, AISettings entitySettings) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeInt(aiEntityID);
        buffer.writeCompoundTag(entitySettings.toTag());
        return buffer;
    }
}
