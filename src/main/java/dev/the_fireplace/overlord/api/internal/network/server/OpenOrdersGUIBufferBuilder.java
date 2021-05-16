package dev.the_fireplace.overlord.api.internal.network.server;

import dev.the_fireplace.overlord.model.aiconfig.AISettings;
import dev.the_fireplace.overlord.network.server.OpenOrdersGUIBufferBuilderImpl;
import net.minecraft.util.PacketByteBuf;

public interface OpenOrdersGUIBufferBuilder {
    static OpenOrdersGUIBufferBuilder getInstance() {
        //noinspection deprecation
        return OpenOrdersGUIBufferBuilderImpl.INSTANCE;
    }
    PacketByteBuf build(int aiEntityID, AISettings entitySettings);
}
