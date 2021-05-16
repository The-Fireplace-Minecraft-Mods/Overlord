package dev.the_fireplace.overlord.network.server;

import dev.the_fireplace.overlord.api.internal.network.server.OpenOrdersGUIBufferBuilder;
import dev.the_fireplace.overlord.model.aiconfig.AISettings;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.util.PacketByteBuf;

public final class OpenOrdersGUIBufferBuilderImpl implements OpenOrdersGUIBufferBuilder {
    @Deprecated
    public static final OpenOrdersGUIBufferBuilder INSTANCE = new OpenOrdersGUIBufferBuilderImpl();

    private OpenOrdersGUIBufferBuilderImpl() {}

    @Override
    public PacketByteBuf build(int aiEntityID, AISettings entitySettings) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeInt(aiEntityID);
        buffer.writeCompoundTag(entitySettings.toTag());
        return buffer;
    }
}
