package dev.the_fireplace.overlord.network;

import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.api.internal.network.ClientToServerPacketIDs;
import dev.the_fireplace.overlord.api.internal.network.ServerToClientPacketIDs;
import net.minecraft.util.Identifier;

public final class OverlordPackets implements ClientToServerPacketIDs, ServerToClientPacketIDs {
    @Deprecated
    public static final OverlordPackets INSTANCE = new OverlordPackets();

    private OverlordPackets() {}
    //S2C
    private static final Identifier OPEN_SKELETON_GUI_PACKET_ID = new Identifier(Overlord.MODID, "open_skeleton_gui");
    private static final Identifier OPEN_CASKET_GUI_PACKET_ID = new Identifier(Overlord.MODID, "open_casket_gui");
    private static final Identifier OPEN_TOMBSTONE_GUI_PACKET_ID = new Identifier(Overlord.MODID, "open_tombstone_gui");
    private static final Identifier OPEN_ORDERS_GUI_ID = new Identifier(Overlord.MODID, "open_ai_gui");

    //C2S
    private static final Identifier UPDATED_AI_PACKET_ID = new Identifier(Overlord.MODID, "updated_ai");
    private static final Identifier TOMBSTONE_TEXT_ID = new Identifier(Overlord.MODID, "tombstone_text");
    private static final Identifier GET_ORDERS_ID = new Identifier(Overlord.MODID, "get_ai");

    @Override
    public Identifier getOrdersPacketID() {
        return GET_ORDERS_ID;
    }

    @Override
    public Identifier openOrdersGuiPacketID() {
        return OPEN_ORDERS_GUI_ID;
    }
}
