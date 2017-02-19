package the_fireplace.overlord.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.entity.EntityArmyMember;

/**
 * @author The_Fireplace
 */
public class SetSquadMessage implements IMessage {

    public int warrior;
    public String squad;

    public SetSquadMessage() {
    }

    public SetSquadMessage(EntityArmyMember skeleton, String squad){
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
            if(player.world.getEntityByID(message.warrior) != null){
                if(player.world.getEntityByID(message.warrior) instanceof EntityArmyMember){
                    ((EntityArmyMember) player.world.getEntityByID(message.warrior)).setSquad(message.squad);
                }else{
                    Overlord.logError("Entity is not an Army Member. It is "+player.world.getEntityByID(message.warrior).toString());
                }
            }else{
                Overlord.logError("Error 404: Army Member not found: "+message.warrior);
            }
            return null;
        }
    }
}
