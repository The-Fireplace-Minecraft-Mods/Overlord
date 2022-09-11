package dev.the_fireplace.overlord.network.packet.clientbound;

import dev.the_fireplace.lib.api.network.interfaces.ClientboundPacketReceiver;
import dev.the_fireplace.lib.api.network.interfaces.ClientboundPacketSpecification;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.network.ClientboundPackets;
import dev.the_fireplace.overlord.network.client.receiver.OpenTombstoneGUIPacketReceiver;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public final class OpenTombstoneScreenSpecification implements ClientboundPacketSpecification
{
    @Override
    public Supplier<ClientboundPacketReceiver> getReceiverFactory() {
        return () -> OverlordConstants.getInjector().getInstance(OpenTombstoneGUIPacketReceiver.class);
    }

    @Override
    public ResourceLocation getPacketID() {
        return ClientboundPackets.OPEN_TOMBSTONE_SCREEN;
    }
}
