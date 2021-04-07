package dev.the_fireplace.overlord.network;

import dev.the_fireplace.overlord.Overlord;
import net.minecraft.util.Identifier;

public class OverlordPackets {
    //S2C
    public static final Identifier OPEN_SKELETON_GUI_PACKET_ID = new Identifier(Overlord.MODID, "open_skeleton_gui");
    public static final Identifier OPEN_CASKET_GUI_PACKET_ID = new Identifier(Overlord.MODID, "open_casket_gui");
    public static final Identifier OPEN_TOMBSTONE_GUI_PACKET_ID = new Identifier(Overlord.MODID, "open_tombstone_gui");
    public static final Identifier OPEN_AI_GUI_PACKET_ID = new Identifier(Overlord.MODID, "open_ai_gui");
    //C2S
    public static final Identifier GET_AI_PACKET_ID = new Identifier(Overlord.MODID, "get_ai");
    public static final Identifier UPDATED_AI_PACKET_ID = new Identifier(Overlord.MODID, "updated_ai");
    public static final Identifier TOMBSTONE_TEXT_ID = new Identifier(Overlord.MODID, "tombstone_text");
}
