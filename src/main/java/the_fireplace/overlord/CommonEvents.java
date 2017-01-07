package the_fireplace.overlord;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntitySkeletonHorse;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import the_fireplace.overlord.entity.*;

import java.util.Random;

/**
 * @author The_Fireplace
 */
public class CommonEvents {
    @SubscribeEvent
    public void rightClickEntity(PlayerInteractEvent.EntityInteract event){
        if(event.getTarget() instanceof EntitySkeleton || ((event.getTarget() instanceof EntitySkeletonWarrior || event.getTarget() instanceof EntityBabySkeleton || event.getTarget() instanceof EntitySkeletonHorse) && event.getEntityPlayer().isSneaking())) {
            if (((EntityLivingBase) event.getTarget()).getHealth() < ((EntityLivingBase) event.getTarget()).getMaxHealth())
                if (!event.getItemStack().isEmpty())
                    if (event.getItemStack().getItem() == Items.MILK_BUCKET) {
                        ((EntityLivingBase) event.getTarget()).heal(1);
                        if (!event.getEntityPlayer().isCreative()) {
                            if (event.getItemStack().getCount() > 1)
                                event.getItemStack().shrink(1);
                            else
                                event.getEntityPlayer().setItemStackToSlot(event.getHand() == EnumHand.MAIN_HAND ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND, ItemStack.EMPTY);
                            event.getEntityPlayer().inventory.addItemStackToInventory(new ItemStack(Items.BUCKET));
                        }
                        if (event.getTarget() instanceof EntitySkeletonWarrior)
                            ((EntitySkeletonWarrior) event.getTarget()).increaseMilkLevel(false);
                    }
            if(event.getTarget() instanceof EntitySkeleton && ((EntitySkeleton) event.getTarget()).isPotionActive(MobEffects.WEAKNESS) && !(event.getTarget() instanceof EntityCuringSkeleton) && event.getItemStack().getItem() == Items.GOLDEN_APPLE && event.getItemStack().getMetadata() == 0){
                if (!event.getEntityPlayer().capabilities.isCreativeMode)
                {
                    event.getItemStack().shrink(1);
                }

                if (!event.getWorld().isRemote)
                {
                    EntitySkeleton oldSkelly = (EntitySkeleton)event.getTarget();
                    EntityCuringSkeleton newSkelly = new EntityCuringSkeleton(event.getWorld(), event.getEntityPlayer() != null ? event.getEntityPlayer().getUniqueID() : null);
                    newSkelly.copyLocationAndAnglesFrom(oldSkelly);
                    newSkelly.setHeldItem(EnumHand.MAIN_HAND, oldSkelly.getHeldItemMainhand());
                    newSkelly.setHeldItem(EnumHand.OFF_HAND, oldSkelly.getHeldItemOffhand());
                    event.getWorld().removeEntity(oldSkelly);
                    event.getWorld().spawnEntity(newSkelly);
                    newSkelly.startConverting(event.getWorld().rand.nextInt(4802) + 3600);
                }
            }
        }else if(event.getTarget() instanceof EntityCow){
            if(!event.getItemStack().isEmpty())
                if(event.getItemStack().getItem() == Items.GLASS_BOTTLE){
                    if (!event.getEntityPlayer().isCreative()) {
                        if (event.getItemStack().getCount() > 1)
                            event.getItemStack().shrink(1);
                        else
                            event.getEntityPlayer().setItemStackToSlot(event.getHand() == EnumHand.MAIN_HAND ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND, ItemStack.EMPTY);
                    }
                    event.getEntityPlayer().inventory.addItemStackToInventory(new ItemStack(Overlord.milk_bottle));
                }
        }
    }
    @SubscribeEvent
    public void entityTick(LivingEvent.LivingUpdateEvent event){
        if(!event.getEntityLiving().world.isRemote){
            if(event.getEntityLiving() instanceof EntitySkeleton || event.getEntityLiving() instanceof EntitySkeletonWarrior || event.getEntityLiving() instanceof EntityBabySkeleton || event.getEntityLiving() instanceof EntityConvertedSkeleton){
                if(event.getEntityLiving().ticksExisted < 5){
                    if(event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.HEAD).isEmpty()){
                        Random random = new Random();
                        if(random.nextInt(1200) == 0)
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
        if(!event.getEntity().world.isRemote)
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
    @SubscribeEvent
    public void livingDeath(LivingDeathEvent event){
        if(!event.getEntityLiving().world.isRemote){
            if(event.getSource().getEntity() instanceof EntityWolf && event.getEntityLiving() instanceof EntityArmyMember){
                if(((EntityWolf) event.getSource().getEntity()).getOwnerId() != null){
                    EntityPlayer wolfOwner = ((EntityArmyMember) event.getEntityLiving()).world.getPlayerEntityByUUID(((EntityWolf) event.getSource().getEntity()).getOwnerId());
                    if(wolfOwner != null){
                        if(wolfOwner instanceof EntityPlayerMP)
                            if(((EntityPlayerMP) wolfOwner).getStatFile().canUnlockAchievement(Overlord.wardog)) {
                                wolfOwner.addStat(Overlord.wardog);
                            }
                    }
                }
            }
        }
    }
}
