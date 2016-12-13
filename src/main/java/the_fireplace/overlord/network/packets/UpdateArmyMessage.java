package the_fireplace.overlord.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import the_fireplace.overlord.entity.EntityArmyMember;

/**
 * @author The_Fireplace
 */
public class UpdateArmyMessage implements IMessage {

    public int buttonId;
    public String squad;

    public UpdateArmyMessage() {
    }

    public UpdateArmyMessage(int buttonId){
        this("", buttonId);
    }

    public UpdateArmyMessage(String squad, int buttonId){
        this.squad = squad;
        this.buttonId = buttonId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        buttonId = buf.readInt();
        squad = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(buttonId);
        ByteBufUtils.writeUTF8String(buf, squad);
    }

    public static class Handler extends AbstractServerMessageHandler<UpdateArmyMessage> {
        @Override
        public IMessage handleServerMessage(EntityPlayer player, UpdateArmyMessage message, MessageContext ctx) {
            player.worldObj.getEntities(EntityArmyMember.class, x -> true).stream().filter(skeleton -> skeleton.getOwnerId().equals(player.getUniqueID())).forEach(skeleton -> {
                if(message.squad.isEmpty() || skeleton.getSquad().equals(message.squad))
                if (message.buttonId < 3) {
                    skeleton.setAttackMode((byte) message.buttonId);
                } else {
                    skeleton.setMovementMode((byte) (message.buttonId - 3));
                }
            });
            return null;
        }
    }
}
