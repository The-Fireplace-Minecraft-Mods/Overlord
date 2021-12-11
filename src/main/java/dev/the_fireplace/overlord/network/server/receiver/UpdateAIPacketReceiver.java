package dev.the_fireplace.overlord.network.server.receiver;

import dev.the_fireplace.lib.api.network.interfaces.ServerPacketReceiver;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.domain.entity.OrderableEntity;
import dev.the_fireplace.overlord.domain.entity.Ownable;
import dev.the_fireplace.overlord.item.OrdersWandItem;
import dev.the_fireplace.overlord.network.ClientToServerPacketIDs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

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
        NbtCompound aiTag = buf.readNbt();
        boolean isWandUpdate = entityId == -1;
        if (isWandUpdate) {
            updateWandAISettings(player, aiTag);
        } else {
            updateEntityAISettings(player, entityId, aiTag);
        }
    }

    private void updateWandAISettings(ServerPlayerEntity player, NbtCompound aiTag) {
        ItemStack wandStack = OrdersWandItem.getActiveWand(player);
        if (!wandStack.isEmpty()) {
            wandStack.getOrCreateTag().put("ai", aiTag);
        }
    }

    private void updateEntityAISettings(ServerPlayerEntity player, int entityId, NbtCompound aiTag) {
        Entity entity = player.getEntityWorld().getEntityById(entityId);
        if (!(entity instanceof OrderableEntity)) {
            Overlord.getLogger().info("Entity is not orderable: {}", Objects.toString(entity));
            return;
        }

        if (entity instanceof Ownable && !Objects.equals(((Ownable) entity).getOwnerUuid(), player.getUuid())) {
            Overlord.getLogger().warn("Save AI packet received with wrong player ID, expected {} and got {}.", ((Ownable) entity).getOwnerUuid(), player.getUuid());
            return;
        }

        if (aiTag != null) {
            ((OrderableEntity) entity).updateAISettings(aiTag);
        } else {
            Overlord.getLogger().error("No settings found in packet.");
        }
    }
}
