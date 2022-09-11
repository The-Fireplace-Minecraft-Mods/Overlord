package dev.the_fireplace.overlord.item;

import com.google.inject.Injector;
import dev.the_fireplace.lib.api.network.injectables.PacketSender;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.domain.config.ConfigValues;
import dev.the_fireplace.overlord.network.ClientboundPackets;
import dev.the_fireplace.overlord.network.server.builder.OpenLocalOrdersGUIBufferBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class OrdersWandItem extends Item
{
    private final ConfigValues configValues;
    private final PacketSender packetSender;
    private final ClientboundPackets clientboundPackets;
    private final OpenLocalOrdersGUIBufferBuilder openLocalOrdersGUIBufferBuilder;

    public OrdersWandItem(Properties settings) {
        super(settings);
        Injector injector = OverlordConstants.getInjector();
        this.configValues = injector.getInstance(ConfigValues.class);
        this.packetSender = injector.getInstance(PacketSender.class);
        this.clientboundPackets = injector.getInstance(ClientboundPackets.class);
        this.openLocalOrdersGUIBufferBuilder = injector.getInstance(OpenLocalOrdersGUIBufferBuilder.class);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        if (world.isClientSide() && !user.isShiftKeyDown()) {
            return InteractionResultHolder.success(user.getItemInHand(hand));
        }
        if (!user.isShiftKeyDown() && user instanceof ServerPlayer) {
            packetSender.sendToClient(
                ((ServerPlayer) user).connection,
                clientboundPackets.openLocalOrdersScreen(),
                openLocalOrdersGUIBufferBuilder.build(configValues.getLocalOrdersDistance())
            );
            return InteractionResultHolder.success(user.getItemInHand(hand));
        }
        return super.use(world, user, hand);
    }

    public static ItemStack getActiveWand(Player player) {
        OverlordItems overlordItems = OverlordConstants.getInjector().getInstance(OverlordItems.class);
        if (player.getMainHandItem().is(overlordItems.getOrdersWand())) {
            return player.getMainHandItem();
        }

        if (player.getOffhandItem().is(overlordItems.getOrdersWand())) {
            return player.getOffhandItem();
        }

        return ItemStack.EMPTY;
    }
}
