package dev.the_fireplace.overlord.network.server.receiver;

import dev.the_fireplace.lib.api.network.interfaces.ServerboundPacketReceiver;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.domain.entity.OrderableEntity;
import dev.the_fireplace.overlord.domain.entity.Ownable;
import dev.the_fireplace.overlord.item.OrdersWandItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import javax.inject.Singleton;
import java.util.Objects;

@Singleton
public final class UpdateOrdersPacketReceiver implements ServerboundPacketReceiver
{
    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf) {
        int entityId = buf.readInt();
        CompoundTag aiTag = buf.readNbt();
        boolean isWandUpdate = entityId == -1;
        if (isWandUpdate) {
            updateWandAISettings(player, aiTag);
        } else {
            updateEntityAISettings(player, entityId, aiTag);
        }
    }

    private void updateWandAISettings(ServerPlayer player, CompoundTag aiTag) {
        ItemStack wandStack = OrdersWandItem.getActiveWand(player);
        if (!wandStack.isEmpty()) {
            wandStack.getOrCreateTag().put("ai", aiTag);
        }
    }

    private void updateEntityAISettings(ServerPlayer player, int entityId, CompoundTag aiTag) {
        Entity entity = player.getCommandSenderWorld().getEntity(entityId);
        if (!(entity instanceof OrderableEntity)) {
            OverlordConstants.getLogger().info("Entity is not orderable: {}", Objects.toString(entity));
            return;
        }

        if (entity instanceof Ownable && !Objects.equals(((Ownable) entity).getOwnerUUID(), player.getUUID())) {
            OverlordConstants.getLogger().warn("Save AI packet received with wrong player ID, expected {} and got {}.", ((Ownable) entity).getOwnerUUID(), player.getUUID());
            return;
        }

        if (aiTag != null) {
            ((OrderableEntity) entity).updateAISettings(aiTag);
        } else {
            OverlordConstants.getLogger().error("No settings found in packet.");
        }
    }
}
