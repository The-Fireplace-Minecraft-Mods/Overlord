package the_fireplace.overlord.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import the_fireplace.overlord.entity.EntityArmyMember;

/**
 * @author The_Fireplace
 */
public class RequestAugmentMessage implements IMessage {

    public int warrior;

    public RequestAugmentMessage() {
    }

    public RequestAugmentMessage(EntityArmyMember skeleton){
        this.warrior = skeleton.hashCode();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        warrior = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(warrior);
    }

    public static class Handler extends AbstractServerMessageHandler<RequestAugmentMessage> {
        @Override
        public IMessage handleServerMessage(EntityPlayer player, RequestAugmentMessage message, MessageContext ctx) {
            String augmentID = "";
            if(player.worldObj.getEntityByID(message.warrior) != null){
                if(player.worldObj.getEntityByID(message.warrior) instanceof EntityArmyMember){
                    augmentID = ((EntityArmyMember) player.worldObj.getEntityByID(message.warrior)).getAugment().augmentId();
                }else{
                    System.out.println("Error: Entity is not an army member. It is "+player.worldObj.getEntityByID(message.warrior).toString());
                }
            }else{
                System.out.println("Error 404: Army Member not found: "+message.warrior);
            }
            return new SetAugmentMessage(message.warrior, augmentID);
        }
    }
}
