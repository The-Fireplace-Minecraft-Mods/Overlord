package dev.the_fireplace.overlord.network.server;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.domain.entity.OrderableEntity;
import dev.the_fireplace.overlord.domain.entity.Ownable;
import dev.the_fireplace.overlord.domain.network.ClientToServerPacketIDs;
import dev.the_fireplace.overlord.domain.network.server.SaveAIPacketReceiver;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import javax.inject.Inject;
import java.util.Objects;

@Implementation
public final class SaveAIPacketReceiverImpl implements SaveAIPacketReceiver {

    private final ClientToServerPacketIDs clientToServerPacketIDs;

    @Inject
    private SaveAIPacketReceiverImpl(ClientToServerPacketIDs clientToServerPacketIDs) {
        this.clientToServerPacketIDs = clientToServerPacketIDs;
    }

    @Override
    public Identifier getId() {
        return clientToServerPacketIDs.saveAiPacketID();
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        int entityId = buf.readInt();
        //TODO Check which thread this runs on
        Entity entity = player.getEntityWorld().getEntityById(entityId);
        if (!(entity instanceof OrderableEntity)) {
            Overlord.getLogger().info("Entity is not orderable: {}", Objects.toString(entity));
            return;
        }

        if (entity instanceof Ownable && !Objects.equals(((Ownable) entity).getOwnerUniqueId(), player.getUuid())) {
            Overlord.getLogger().warn("Save AI packet received with wrong player ID, expected {} and got {}.", ((Ownable) entity).getOwnerUniqueId(), player.getUuid());
            return;
        }

        NbtCompound aiTag = buf.readNbt();
        if (aiTag != null) {
            ((OrderableEntity) entity).updateAISettings(aiTag);
        } else {
            Overlord.getLogger().error("No settings found in packet.");
        }
    }
}
