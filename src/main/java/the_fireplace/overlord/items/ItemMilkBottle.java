package the_fireplace.overlord.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.entity.projectile.EntityMilkBottle;
import the_fireplace.overlord.handlers.MilkBottleWrapper;
import the_fireplace.overlord.registry.MilkRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author The_Fireplace
 */
public class ItemMilkBottle extends Item {
	public ItemMilkBottle() {
		setCreativeTab(Overlord.tabOverlord);
		setTranslationKey("milk_bottle");
	}

	@Override
	@Nonnull
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull EnumHand hand) {
		if (!playerIn.capabilities.isCreativeMode) {
			playerIn.getHeldItem(hand).shrink(1);
		}

		worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

		if (!worldIn.isRemote) {
			EntityMilkBottle bottle = new EntityMilkBottle(worldIn, playerIn);
			bottle.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
			worldIn.spawnEntity(bottle);
		}

		return new ActionResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(hand));
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
		if (MilkRegistry.isMilkRegistered() && this.getClass() == ItemMilkBottle.class)
			return new MilkBottleWrapper(stack);
		else
			return super.initCapabilities(stack, nbt);
	}
}