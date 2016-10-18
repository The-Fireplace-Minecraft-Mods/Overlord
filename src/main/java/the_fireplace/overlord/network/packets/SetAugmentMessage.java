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
public class SetAugmentMessage implements IMessage {

    public int skeleton;
    public String augment;

    public SetAugmentMessage() {
    }

    public SetAugmentMessage(int skeleton, String augment){
        this.skeleton=skeleton;
        this.augment=augment;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        skeleton = buf.readInt();
        augment = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(skeleton);
        ByteBufUtils.writeUTF8String(buf, augment);
    }

    public static class Handler extends AbstractClientMessageHandler<SetAugmentMessage> {
        @Override
        public IMessage handleClientMessage(EntityPlayer player, SetAugmentMessage message, MessageContext ctx) {
            if(player.worldObj.getEntityByID(message.skeleton) != null && player.worldObj.getEntityByID(message.skeleton) instanceof EntitySkeletonWarrior)
                ((EntitySkeletonWarrior)player.worldObj.getEntityByID(message.skeleton)).setAugment(message.augment);
            return null;
        }
    }
}