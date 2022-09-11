package dev.the_fireplace.overlord.network;

import dev.the_fireplace.lib.api.network.interfaces.ServerboundPacketSpecification;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.network.packet.serverbound.*;
import net.minecraft.resources.ResourceLocation;

import javax.inject.Inject;

public final class ServerboundPackets
{
    public static final ResourceLocation UPDATE_ORDERS = new ResourceLocation(OverlordConstants.MODID, "update_ai");
    public static final ResourceLocation SAVE_TOMBSTONE = new ResourceLocation(OverlordConstants.MODID, "tombstone_text");
    public static final ResourceLocation GET_ORDERS = new ResourceLocation(OverlordConstants.MODID, "get_ai");
    public static final ResourceLocation UPDATE_SQUAD = new ResourceLocation(OverlordConstants.MODID, "update_squad");
    public static final ResourceLocation DELETE_SQUAD = new ResourceLocation(OverlordConstants.MODID, "delete_squad");
    public static final ResourceLocation SET_SQUAD = new ResourceLocation(OverlordConstants.MODID, "set_squad");
    public static final ResourceLocation ISSUE_LOCAL_ORDERS = new ResourceLocation(OverlordConstants.MODID, "issue_local_orders");

    private final ServerboundPacketSpecification updateOrders;
    private final ServerboundPacketSpecification saveTombstone;
    private final ServerboundPacketSpecification getOrders;
    private final ServerboundPacketSpecification updateSquad;
    private final ServerboundPacketSpecification deleteSquad;
    private final ServerboundPacketSpecification setSquad;
    private final ServerboundPacketSpecification issueLocalOrders;

    @Inject
    public ServerboundPackets(
        UpdateOrdersSpecification updateOrders,
        SaveTombstoneSpecification saveTombstone,
        GetOrdersSpecification getOrders,
        UpdateSquadSpecification updateSquad,
        DeleteSquadSpecification deleteSquad,
        SetSquadSpecification setSquad,
        IssueLocalOrdersSpecification issueLocalOrders
    ) {
        this.updateOrders = updateOrders;
        this.saveTombstone = saveTombstone;
        this.getOrders = getOrders;
        this.updateSquad = updateSquad;
        this.deleteSquad = deleteSquad;
        this.setSquad = setSquad;
        this.issueLocalOrders = issueLocalOrders;
    }

    public ServerboundPacketSpecification updateOrders() {
        return updateOrders;
    }

    public ServerboundPacketSpecification saveTombstone() {
        return saveTombstone;
    }

    public ServerboundPacketSpecification getOrders() {
        return getOrders;
    }

    public ServerboundPacketSpecification updateSquad() {
        return updateSquad;
    }

    public ServerboundPacketSpecification deleteSquad() {
        return deleteSquad;
    }

    public ServerboundPacketSpecification setSquad() {
        return setSquad;
    }

    public ServerboundPacketSpecification issueLocalOrders() {
        return issueLocalOrders;
    }
}
