package dev.the_fireplace.overlord.network.server.receiver;

import dev.the_fireplace.lib.api.network.interfaces.ServerPacketReceiver;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.domain.data.Squads;
import dev.the_fireplace.overlord.domain.entity.Ownable;
import dev.the_fireplace.overlord.entity.ArmyEntity;
import dev.the_fireplace.overlord.item.OrdersWandItem;
import dev.the_fireplace.overlord.network.ClientToServerPacketIDs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import javax.inject.Inject;
import java.util.Objects;
import java.util.UUID;

public final class SetSquadPacketReceiver implements ServerPacketReceiver
{
    private final Squads squads;

    @Inject
    public SetSquadPacketReceiver(Squads squads) {
        this.squads = squads;
    }

    @Override
    public Identifier getId() {
        return ClientToServerPacketIDs.SET_SQUAD;
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        UUID squadId = buf.readUuid();
        int entityId = buf.readInt();
        boolean isWandSquad = entityId == -1;
        if (isWandSquad) {
            setWandSquad(player, squadId);
        } else {
            setEntitySquad(player, squadId, entityId);
        }
    }

    private void setWandSquad(ServerPlayerEntity player, UUID squadId) {
        ItemStack wandStack = OrdersWandItem.getActiveWand(player);
        if (!wandStack.isEmpty()) {
            wandStack.getOrCreateNbt().putUuid("squad", squadId);
        }
    }

    private void setEntitySquad(ServerPlayerEntity player, UUID squadId, int entityId) {
        Entity entity = player.getEntityWorld().getEntityById(entityId);
        if (!(entity instanceof ArmyEntity)) {
            Overlord.getLogger().info("Entity is not an army entity: {}", Objects.toString(entity));
            return;
        }

        if (!Objects.equals(((Ownable) entity).getOwnerUuid(), player.getUuid())) {
            Overlord.getLogger().warn("Set Squad packet received with wrong player ID, expected {} and got {}.", ((Ownable) entity).getOwnerUuid(), player.getUuid());
            return;
        }

        ((ArmyEntity) entity).setSquad(squadId);
    }
}
