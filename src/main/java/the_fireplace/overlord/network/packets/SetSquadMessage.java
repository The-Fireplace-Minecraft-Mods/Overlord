package the_fireplace.overlord.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import the_fireplace.overlord.entity.EntitySkeletonWarrior;

/**
 * @author The_Fireplace
 */
public class SetSquadMessage implements IMessage {

    public int warrior;
    public String squad;

    public SetSquadMessage() {
    }

    public SetSquadMessage(EntitySkeletonWarrior skeleton, String squad){
        this.warrior = skeleton.hashCode();
        this.squad = squad;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        warrior = buf.readInt();
        squad = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(warrior);
        ByteBufUtils.writeUTF8String(buf, squad);
    }

    public static class Handler extends AbstractServerMessageHandler<SetSquadMessage> {
        @Override
        public IMessage handleServerMessage(EntityPlayer player, SetSquadMessage message, MessageContext ctx) {
            if(player.worldObj.getEntityByID(message.warrior) != null){
                if(player.worldObj.getEntityByID(message.warrior) instanceof EntitySkeletonWarrior){
                    ((EntitySkeletonWarrior) player.worldObj.getEntityByID(message.warrior)).setSquad(message.squad);
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
