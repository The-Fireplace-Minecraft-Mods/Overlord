package dev.the_fireplace.overlord.api.network.s2cPackets;

import dev.the_fireplace.overlord.model.aiconfig.AISettings;
import dev.the_fireplace.overlord.network.s2c.OpenOrdersGUIPacketImpl;
import net.minecraft.util.PacketByteBuf;

public interface OpenOrdersGUIPacket extends ServerToClientPacket {
    static OpenOrdersGUIPacket getInstance() {
        //noinspection deprecation
        return OpenOrdersGUIPacketImpl.INSTANCE;
    }
    PacketByteBuf buildBuffer(int aiEntityID, AISettings entitySettings);
}
