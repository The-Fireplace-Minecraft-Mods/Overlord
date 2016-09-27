package the_fireplace.overlord.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import the_fireplace.overlord.entity.EntitySkeletonWarrior;

/**
 * @author The_Fireplace
 */
public class UpdateAttackModeMessage implements IMessage {
    public int warrior;
    public byte mode;

    public UpdateAttackModeMessage() {
    }

    public UpdateAttackModeMessage(int skeleton, byte mode){
        this.warrior = skeleton;
        this.mode=mode;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        warrior = buf.readInt();
        mode = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(warrior);
        buf.writeByte(mode);
    }

    public static class Handler extends AbstractClientMessageHandler<UpdateAttackModeMessage> {
        @Override
        public IMessage handleClientMessage(EntityPlayer player, UpdateAttackModeMessage message, MessageContext ctx) {
            if(player.worldObj.getEntityByID(message.warrior) != null){
                if(player.worldObj.getEntityByID(message.warrior) instanceof EntitySkeletonWarrior){
                    ((EntitySkeletonWarrior) player.worldObj.getEntityByID(message.warrior)).setAttackMode(message.mode);
                }else{
                    System.out.println("Error: Entity is not a Skeleton Warrior. It is "+player.worldObj.getEntityByID(message.warrior).toString());
                }
            }else{
                System.out.println("Error 404: Skeleton Warrior not found: "+message.warrior);
            }
            return null;
        }
    }
}
