package dev.the_fireplace.overlord.item;

import dev.the_fireplace.annotateddi.api.DIContainer;
import dev.the_fireplace.overlord.domain.config.ConfigValues;
import dev.the_fireplace.overlord.network.ServerToClientPacketIDs;
import dev.the_fireplace.overlord.network.server.builder.OpenLocalOrdersGUIBufferBuilder;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class OrdersWandItem extends Item
{
    private final ConfigValues configValues;

    public OrdersWandItem(Settings settings) {
        super(settings);
        this.configValues = DIContainer.get().getInstance(ConfigValues.class);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient() && !user.isSneaking()) {
            return TypedActionResult.success(user.getStackInHand(hand));
        }
        if (!user.isSneaking() && user instanceof ServerPlayerEntity) {
            ServerPlayNetworking.send((ServerPlayerEntity) user, ServerToClientPacketIDs.OPEN_LOCAL_ORDERS_GUI, OpenLocalOrdersGUIBufferBuilder.build(configValues.getLocalOrdersDistance()));
            return TypedActionResult.success(user.getStackInHand(hand));
        }
        return super.use(world, user, hand);
    }

    public static ItemStack getActiveWand(PlayerEntity player) {
        if (player.getMainHandStack().isOf(OverlordItems.ORDERS_WAND)) {
            return player.getMainHandStack();
        }

        if (player.getOffHandStack().isOf(OverlordItems.ORDERS_WAND)) {
            return player.getOffHandStack();
        }

        return ItemStack.EMPTY;
    }
}
