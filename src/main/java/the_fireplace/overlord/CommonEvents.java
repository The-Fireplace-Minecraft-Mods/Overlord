package the_fireplace.overlord;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import the_fireplace.overlord.entity.EntitySkeletonWarrior;

import java.util.Random;

/**
 * @author The_Fireplace
 */
public class CommonEvents {
    @SubscribeEvent
    public void rightClickEntity(PlayerInteractEvent.EntityInteract event){
        if(event.getTarget() instanceof EntitySkeleton || event.getTarget() instanceof EntitySkeletonWarrior)
            if(((EntityLivingBase) event.getTarget()).getHealth() < ((EntityLivingBase) event.getTarget()).getMaxHealth())
                if(event.getItemStack() != null)
                    if(event.getItemStack().getItem() == Items.MILK_BUCKET){
                        ((EntityLivingBase) event.getTarget()).heal(1);
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
    public void configCahnged(ConfigChangedEvent.OnConfigChangedEvent event){
        if(event.getModID().equals(Overlord.MODID)){
            Overlord.syncConfig();
        }
    }
    @SubscribeEvent
    public void livingHurt(LivingHurtEvent event){
        if(!event.getEntity().worldObj.isRemote)
        if(event.getSource().isProjectile()){
            if(event.getEntityLiving() instanceof EntityPlayerMP){
                if(event.getSource().getEntity() instanceof EntitySkeletonWarrior){
                    if(((EntitySkeletonWarrior) event.getSource().getEntity()).getOwnerId().equals(event.getEntityLiving().getUniqueID())){
                        if(((EntityPlayerMP) event.getEntityLiving()).getStatFile().canUnlockAchievement(Overlord.nmyi))
                            ((EntityPlayerMP) event.getEntityLiving()).addStat(Overlord.nmyi);
                    }
                }
            }
        }
    }
}
