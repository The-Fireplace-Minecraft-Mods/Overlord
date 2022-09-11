package dev.the_fireplace.overlord.network;

import dev.the_fireplace.lib.api.network.interfaces.ClientboundPacketSpecification;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.network.packet.clientbound.*;
import net.minecraft.resources.ResourceLocation;

import javax.inject.Inject;

public final class ClientboundPackets
{
    public static final ResourceLocation OPEN_TOMBSTONE_SCREEN = new ResourceLocation(OverlordConstants.MODID, "open_tombstone_gui");
    public static final ResourceLocation OPEN_ORDERS_SCREEN = new ResourceLocation(OverlordConstants.MODID, "open_ai_gui");
    public static final ResourceLocation OPEN_LOCAL_ORDERS_SCREEN = new ResourceLocation(OverlordConstants.MODID, "open_local_orders_gui");
    public static final ResourceLocation SYNC_SQUADS = new ResourceLocation(OverlordConstants.MODID, "sync_squads");
    public static final ResourceLocation SQUAD_UPDATED = new ResourceLocation(OverlordConstants.MODID, "squad_updated");
    public static final ResourceLocation SQUAD_UPDATE_FAILED = new ResourceLocation(OverlordConstants.MODID, "squad_update_failed");

    private final ClientboundPacketSpecification openTombstoneScreen;
    private final ClientboundPacketSpecification openOrdersScreen;
    private final ClientboundPacketSpecification openLocalOrdersScreen;
    private final ClientboundPacketSpecification syncSquads;
    private final ClientboundPacketSpecification squadUpdated;
    private final ClientboundPacketSpecification squadUpdateFailed;

    @Inject
    public ClientboundPackets(
        OpenTombstoneScreenSpecification openTombstoneScreen,
        OpenOrdersScreenSpecification openOrdersScreen,
        OpenLocalOrdersScreenSpecification openLocalOrdersScreen,
        SyncSquadsSpecification syncSquads,
        SquadUpdatedSpecification squadUpdated,
        SquadUpdateFailedSpecification squadUpdateFailed
    ) {
        this.openTombstoneScreen = openTombstoneScreen;
        this.openOrdersScreen = openOrdersScreen;
        this.openLocalOrdersScreen = openLocalOrdersScreen;
        this.syncSquads = syncSquads;
        this.squadUpdated = squadUpdated;
        this.squadUpdateFailed = squadUpdateFailed;
    }

    public ClientboundPacketSpecification openTombstoneScreen() {
        return openTombstoneScreen;
    }

    public ClientboundPacketSpecification openOrdersScreen() {
        return openOrdersScreen;
    }

    public ClientboundPacketSpecification openLocalOrdersScreen() {
        return openLocalOrdersScreen;
    }

    public ClientboundPacketSpecification syncSquads() {
        return syncSquads;
    }

    public ClientboundPacketSpecification squadUpdated() {
        return squadUpdated;
    }

    public ClientboundPacketSpecification squadUpdateFailed() {
        return squadUpdateFailed;
    }
}
