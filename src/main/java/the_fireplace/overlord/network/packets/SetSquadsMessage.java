package the_fireplace.overlord.network.packets;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import the_fireplace.overlord.tools.Squads;

import java.util.ArrayList;

/**
 * @author The_Fireplace
 */
public class SetSquadsMessage implements IMessage {

    public ArrayList<String> squads;

    public SetSquadsMessage() {
    }

    public SetSquadsMessage(ArrayList<String> squadData){
        this.squads=squadData;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        squads = Lists.newArrayList();
        for(int i=0;i<buf.readByte(); i++)
            squads.add(ByteBufUtils.readUTF8String(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(squads.size());
        for(String s:squads)
            ByteBufUtils.writeUTF8String(buf, s);
    }

    public static class Handler extends AbstractClientMessageHandler<SetSquadsMessage> {
        @Override
        public IMessage handleClientMessage(EntityPlayer player, SetSquadsMessage message, MessageContext ctx) {
            Squads.makeClientInstance(player, message.squads);
            return null;
        }
    }
}