package dev.the_fireplace.overlord.network.client.receiver;

import dev.the_fireplace.lib.api.network.interfaces.ClientboundPacketReceiver;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.client.impl.data.ClientSquads;
import dev.the_fireplace.overlord.client.util.SquadDeserialization;
import dev.the_fireplace.overlord.domain.data.objects.Squad;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.UUID;

@Singleton
public final class SyncSquadsPacketReceiver implements ClientboundPacketReceiver
{
    private final ClientSquads clientSquads;

    @Inject
    public SyncSquadsPacketReceiver(ClientSquads clientSquads) {
        this.clientSquads = clientSquads;
    }

    @Override
    public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf) {
        CompoundTag squadCompound = buf.readNbt();
        if (squadCompound == null) {
            OverlordConstants.getLogger().error("Received sync squads packet with null squads!");
            return;
        }
        UUID owner = null;
        if (buf.isReadable()) {
            owner = buf.readUUID();
        }
        Collection<? extends Squad> squads = SquadDeserialization.collectionFromNbt(squadCompound);
        if (owner != null) {
            clientSquads.setSquadsFromOwner(owner, squads);
        } else {
            clientSquads.setSquads(squads);
        }
    }
}
