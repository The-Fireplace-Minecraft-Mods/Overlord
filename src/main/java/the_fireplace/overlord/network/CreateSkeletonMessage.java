package the_fireplace.overlord.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import the_fireplace.overlord.tileentity.TileEntitySkeletonMaker;

/**
 * @author The_Fireplace
 */
public class CreateSkeletonMessage implements IMessage {

    public BlockPos pos;

    public CreateSkeletonMessage() {
    }

    public CreateSkeletonMessage(BlockPos pos){
        this.pos=pos;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = new BlockPos(buf.readInt(), buf.readShort(), buf.readInt());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(pos.getX());
        buf.writeShort(pos.getY());
        buf.writeInt(pos.getZ());
    }

    public static class Handler extends AbstractServerMessageHandler<CreateSkeletonMessage> {
        @Override
        public IMessage handleServerMessage(EntityPlayer player, CreateSkeletonMessage message, MessageContext ctx) {
            if(player.worldObj.getTileEntity(message.pos) instanceof TileEntitySkeletonMaker){
                ((TileEntitySkeletonMaker) player.worldObj.getTileEntity(message.pos)).spawnSkeleton();
            }
            return null;
        }
    }
}