package the_fireplace.overlord.items;

import net.minecraft.block.BlockFence;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.entity.EntityArmyMember;
import the_fireplace.overlord.entity.EntityBabySkeleton;
import the_fireplace.overlord.entity.EntityConvertedSkeleton;
import the_fireplace.overlord.entity.EntitySkeletonWarrior;
import the_fireplace.overlord.tools.ArmyUtils;

import javax.annotation.Nonnull;
import java.util.List;

import static the_fireplace.overlord.Overlord.proxy;

/**
 * @author The_Fireplace
 */
public class ItemKeychain extends Item {
    private boolean isOccupied;
    public boolean getIsOccupied(){
        return isOccupied;
    }
    public ItemKeychain(boolean isOccupied) {
        super();
        this.isOccupied = isOccupied;
        setMaxStackSize(1);
        if(!isOccupied)
            setCreativeTab(Overlord.tabOverlord);
    }

    @Override
    @Nonnull
    public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        ItemStack stack = playerIn.getHeldItem(hand);
        if (worldIn.isRemote)
        {
            return EnumActionResult.SUCCESS;
        }
        else if (!playerIn.canPlayerEdit(pos.offset(facing), facing, stack))
        {
            return EnumActionResult.FAIL;
        }
        else
        {
            IBlockState iblockstate = worldIn.getBlockState(pos);

            pos = pos.offset(facing);
            double offsetY = 0.0D;

            if (facing == EnumFacing.UP && iblockstate.getBlock() instanceof BlockFence)
            {
                offsetY = 0.5D;
            }

            EntityArmyMember entity;
            NBTTagCompound entNbt = stack.getTagCompound();
            if(entNbt != null) {
                if (entNbt.getString("SkeletonType").equals("skeleton_warrior"))
                    entity = new EntitySkeletonWarrior(worldIn);
                else if (entNbt.getString("SkeletonType").equals("skeleton_converted"))
                    entity = new EntityConvertedSkeleton(worldIn);
                else {
                    entity = new EntityBabySkeleton(worldIn);
                    if (!entNbt.getString("SkeletonType").equals("skeleton_baby"))
                        Overlord.logError("Skeleton Type for keychain was " + entNbt.getString("SkeletonType"));
                }
                entity.readFromNBT(entNbt);

                entity.setLocationAndAngles(pos.getX() + 0.5D, pos.getY() + offsetY, pos.getZ() + 0.5D, MathHelper.wrapDegrees(worldIn.rand.nextFloat() * 360.0F), 0.0F);
                entity.rotationYawHead = entity.rotationYaw;
                entity.renderYawOffset = entity.rotationYaw;
                worldIn.spawnEntity(entity);
                entity.playLivingSound();

                entity.setLocationAndAngles(pos.getX() + 0.5D, pos.getY() + offsetY, pos.getZ() + 0.5D, MathHelper.wrapDegrees(worldIn.rand.nextFloat() * 360.0F), 0.0F);

                stack.shrink(1);
                if(!playerIn.inventory.addItemStackToInventory(new ItemStack(Overlord.keychain)))
                    playerIn.dropItem(Overlord.keychain, 1);

                return EnumActionResult.SUCCESS;
            }else{
                return EnumActionResult.FAIL;
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
        NBTTagCompound nbt = stack.getTagCompound();
        if(getIsOccupied() && nbt != null) {
            if(nbt.hasKey("SkinsuitName") && nbt.hasKey("HasSkinsuit") && nbt.getBoolean("HasSkinsuit"))
                tooltip.add(proxy.translateToLocal("tooltip.skin")+' '+(nbt.getString("SkinsuitName").isEmpty() ? "Steve" : nbt.getString("SkinsuitName")));
            if(nbt.hasKey("SkeletonType"))
                tooltip.add(proxy.translateToLocal("tooltip.type") + ' ' + proxy.translateToLocal("entity."+nbt.getString("SkeletonType")+".name"));
            if(nbt.hasKey("SkeletonPowerLevel"))
                tooltip.add(proxy.translateToLocal("tooltip.level")+' '+nbt.getInteger("SkeletonPowerLevel"));
            if(nbt.hasKey("CustomName"))
                tooltip.add(proxy.translateToLocal("tooltip.name")+' '+nbt.getString("CustomName"));
            if(nbt.hasKey("AttackMode"))
                tooltip.add(proxy.translateToLocal("tooltip.attack_mode")+' '+ ArmyUtils.getAttackModeString(nbt.getByte("AttackMode")));
            if(nbt.hasKey("MovementMode"))
                tooltip.add(proxy.translateToLocal("tooltip.movement_mode")+' '+ ArmyUtils.getMovementModeString(nbt.getByte("MovementMode")));
            if(nbt.hasKey("Squad") && !nbt.getString("Squad").isEmpty())
                tooltip.add(proxy.translateToLocal("tooltip.squad")+' '+nbt.getString("Squad"));

            NBTTagList armorInv = (NBTTagList) nbt.getTag("SkeletonEquipment");
            if (armorInv != null && armorInv.tagCount() > 0) {
                tooltip.add(proxy.translateToLocal("tooltip.equipment"));
                for (int i = 0; i < armorInv.tagCount(); i++) {
                    NBTTagCompound item = (NBTTagCompound) armorInv.get(i);
                    tooltip.add(new ItemStack(item).getDisplayName());
                }
            }
        }
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand)
    {
        if(!getIsOccupied() && !playerIn.world.isRemote && target instanceof EntityArmyMember && (((EntityArmyMember)target).getOwnerId() == null || ((EntityArmyMember)target).getOwnerId().equals(playerIn.getUniqueID()))){
            if(target instanceof EntityBabySkeleton|| target instanceof EntityConvertedSkeleton || target instanceof EntitySkeletonWarrior){
                ItemStack occupiedItem = new ItemStack(Overlord.keychain_occupied);
                NBTTagCompound entNbt = new NBTTagCompound();
                target.writeToNBT(entNbt);
                String entityString = EntityList.getEntityString(target);
                if(entityString != null)
                    entNbt.setString("SkeletonType", entityString);
                else{
                    Overlord.logError("Entity string for "+target.toString()+" was null.");
                    return false;
                }
                occupiedItem.setTagCompound(entNbt);
                if (playerIn.getHeldItem(hand).getCount() > 1)
                    playerIn.getHeldItem(hand).shrink(1);
                else
                    playerIn.setItemStackToSlot(hand == EnumHand.MAIN_HAND ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND, ItemStack.EMPTY);
                if(!playerIn.inventory.addItemStackToInventory(occupiedItem))
                    playerIn.dropItem(occupiedItem, false);
                target.world.removeEntity(target);
                return true;
            }
        }
        return false;
    }
}
