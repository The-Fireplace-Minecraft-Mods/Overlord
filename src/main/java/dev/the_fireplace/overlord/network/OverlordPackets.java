package dev.the_fireplace.overlord.network;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.domain.network.ClientToServerPacketIDs;
import dev.the_fireplace.overlord.domain.network.ServerToClientPacketIDs;
import net.minecraft.util.Identifier;

@Implementation({
    "dev.the_fireplace.overlord.domain.network.ClientToServerPacketIDs",
    "dev.the_fireplace.overlord.domain.network.ServerToClientPacketIDs",
})
public final class OverlordPackets implements ClientToServerPacketIDs, ServerToClientPacketIDs {
    //S2C
    private static final Identifier OPEN_TOMBSTONE_GUI_PACKET_ID = new Identifier(Overlord.MODID, "open_tombstone_gui");
    private static final Identifier OPEN_ORDERS_GUI_ID = new Identifier(Overlord.MODID, "open_ai_gui");
    private static final Identifier OPEN_SQUADS_GUI_ID = new Identifier(Overlord.MODID, "open_squads_gui");

    //C2S
    private static final Identifier UPDATED_AI_PACKET_ID = new Identifier(Overlord.MODID, "updated_ai");
    private static final Identifier SAVE_TOMBSTONE_ID = new Identifier(Overlord.MODID, "tombstone_text");
    private static final Identifier GET_ORDERS_ID = new Identifier(Overlord.MODID, "get_ai");
    private static final Identifier GET_SQUADS_ID = new Identifier(Overlord.MODID, "get_squads");

    @Override
    public Identifier getOrdersPacketID() {
        return GET_ORDERS_ID;
    }

    @Override
    public Identifier getSquadsPacketID() {
        return GET_SQUADS_ID;
    }

    @Override
    public Identifier saveAiPacketID() {
        return UPDATED_AI_PACKET_ID;
    }

    @Override
    public Identifier saveTombstonePacketID() {
        return SAVE_TOMBSTONE_ID;
    }

    @Override
    public Identifier openOrdersGuiPacketID() {
        return OPEN_ORDERS_GUI_ID;
    }

    @Override
    public Identifier openSquadsGuiPacketID() {
        return OPEN_SQUADS_GUI_ID;
    }

    @Override
    public Identifier openTombstoneGuiPacketID() {
        return OPEN_TOMBSTONE_GUI_PACKET_ID;
    }
}
