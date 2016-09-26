package the_fireplace.overlord.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @author The_Fireplace
 */
public class AllyAcceptMessage implements IMessage {

    public String username;

    public AllyAcceptMessage() {
    }

    public AllyAcceptMessage(String username){
        this.username=username;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        username = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, username);
    }

    public static class Handler extends AbstractClientMessageHandler<AllyAcceptMessage> {
        @Override
        public IMessage handleClientMessage(EntityPlayer player, AllyAcceptMessage message, MessageContext ctx) {
            player.addChatMessage(new TextComponentTranslation("overlord.allyaccept", message.username));
            return null;
        }
    }
}
