package the_fireplace.overlord.fabric.network;

import net.minecraft.util.Identifier;
import the_fireplace.overlord.OverlordHelper;

public class OverlordPackets {
    //S2C
    public static final Identifier OPEN_SKELETON_GUI_PACKET_ID = new Identifier(OverlordHelper.MODID, "open_skeleton_gui");
    public static final Identifier OPEN_CASKET_GUI_PACKET_ID = new Identifier(OverlordHelper.MODID, "open_casket_gui");
    public static final Identifier OPEN_TOMBSTONE_GUI_PACKET_ID = new Identifier(OverlordHelper.MODID, "open_tombstone_gui");
    public static final Identifier OPEN_AI_GUI_PACKET_ID = new Identifier(OverlordHelper.MODID, "open_ai_gui");
    //C2S
    public static final Identifier GET_AI_PACKET_ID = new Identifier(OverlordHelper.MODID, "get_ai");
    public static final Identifier UPDATED_AI_PACKET_ID = new Identifier(OverlordHelper.MODID, "updated_ai");
    public static final Identifier TOMBSTONE_TEXT_ID = new Identifier(OverlordHelper.MODID, "tombstone_text");
}
