package dev.the_fireplace.overlord.network.server.receiver;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.lib.api.uuid.injectables.EmptyUUID;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.domain.data.Squads;
import dev.the_fireplace.overlord.domain.data.objects.Squad;
import dev.the_fireplace.overlord.domain.entity.Ownable;
import dev.the_fireplace.overlord.domain.network.ClientToServerPacketIDs;
import dev.the_fireplace.overlord.domain.network.ServerToClientPacketIDs;
import dev.the_fireplace.overlord.domain.network.server.GetSquadsPacketReceiver;
import dev.the_fireplace.overlord.domain.network.server.OpenSquadsGUIBufferBuilder;
import dev.the_fireplace.overlord.entity.ArmyEntity;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

@Implementation
public final class GetSquadsPacketReceiverImpl implements GetSquadsPacketReceiver
{
    private final ClientToServerPacketIDs clientToServerPacketIDs;
    private final ServerToClientPacketIDs serverToClientPacketIDs;
    private final OpenSquadsGUIBufferBuilder openSquadsGUIBufferBuilder;
    private final Squads squads;
    private final EmptyUUID emptyUUID;

    @Inject
    public GetSquadsPacketReceiverImpl(
        ClientToServerPacketIDs clientToServerPacketIDs,
        ServerToClientPacketIDs serverToClientPacketIDs,
        OpenSquadsGUIBufferBuilder openSquadsGUIBufferBuilder,
        Squads squads,
        EmptyUUID emptyUUID
    ) {
        this.clientToServerPacketIDs = clientToServerPacketIDs;
        this.serverToClientPacketIDs = serverToClientPacketIDs;
        this.openSquadsGUIBufferBuilder = openSquadsGUIBufferBuilder;
        this.squads = squads;
        this.emptyUUID = emptyUUID;
    }

    @Override
    public Identifier getId() {
        return clientToServerPacketIDs.getSquadsPacketID();
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        Collection<? extends Squad> playerSquads = squads.getSquadsWithOwner(player.getUuid());
        boolean isSelector = buf.isReadable();
        PacketByteBuf squadsPacketBuffer;
        if (isSelector) {
            squadsPacketBuffer = buildSelectorBuffer(player, buf, playerSquads);
        } else {
            squadsPacketBuffer = openSquadsGUIBufferBuilder.buildManager(playerSquads);
        }
        if (squadsPacketBuffer != null) {
            responseSender.sendPacket(serverToClientPacketIDs.openSquadsGuiPacketID(), squadsPacketBuffer);
        }
    }

    @Nullable
    private PacketByteBuf buildSelectorBuffer(ServerPlayerEntity player, PacketByteBuf buf, Collection<? extends Squad> playerSquads) {
        PacketByteBuf squadsPacketBuffer;
        int entityId = buf.readInt();
        Entity entity = player.getEntityWorld().getEntityById(entityId);
        if (!(entity instanceof ArmyEntity)) {
            Overlord.getLogger().info("Entity is not an army entity: {}", Objects.toString(entity));
            return null;
        }

        if (!Objects.equals(((Ownable) entity).getOwnerUuid(), player.getUuid())) {
            Overlord.getLogger().warn("Get Squads packet received with wrong player ID, expected {} and got {}.", ((Ownable) entity).getOwnerUuid(), player.getUuid());
            return null;
        }
        UUID squad = ((ArmyEntity) entity).getSquad();//TODO send empty UUID instead, and handle it when it's received.
        squadsPacketBuffer = openSquadsGUIBufferBuilder.buildSelector(entityId, playerSquads, emptyUUID.is(squad) ? null : squad);

        return squadsPacketBuffer;
    }
}
