package the_fireplace.overlord.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import the_fireplace.overlord.entity.EntityArmyMember;

/**
 * @author The_Fireplace
 */
public class SetAugmentMessage implements IMessage {

	public int skeleton;
	public String augment;
	public ItemStack stack;

	public SetAugmentMessage() {
	}

	public SetAugmentMessage(int skeleton, String augment, ItemStack augmentDisplayStack) {
		this.skeleton = skeleton;
		this.augment = augment;
		this.stack = augmentDisplayStack;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		skeleton = buf.readInt();
		augment = ByteBufUtils.readUTF8String(buf);
		stack = ByteBufUtils.readItemStack(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(skeleton);
		ByteBufUtils.writeUTF8String(buf, augment);
		ByteBufUtils.writeItemStack(buf, stack);
	}

	public static class Handler extends AbstractClientMessageHandler<SetAugmentMessage> {
		@Override
		public IMessage handleClientMessage(EntityPlayer player, SetAugmentMessage message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(() -> {
				if (player.world.getEntityByID(message.skeleton) != null && player.world.getEntityByID(message.skeleton) instanceof EntityArmyMember) {
					((EntityArmyMember) player.world.getEntityByID(message.skeleton)).setClientAugment(message.augment);
					((EntityArmyMember) player.world.getEntityByID(message.skeleton)).setAugmentDisplayStack(message.stack);
				}
			});
			return null;
		}
	}
}