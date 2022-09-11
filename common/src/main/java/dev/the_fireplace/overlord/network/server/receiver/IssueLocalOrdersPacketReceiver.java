package dev.the_fireplace.overlord.network.server.receiver;

import dev.the_fireplace.lib.api.network.interfaces.ServerboundPacketReceiver;
import dev.the_fireplace.overlord.domain.config.ConfigValues;
import dev.the_fireplace.overlord.entity.ArmyEntity;
import dev.the_fireplace.overlord.entity.ai.aiconfig.AISettings;
import dev.the_fireplace.overlord.item.OrdersWandItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStack;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.UUID;

@Singleton
public final class IssueLocalOrdersPacketReceiver implements ServerboundPacketReceiver
{
    private final ConfigValues configValues;

    @Inject
    public IssueLocalOrdersPacketReceiver(ConfigValues configValues) {
        this.configValues = configValues;
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf) {
        ItemStack wandStack = OrdersWandItem.getActiveWand(player);
        if (wandStack.isEmpty()) {
            return;
        }

        //noinspection ConstantConditions
        UUID squadId = wandStack.hasTag() && wandStack.getTag().contains("squad") ? wandStack.getTag().getUUID("squad") : null;
        CompoundTag aiSettings;
        //noinspection ConstantConditions
        if (wandStack.hasTag() && wandStack.getTag().contains("ai")) {
            aiSettings = wandStack.getTag().getCompound("ai");
        } else {
            aiSettings = new AISettings().toTag();
        }

        Collection<ArmyEntity> nearbyArmyMembers = player.level.getEntitiesOfClass(
            ArmyEntity.class,
            player.getBoundingBox().inflate(configValues.getLocalOrdersDistance()),
            entity -> player.getUUID().equals(entity.getOwnerUUID()) && (squadId == null || entity.getSquad().equals(squadId))
        );

        for (ArmyEntity entity : nearbyArmyMembers) {
            entity.updateAISettings(aiSettings);
        }
    }
}
