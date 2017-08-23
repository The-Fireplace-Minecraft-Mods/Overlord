package the_fireplace.overlord.items;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.entity.EntityArmyMember;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemRallyingHorn extends Item {
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		if (!worldIn.isRemote) {
			for (EntityArmyMember entity : worldIn.getEntities(EntityArmyMember.class, input -> input != null && input.getOwner() != null && input.getOwner() == playerIn))
				entity.setLocationAndAngles(playerIn.posX - 1 + worldIn.rand.nextFloat() * 2, playerIn.posY, playerIn.posZ - 1 + worldIn.rand.nextFloat() * 2, worldIn.rand.nextFloat(), worldIn.rand.nextFloat());
			worldIn.playSound(null, playerIn.getPosition(), Overlord.HORN_SOUND, SoundCategory.PLAYERS, 1.0f, 0.5f + itemRand.nextFloat());
		}
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

	@Override
	public int getItemBurnTime(ItemStack stack) {
		return 360;
	}
}
