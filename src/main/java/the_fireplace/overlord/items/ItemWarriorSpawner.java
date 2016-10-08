package the_fireplace.overlord.items;

import net.minecraft.block.BlockFence;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import the_fireplace.overlord.entity.EntitySkeletonWarrior;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author The_Fireplace
 */
public class ItemWarriorSpawner extends Item {
    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
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

            EntitySkeletonWarrior entity = spawnCreature(worldIn, (double)pos.getX() + 0.5D, (double)pos.getY() + offsetY, (double)pos.getZ() + 0.5D);

            if (entity != null)
            {
                if (stack.hasDisplayName())
                {
                    entity.setCustomNameTag(stack.getDisplayName());
                }

                applyItemEntityDataToEntity(worldIn, playerIn, stack, entity);

                entity.setLocationAndAngles((double)pos.getX() + 0.5D, (double)pos.getY() + offsetY, (double)pos.getZ() + 0.5D, MathHelper.wrapDegrees(worldIn.rand.nextFloat() * 360.0F), 0.0F);

                if (!playerIn.capabilities.isCreativeMode)
                {
                    --stack.stackSize;
                }
            }

            return EnumActionResult.SUCCESS;
        }
    }

    /**
     * Applies the data in the EntityTag tag of the given ItemStack to the given Entity.
     */
    public static void applyItemEntityDataToEntity(World entityWorld, @Nullable EntityPlayer player, ItemStack stack, @Nullable Entity targetEntity)
    {
        MinecraftServer minecraftserver = entityWorld.getMinecraftServer();

        if (minecraftserver != null && targetEntity != null)
        {
            NBTTagCompound nbttagcompound = stack.getTagCompound();

            if (nbttagcompound != null)
            {
                if (!entityWorld.isRemote && targetEntity.ignoreItemEntityData() && (player == null || !minecraftserver.getPlayerList().canSendCommands(player.getGameProfile())))
                {
                    return;
                }

                NBTTagCompound nbttagcompound1 = targetEntity.writeToNBT(new NBTTagCompound());
                UUID uuid = targetEntity.getUniqueID();
                nbttagcompound1.merge(nbttagcompound);
                targetEntity.setUniqueId(uuid);
                targetEntity.readFromNBT(nbttagcompound1);
            }
        }
    }

    public static EntitySkeletonWarrior spawnCreature(World worldIn, double x, double y, double z)
    {
        EntitySkeletonWarrior entity = new EntitySkeletonWarrior(worldIn);

        entity.setLocationAndAngles(x, y, z, MathHelper.wrapDegrees(worldIn.rand.nextFloat() * 360.0F), 0.0F);
        entity.rotationYawHead = entity.rotationYaw;
        entity.renderYawOffset = entity.rotationYaw;
        entity.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(entity)), null);
        worldIn.spawnEntityInWorld(entity);
        entity.playLivingSound();

        return entity;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack)
    {
        return stack.getTagCompound() != null;
    }
}
