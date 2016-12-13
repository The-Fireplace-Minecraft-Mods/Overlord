package the_fireplace.overlord.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.entity.projectile.EntityMilkBottle;

import javax.annotation.Nonnull;

/**
 * @author The_Fireplace
 */
public class ItemMilkBottle extends Item {
    public ItemMilkBottle() {
        setCreativeTab(Overlord.tabOverlord);
        setUnlocalizedName("milk_bottle");
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    @Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World worldIn, EntityPlayer playerIn, @Nonnull EnumHand hand) {
        if (!playerIn.capabilities.isCreativeMode && playerIn.getHeldItem(hand) != null) {
            playerIn.getHeldItem(hand).stackSize--;
        }

        worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

        if (!worldIn.isRemote) {
            EntityMilkBottle bottle = new EntityMilkBottle(worldIn, playerIn);
            bottle.setHeadingFromThrower(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
            worldIn.spawnEntity(bottle);
        }

        return new ActionResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(hand));
    }
}