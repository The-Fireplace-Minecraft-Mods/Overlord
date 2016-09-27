package the_fireplace.overlord;

import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import the_fireplace.overlord.entity.EntitySkeletonWarrior;
import the_fireplace.overlord.network.PacketDispatcher;
import the_fireplace.overlord.network.packets.DebugSkeletonMessage;

import java.util.Random;

/**
 * @author The_Fireplace
 */
public class CommonEvents {
    @SubscribeEvent
    public void rightClickEntity(PlayerInteractEvent.EntityInteract event){
        if(event.getTarget() instanceof EntitySkeleton)
            if(((EntitySkeleton) event.getTarget()).getHealth() < ((EntitySkeleton) event.getTarget()).getMaxHealth())
                if(event.getItemStack() != null)
                    if(event.getItemStack().getItem() == Items.MILK_BUCKET){
                        ((EntitySkeleton) event.getTarget()).heal(1);
                        event.getEntityPlayer().setItemStackToSlot(event.getHand() == EnumHand.MAIN_HAND ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND, new ItemStack(Items.BUCKET));
                    }
    }
    @SubscribeEvent
    public void entityTick(LivingEvent.LivingUpdateEvent event){
        if(!event.getEntityLiving().worldObj.isRemote){
            if(event.getEntityLiving() instanceof EntitySkeleton || event.getEntityLiving() instanceof EntitySkeletonWarrior){
                if(event.getEntityLiving().ticksExisted < 5){
                    if(event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.HEAD) == null){
                        Random random = new Random();
                        if(random.nextInt(1000) == 0)
                            event.getEntityLiving().setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Overlord.sans_mask));
                    }
                }
            }
        }
    }
    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickBlock event){
        PacketDispatcher.sendToServer(new DebugSkeletonMessage(event.getEntityLiving().getPosition()));
        //TODO: Remove before release, this is just here because I am lazy when debugging.
    }
}
