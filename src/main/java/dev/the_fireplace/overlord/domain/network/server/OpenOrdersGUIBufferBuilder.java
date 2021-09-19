package dev.the_fireplace.overlord.domain.network.server;

import dev.the_fireplace.overlord.model.aiconfig.AISettings;
import net.minecraft.util.PacketByteBuf;

public interface OpenOrdersGUIBufferBuilder {
    PacketByteBuf build(int aiEntityID, AISettings entitySettings);
}
