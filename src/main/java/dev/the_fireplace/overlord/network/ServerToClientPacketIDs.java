package dev.the_fireplace.overlord.network;

import dev.the_fireplace.overlord.Overlord;
import net.minecraft.util.Identifier;

public final class ServerToClientPacketIDs
{
    public static final Identifier OPEN_TOMBSTONE_GUI = new Identifier(Overlord.MODID, "open_tombstone_gui");
    public static final Identifier OPEN_ORDERS_GUI = new Identifier(Overlord.MODID, "open_ai_gui");
    public static final Identifier SYNC_SQUADS = new Identifier(Overlord.MODID, "sync_squads");
    public static final Identifier SQUAD_UPDATED = new Identifier(Overlord.MODID, "squad_updated");
}
