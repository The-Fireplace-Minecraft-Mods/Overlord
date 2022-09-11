package dev.the_fireplace.overlord.network.packet.serverbound;

import dev.the_fireplace.lib.api.network.interfaces.ServerboundPacketReceiver;
import dev.the_fireplace.lib.api.network.interfaces.ServerboundPacketSpecification;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.network.ServerboundPackets;
import dev.the_fireplace.overlord.network.server.receiver.SetSquadPacketReceiver;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public final class SetSquadSpecification implements ServerboundPacketSpecification
{
    @Override
    public Supplier<ServerboundPacketReceiver> getReceiverFactory() {
        return () -> OverlordConstants.getInjector().getInstance(SetSquadPacketReceiver.class);
    }

    @Override
    public ResourceLocation getPacketID() {
        return ServerboundPackets.SET_SQUAD;
    }
}
