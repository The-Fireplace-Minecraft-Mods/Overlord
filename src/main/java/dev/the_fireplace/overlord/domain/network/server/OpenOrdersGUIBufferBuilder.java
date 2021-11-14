package dev.the_fireplace.overlord.domain.network.server;

import dev.the_fireplace.overlord.entity.ai.aiconfig.AISettings;
import net.minecraft.network.PacketByteBuf;

public interface OpenOrdersGUIBufferBuilder {
    PacketByteBuf build(int aiEntityID, AISettings entitySettings);
}
