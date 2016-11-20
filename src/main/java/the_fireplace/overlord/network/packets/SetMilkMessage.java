package the_fireplace.overlord.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import the_fireplace.overlord.tileentity.TileEntitySkeletonMaker;

/**
 * @author The_Fireplace
 */
public class SetMilkMessage implements IMessage {

    public BlockPos pos;
    public byte milk;

    public SetMilkMessage() {
    }

    public SetMilkMessage(BlockPos pos, byte milk){
        this.pos=pos;
        this.milk=milk;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = new BlockPos(buf.readInt(), buf.readShort(), buf.readInt());
        milk = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(pos.getX());
        buf.writeShort(pos.getY());
        buf.writeInt(pos.getZ());
        buf.writeByte(milk);
    }

    public static class Handler extends AbstractClientMessageHandler<SetMilkMessage> {
        @Override
        public IMessage handleClientMessage(EntityPlayer player, SetMilkMessage message, MessageContext ctx) {
            if(player.world.getTileEntity(message.pos) instanceof TileEntitySkeletonMaker){
                ((TileEntitySkeletonMaker) player.world.getTileEntity(message.pos)).setMilk(message.milk);
            }
            return null;
        }
    }
}