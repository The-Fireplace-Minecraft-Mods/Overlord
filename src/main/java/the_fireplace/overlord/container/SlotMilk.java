package the_fireplace.overlord.container;

import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import the_fireplace.overlord.Overlord;

/**
 * @author The_Fireplace
 */
public class SlotMilk extends Slot {
    IInventory inv;

    public SlotMilk(IInventory inventoryIn, int index, int xPosition,
                    int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
        inv = inventoryIn;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return stack.getItem().equals(Items.MILK_BUCKET) || stack.getItem().equals(Overlord.milk_bottle);
    }
}
