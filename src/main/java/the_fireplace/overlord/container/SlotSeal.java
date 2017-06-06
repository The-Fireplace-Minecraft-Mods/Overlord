package the_fireplace.overlord.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import the_fireplace.overlord.items.ItemOverlordsSeal;

/**
 * @author The_Fireplace
 */
public class SlotSeal extends Slot {
	IInventory inv;

	public SlotSeal(IInventory inventoryIn, int index, int xPosition,
	                int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
		inv = inventoryIn;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return stack.getItem() instanceof ItemOverlordsSeal;
	}
}
