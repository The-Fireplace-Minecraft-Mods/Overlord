package the_fireplace.overlord.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import the_fireplace.overlord.Overlord;
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
            ItemStack stack = ItemStack.EMPTY;
            if(player.world.getEntityByID(message.warrior) != null){
                if(player.world.getEntityByID(message.warrior) instanceof EntityArmyMember){
                    if(((EntityArmyMember) player.world.getEntityByID(message.warrior)).getAugment() != null) {
                        augmentID = ((EntityArmyMember) player.world.getEntityByID(message.warrior)).getAugment().augmentId();
                    }
                    stack = ((EntityArmyMember) player.world.getEntityByID(message.warrior)).getAugmentStack();
                }else{
                    Overlord.logError("Entity is not an Army Member. It is "+player.world.getEntityByID(message.warrior).toString());
                }
            }else{
                Overlord.logError("Error 404: Army Member not found: "+message.warrior);
            }
            return new SetAugmentMessage(message.warrior, augmentID, stack);
        }
    }
}
