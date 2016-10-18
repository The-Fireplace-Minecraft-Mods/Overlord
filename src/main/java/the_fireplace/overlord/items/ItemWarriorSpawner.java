package the_fireplace.overlord.items;

import net.minecraft.block.BlockFence;
import net.minecraft.block.state.IBlockState;
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

            EntitySkeletonWarrior entity = new EntitySkeletonWarrior(worldIn);

            entity.setLocationAndAngles(pos.getX()+0.5D, pos.getY() + offsetY, pos.getZ()+0.5D, MathHelper.wrapDegrees(worldIn.rand.nextFloat() * 360.0F), 0.0F);
            entity.rotationYawHead = entity.rotationYaw;
            entity.renderYawOffset = entity.rotationYaw;
            entity.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(entity)), null);
            worldIn.spawnEntityInWorld(entity);
            entity.playLivingSound();

            if (entity != null)
            {
                if (stack.hasDisplayName())
                {
                    entity.setCustomNameTag(stack.getDisplayName());
                }

                applyItemEntityDataToEntity(worldIn, playerIn, stack, entity);

                entity.setLocationAndAngles(pos.getX()+0.5D, pos.getY() + offsetY, pos.getZ()+0.5D, MathHelper.wrapDegrees(worldIn.rand.nextFloat() * 360.0F), 0.0F);

                if (!playerIn.capabilities.isCreativeMode)
                {
                    --stack.stackSize;
                }
            }

            return EnumActionResult.SUCCESS;
        }
    }

    public static void applyItemEntityDataToEntity(World entityWorld, @Nullable EntityPlayer player, ItemStack stack, @Nullable EntitySkeletonWarrior targetEntity)
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

                targetEntity.readFromNBT(nbttagcompound);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack)
    {
        return stack.getTagCompound() != null;
    }
}
