package the_fireplace.overlord.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import the_fireplace.overlord.entity.EntitySkeletonWarrior;

/**
 * @author The_Fireplace
 */
public class AttackModeMessage implements IMessage {

    public int warrior;

    public AttackModeMessage() {
    }

    public AttackModeMessage(EntitySkeletonWarrior skeleton){
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

    public static class Handler extends AbstractServerMessageHandler<AttackModeMessage> {
        @Override
        public IMessage handleServerMessage(EntityPlayer player, AttackModeMessage message, MessageContext ctx) {
            if(player.worldObj.getEntityByID(message.warrior) != null){
                if(player.worldObj.getEntityByID(message.warrior) instanceof EntitySkeletonWarrior){
                    ((EntitySkeletonWarrior) player.worldObj.getEntityByID(message.warrior)).cycleAttackMode();
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
