package the_fireplace.overlord.handlers;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.entity.EntityArmyMember;
import the_fireplace.overlord.entity.EntityBabySkeleton;
import the_fireplace.overlord.entity.EntityConvertedSkeleton;
import the_fireplace.overlord.entity.EntitySkeletonWarrior;

/**
 * @author The_Fireplace
 */
@MethodsReturnNonnullByDefault
public class DispenseBehaviorKeychain extends BehaviorDefaultDispenseItem {
    @Override
    public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
    {
        EnumFacing enumfacing = source.getBlockState().getValue(BlockDispenser.FACING);
        double d0 = source.getX() + (double)enumfacing.getFrontOffsetX();
        double d1 = (double)((float)(source.getBlockPos().getY() + enumfacing.getFrontOffsetY()) + 0.2F);
        double d2 = source.getZ() + (double)enumfacing.getFrontOffsetZ();

        EntityArmyMember entity;
        NBTTagCompound entNbt = stack.getTagCompound();
        if (entNbt != null) {
            if(entNbt.getString("SkeletonType").equals("skeleton_warrior"))
                entity = new EntitySkeletonWarrior(source.getWorld());
            else if(entNbt.getString("SkeletonType").equals("skeleton_converted"))
                entity = new EntityConvertedSkeleton(source.getWorld());
            else {
                entity = new EntityBabySkeleton(source.getWorld());
                if(!entNbt.getString("SkeletonType").equals("skeleton_baby"))
                    Overlord.logError("Skeleton Type for keychain was "+entNbt.getString("SkeletonType"));
            }
            entity.readFromNBT(entNbt);

            entity.setLocationAndAngles(d0+0.5D, d1, d2+0.5D, MathHelper.wrapDegrees(source.getWorld().rand.nextFloat() * 360.0F), 0.0F);
            entity.rotationYawHead = entity.rotationYaw;
            entity.renderYawOffset = entity.rotationYaw;
            source.getWorld().spawnEntity(entity);
            entity.playLivingSound();

            entity.setLocationAndAngles(d0+0.5D, d1, d2+0.5D, MathHelper.wrapDegrees(source.getWorld().rand.nextFloat() * 360.0F), 0.0F);

            stack.stackSize--;
            if(stack.stackSize <= 0)
                return new ItemStack(Overlord.keychain);
        }
        return stack;
    }
}
