package the_fireplace.overlord.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import the_fireplace.overlord.config.ConfigValues;

/**
 * @author The_Fireplace
 */
public class SetConfigsMessage implements IMessage {

    public byte warrior;
    public byte baby;

    public SetConfigsMessage() {
    }

    public SetConfigsMessage(byte warrior, byte baby){
        this.warrior=warrior;
        this.baby=baby;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        warrior = buf.readByte();
        baby = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(warrior);
        buf.writeByte(baby);
    }

    public static class Handler extends AbstractClientMessageHandler<SetConfigsMessage> {
        @Override
        public IMessage handleClientMessage(EntityPlayer player, SetConfigsMessage message, MessageContext ctx) {
            ConfigValues.SERVER_BONEREQ_WARRIOR=message.warrior;
            ConfigValues.SERVER_BONEREQ_BABY=message.baby;
            return null;
        }
    }
}