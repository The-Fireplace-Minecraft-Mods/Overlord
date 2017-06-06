package the_fireplace.overlord.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * @author The_Fireplace
 */
public class SlotOutput extends Slot {
	IInventory inv;

	public SlotOutput(IInventory inventoryIn, int index, int xPosition,
	                  int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
		inv = inventoryIn;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return false;
	}
}
