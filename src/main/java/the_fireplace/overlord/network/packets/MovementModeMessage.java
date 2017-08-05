package the_fireplace.overlord.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.entity.EntityArmyMember;

/**
 * @author The_Fireplace
 */
public class MovementModeMessage implements IMessage {

	public int warrior;

	public MovementModeMessage() {
	}

	public MovementModeMessage(EntityArmyMember skeleton) {
		this.warrior = skeleton.getEntityId();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		warrior = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(warrior);
	}

	public static class Handler extends AbstractServerMessageHandler<MovementModeMessage> {
		@Override
		public IMessage handleServerMessage(EntityPlayer player, MovementModeMessage message, MessageContext ctx) {
			FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
				if (player.world.getEntityByID(message.warrior) != null) {
					if (player.world.getEntityByID(message.warrior) instanceof EntityArmyMember)
						((EntityArmyMember) player.world.getEntityByID(message.warrior)).cycleMovementMode();
					else
						Overlord.logError("Entity is not an Army Member. It is " + player.world.getEntityByID(message.warrior).toString());
				} else
					Overlord.logError("Error 404: Army Member not found: " + message.warrior);
			});
			return null;
		}
	}
}
