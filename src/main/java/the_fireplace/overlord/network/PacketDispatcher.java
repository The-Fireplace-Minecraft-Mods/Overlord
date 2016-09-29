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
public class PacketDispatcher {
    private static byte packetId = 0;

    private static final SimpleNetworkWrapper dispatcher = NetworkRegistry.INSTANCE.newSimpleChannel(Overlord.MODID);

    public static final void registerPackets() {
        PacketDispatcher.registerMessage(CreateSkeletonMessage.Handler.class, CreateSkeletonMessage.class, Side.SERVER);
        PacketDispatcher.registerMessage(SetMilkMessage.Handler.class, SetMilkMessage.class, Side.CLIENT);
        PacketDispatcher.registerMessage(DebugSkeletonMessage.Handler.class, DebugSkeletonMessage.class, Side.SERVER);
        PacketDispatcher.registerMessage(AttackModeMessage.Handler.class, AttackModeMessage.class, Side.SERVER);
        PacketDispatcher.registerMessage(MovementModeMessage.Handler.class, MovementModeMessage.class, Side.SERVER);
        PacketDispatcher.registerMessage(UpdateArmyMessage.Handler.class, UpdateArmyMessage.class, Side.SERVER);
    }

    private static final void registerMessage(Class handlerClass, Class messageClass, Side side) {
        PacketDispatcher.dispatcher.registerMessage(handlerClass, messageClass, packetId++, side);
    }

    //Wrapper methods
    public static final void sendTo(IMessage message, EntityPlayerMP player) {
        PacketDispatcher.dispatcher.sendTo(message, player);
    }

    public static final void sendToAll(IMessage message) {
        PacketDispatcher.dispatcher.sendToAll(message);
    }

    public static final void sendToAllAround(IMessage message, NetworkRegistry.TargetPoint point) {
        PacketDispatcher.dispatcher.sendToAllAround(message, point);
    }

    public static final void sendToAllAround(IMessage message, int dimension, double x, double y, double z, double range) {
        PacketDispatcher.dispatcher.sendToAllAround(message, new NetworkRegistry.TargetPoint(dimension, x, y, z, range));
    }

    public static final void sendToAllAround(IMessage message, EntityPlayer player, double range) {
        PacketDispatcher.dispatcher.sendToAllAround(message, new NetworkRegistry.TargetPoint(player.worldObj.provider.getDimension(), player.posX, player.posY, player.posZ, range));
    }

    public static final void sendToDimension(IMessage message, int dimensionId) {
        PacketDispatcher.dispatcher.sendToDimension(message, dimensionId);
    }

    public static final void sendToServer(IMessage message) {
        PacketDispatcher.dispatcher.sendToServer(message);
    }
}