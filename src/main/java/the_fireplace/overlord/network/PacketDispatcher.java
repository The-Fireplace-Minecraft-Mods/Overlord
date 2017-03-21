package the_fireplace.overlord.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.network.packets.*;

/**
 * @author coolAlias
 * @author The_Fireplace
 */
public final class PacketDispatcher {
    private static byte packetId = 0;

    private static final SimpleNetworkWrapper dispatcher = NetworkRegistry.INSTANCE.newSimpleChannel(Overlord.MODID);

    public static void registerPackets() {
        PacketDispatcher.registerMessage(CreateSkeletonMessage.Handler.class, CreateSkeletonMessage.class, Side.SERVER);
        PacketDispatcher.registerMessage(SetMilkMessage.Handler.class, SetMilkMessage.class, Side.CLIENT);
        PacketDispatcher.registerMessage(DebugSkeletonMessage.Handler.class, DebugSkeletonMessage.class, Side.SERVER);
        PacketDispatcher.registerMessage(AttackModeMessage.Handler.class, AttackModeMessage.class, Side.SERVER);
        PacketDispatcher.registerMessage(MovementModeMessage.Handler.class, MovementModeMessage.class, Side.SERVER);
        PacketDispatcher.registerMessage(UpdateArmyMessage.Handler.class, UpdateArmyMessage.class, Side.SERVER);
        PacketDispatcher.registerMessage(UpdateSquadsMessage.Handler.class, UpdateSquadsMessage.class, Side.SERVER);
        PacketDispatcher.registerMessage(UpdateClientSquadsMessage.Handler.class, UpdateClientSquadsMessage.class, Side.CLIENT);
        PacketDispatcher.registerMessage(SetSquadMessage.Handler.class, SetSquadMessage.class, Side.SERVER);
        PacketDispatcher.registerMessage(SetConfigsMessage.Handler.class, SetConfigsMessage.class, Side.CLIENT);
        PacketDispatcher.registerMessage(RequestAugmentMessage.Handler.class, RequestAugmentMessage.class, Side.SERVER);
        PacketDispatcher.registerMessage(SetAugmentMessage.Handler.class, SetAugmentMessage.class, Side.CLIENT);
        PacketDispatcher.registerMessage(SetSquadsMessage.Handler.class, SetSquadsMessage.class, Side.CLIENT);
    }

    @SuppressWarnings("unchecked")
    private static void registerMessage(Class handlerClass, Class messageClass, Side side) {
        PacketDispatcher.dispatcher.registerMessage(handlerClass, messageClass, packetId++, side);
    }

    //Wrapper methods
    public static void sendTo(IMessage message, EntityPlayerMP player) {
        PacketDispatcher.dispatcher.sendTo(message, player);
    }

    public static void sendToAll(IMessage message) {
        PacketDispatcher.dispatcher.sendToAll(message);
    }

    public static void sendToAllAround(IMessage message, NetworkRegistry.TargetPoint point) {
        PacketDispatcher.dispatcher.sendToAllAround(message, point);
    }

    public static void sendToAllAround(IMessage message, int dimension, double x, double y, double z, double range) {
        PacketDispatcher.dispatcher.sendToAllAround(message, new NetworkRegistry.TargetPoint(dimension, x, y, z, range));
    }

    public static void sendToAllAround(IMessage message, EntityPlayer player, double range) {
        PacketDispatcher.dispatcher.sendToAllAround(message, new NetworkRegistry.TargetPoint(player.world.provider.getDimension(), player.posX, player.posY, player.posZ, range));
    }

    public static void sendToDimension(IMessage message, int dimensionId) {
        PacketDispatcher.dispatcher.sendToDimension(message, dimensionId);
    }

    public static void sendToServer(IMessage message) {
        PacketDispatcher.dispatcher.sendToServer(message);
    }
}