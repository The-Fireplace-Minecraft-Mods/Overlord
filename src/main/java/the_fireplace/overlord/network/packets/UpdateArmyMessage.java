package the_fireplace.overlord.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import the_fireplace.overlord.entity.EntitySkeletonWarrior;

/**
 * @author The_Fireplace
 */
public class UpdateArmyMessage implements IMessage {

    public int buttonId;

    public UpdateArmyMessage() {
    }

    public UpdateArmyMessage(int buttonId){
        this.buttonId = buttonId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        buttonId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(buttonId);
    }

    public static class Handler extends AbstractServerMessageHandler<UpdateArmyMessage> {
        @Override
        public IMessage handleServerMessage(EntityPlayer player, UpdateArmyMessage message, MessageContext ctx) {
            for(EntitySkeletonWarrior skeleton:player.worldObj.getEntities(EntitySkeletonWarrior.class, x -> true)){
                if(skeleton.getOwnerId().equals(player.getUniqueID())){
                    if(message.buttonId < 3){
                        skeleton.setAttackMode((byte)message.buttonId);
                    }else{
                        skeleton.setMovementMode((byte)(message.buttonId-3));
                    }
                }
            }
            return null;
        }
    }
}
