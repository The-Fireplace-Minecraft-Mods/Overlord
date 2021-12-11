package dev.the_fireplace.overlord.network.server.receiver;

import dev.the_fireplace.lib.api.network.interfaces.ServerPacketReceiver;
import dev.the_fireplace.overlord.domain.config.ConfigValues;
import dev.the_fireplace.overlord.entity.ArmyEntity;
import dev.the_fireplace.overlord.entity.ai.aiconfig.AISettings;
import dev.the_fireplace.overlord.item.OrdersWandItem;
import dev.the_fireplace.overlord.network.ClientToServerPacketIDs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import javax.inject.Inject;
import java.util.Collection;
import java.util.UUID;

public final class IssueLocalOrdersPacketReceiver implements ServerPacketReceiver
{
    private final ConfigValues configValues;

    @Inject
    public IssueLocalOrdersPacketReceiver(ConfigValues configValues) {
        this.configValues = configValues;
    }

    @Override
    public Identifier getId() {
        return ClientToServerPacketIDs.ISSUE_LOCAL_ORDERS;
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        ItemStack wandStack = OrdersWandItem.getActiveWand(player);
        if (wandStack.isEmpty()) {
            return;
        }

        //noinspection ConstantConditions
        UUID squadId = wandStack.hasTag() && wandStack.getTag().contains("squad") ? wandStack.getTag().getUuid("squad") : null;
        NbtCompound aiSettings;
        //noinspection ConstantConditions
        if (wandStack.hasTag() && wandStack.getTag().contains("ai")) {
            aiSettings = wandStack.getTag().getCompound("ai");
        } else {
            aiSettings = new AISettings().toTag();
        }

        Collection<ArmyEntity> nearbyArmyMembers = player.world.getEntitiesByClass(
            ArmyEntity.class,
            player.getBoundingBox().expand(configValues.getLocalOrdersDistance()),
            entity -> player.getUuid().equals(entity.getOwnerUuid()) && (squadId == null || entity.getSquad().equals(squadId))
        );

        for (ArmyEntity entity : nearbyArmyMembers) {
            entity.updateAISettings(aiSettings);
        }
    }
}
