package dev.the_fireplace.overlord.network.client.receiver;

import dev.the_fireplace.lib.api.network.interfaces.ClientPacketReceiver;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.client.impl.data.ClientSquads;
import dev.the_fireplace.overlord.client.util.SquadDeserialization;
import dev.the_fireplace.overlord.domain.data.objects.Squad;
import dev.the_fireplace.overlord.network.ServerToClientPacketIDs;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import javax.inject.Inject;
import java.util.Collection;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public final class SyncSquadsPacketReceiver implements ClientPacketReceiver
{
    private final ClientSquads clientSquads;

    @Inject
    public SyncSquadsPacketReceiver(ClientSquads clientSquads) {
        this.clientSquads = clientSquads;
    }

    @Override
    public Identifier getId() {
        return ServerToClientPacketIDs.SYNC_SQUADS;
    }

    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        NbtCompound squadCompound = buf.readNbt();
        if (squadCompound == null) {
            Overlord.getLogger().error("Received sync squads packet with null squads!");
            return;
        }
        UUID owner = null;
        if (buf.isReadable()) {
            owner = buf.readUuid();
        }
        Collection<? extends Squad> squads = SquadDeserialization.collectionFromNbt(squadCompound);
        if (owner != null) {
            clientSquads.setSquadsFromOwner(owner, squads);
        } else {
            clientSquads.setSquads(squads);
        }
    }
}
