package dev.the_fireplace.overlord.network;

import dev.the_fireplace.overlord.Overlord;
import net.minecraft.util.Identifier;

public final class ClientToServerPacketIDs
{
    public static final Identifier UPDATE_AI = new Identifier(Overlord.MODID, "update_ai");
    public static final Identifier SAVE_TOMBSTONE = new Identifier(Overlord.MODID, "tombstone_text");
    public static final Identifier GET_ORDERS = new Identifier(Overlord.MODID, "get_ai");
    public static final Identifier UPDATE_SQUAD = new Identifier(Overlord.MODID, "update_squad");
    public static final Identifier DELETE_SQUAD = new Identifier(Overlord.MODID, "delete_squad");
    public static final Identifier SET_SQUAD = new Identifier(Overlord.MODID, "set_squad");
}
