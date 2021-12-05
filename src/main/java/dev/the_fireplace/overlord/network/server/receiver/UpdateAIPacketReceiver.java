package dev.the_fireplace.overlord.network.server.receiver;

import dev.the_fireplace.lib.api.network.interfaces.ServerPacketReceiver;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.domain.entity.OrderableEntity;
import dev.the_fireplace.overlord.domain.entity.Ownable;
import dev.the_fireplace.overlord.network.ClientToServerPacketIDs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import java.util.Objects;

public final class UpdateAIPacketReceiver implements ServerPacketReceiver
{
    @Override
    public Identifier getId() {
        return ClientToServerPacketIDs.UPDATE_AI;
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

        CompoundTag aiTag = buf.readCompoundTag();
        if (aiTag != null) {
            ((OrderableEntity) entity).updateAISettings(aiTag);
        } else {
            Overlord.getLogger().error("No settings found in packet.");
        }
    }
}
