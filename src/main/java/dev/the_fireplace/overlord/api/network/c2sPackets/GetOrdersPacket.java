package dev.the_fireplace.overlord.api.network.c2sPackets;

import dev.the_fireplace.overlord.network.c2s.GetAIPacketHandler;
import net.minecraft.util.PacketByteBuf;

public interface GetOrdersPacket extends ClientToServerPacket {
    static GetOrdersPacket getInstance() {
        //noinspection deprecation
        return GetAIPacketHandler.INSTANCE;
    }

    PacketByteBuf buildBuffer(int aiEntityID);
}
