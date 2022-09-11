package dev.the_fireplace.overlord.network.server.receiver;

import dev.the_fireplace.lib.api.network.interfaces.ServerboundPacketReceiver;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.domain.entity.Ownable;
import dev.the_fireplace.overlord.entity.ArmyEntity;
import dev.the_fireplace.overlord.item.OrdersWandItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import javax.inject.Singleton;
import java.util.Objects;
import java.util.UUID;

@Singleton
public final class SetSquadPacketReceiver implements ServerboundPacketReceiver
{
    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf) {
        UUID squadId = buf.readUUID();
        int entityId = buf.readInt();
        boolean isWandSquad = entityId == -1;
        if (isWandSquad) {
            setWandSquad(player, squadId);
        } else {
            setEntitySquad(player, squadId, entityId);
        }
    }

    private void setWandSquad(ServerPlayer player, UUID squadId) {
        ItemStack wandStack = OrdersWandItem.getActiveWand(player);
        if (!wandStack.isEmpty()) {
            wandStack.getOrCreateTag().putUUID("squad", squadId);
        }
    }

    private void setEntitySquad(ServerPlayer player, UUID squadId, int entityId) {
        Entity entity = player.getCommandSenderWorld().getEntity(entityId);
        if (!(entity instanceof ArmyEntity)) {
            OverlordConstants.getLogger().info("Entity is not an army entity: {}", Objects.toString(entity));
            return;
        }

        if (!Objects.equals(((Ownable) entity).getOwnerUUID(), player.getUUID())) {
            OverlordConstants.getLogger().warn("Set Squad packet received with wrong player ID, expected {} and got {}.", ((Ownable) entity).getOwnerUUID(), player.getUUID());
            return;
        }

        ((ArmyEntity) entity).setSquad(squadId);
    }
}
