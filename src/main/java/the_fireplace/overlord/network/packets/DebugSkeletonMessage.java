package the_fireplace.overlord.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.entity.EntitySkeletonWarrior;

/**
 * @author The_Fireplace
 */
public class DebugSkeletonMessage implements IMessage {

    public BlockPos pos;

    public DebugSkeletonMessage() {
    }

    public DebugSkeletonMessage(BlockPos pos){
        this.pos=pos;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = new BlockPos(buf.readInt(), buf.readShort(), buf.readInt());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(pos.getX());
        buf.writeShort(pos.getY());
        buf.writeInt(pos.getZ());
    }

    public static class Handler extends AbstractServerMessageHandler<DebugSkeletonMessage> {
        @Override
        public IMessage handleServerMessage(EntityPlayer player, DebugSkeletonMessage message, MessageContext ctx) {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
                EntitySkeletonWarrior skeletonWarrior = new EntitySkeletonWarrior(player.worldObj, null);
                skeletonWarrior.setLocationAndAngles(message.pos.getX(), message.pos.getY(), message.pos.getZ(), 1, 0);
                skeletonWarrior.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Items.CHAINMAIL_HELMET));
                skeletonWarrior.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(Items.CHAINMAIL_CHESTPLATE));
                skeletonWarrior.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(Items.CHAINMAIL_LEGGINGS));
                skeletonWarrior.setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(Items.CHAINMAIL_BOOTS));
                skeletonWarrior.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
                skeletonWarrior.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, Overlord.shieldStack());

                player.worldObj.spawnEntityInWorld(skeletonWarrior);
            });
            return null;
        }
    }
}