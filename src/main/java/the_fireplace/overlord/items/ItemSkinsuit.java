package the_fireplace.overlord.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import the_fireplace.overlord.tools.ISkinsuitWearer;
import the_fireplace.overlord.tools.SkinType;

public class ItemSkinsuit extends Item {
	protected SkinType type;

	public ItemSkinsuit(SkinType type) {
		super();
		this.type = type;
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
		if (target instanceof ISkinsuitWearer) {
			ISkinsuitWearer skin = (ISkinsuitWearer) target;
			if (skin.getSkinType().isNone()) {
				skin.setSkinsuit(stack, type);
				if (!playerIn.isCreative())
					stack.shrink(1);
				return true;
			}
		}
		return super.itemInteractionForEntity(stack, playerIn, target, hand);
	}

	public SkinType getType() {
		return type;
	}
}
