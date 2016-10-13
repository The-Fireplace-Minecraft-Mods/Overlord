package the_fireplace.overlord.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import the_fireplace.overlord.entity.EntitySkeletonWarrior;
import the_fireplace.overlord.tools.Squads;

import java.util.ArrayList;

/**
 * @author The_Fireplace
 */
public class UpdateSquadsMessage implements IMessage {

    public ArrayList<String> names;

    public UpdateSquadsMessage() {
    }

    public UpdateSquadsMessage(ArrayList<String> names){
        this.names=names;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        names = new ArrayList();
        names.add(ByteBufUtils.readUTF8String(buf));
        names.add(ByteBufUtils.readUTF8String(buf));
        names.add(ByteBufUtils.readUTF8String(buf));
        names.add(ByteBufUtils.readUTF8String(buf));
        names.add(ByteBufUtils.readUTF8String(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        for(String name:names)
            ByteBufUtils.writeUTF8String(buf, name);
    }

    public static class Handler extends AbstractServerMessageHandler<UpdateSquadsMessage> {
        @Override
        public IMessage handleServerMessage(EntityPlayer player, UpdateSquadsMessage message, MessageContext ctx) {
            Squads.getInstance().setPlayerSquadNames(player.getUniqueID(), message.names);
            for(EntitySkeletonWarrior entity:player.worldObj.getEntities(EntitySkeletonWarrior.class, x -> true)){
                if(entity.getOwnerId().equals(player.getUniqueID()))
                    entity.onSquadUpdate();
            }
            return null;
        }
    }
}
