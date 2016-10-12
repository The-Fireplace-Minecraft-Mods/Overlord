package the_fireplace.overlord.container;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.tileentity.TileEntitySkeletonMaker;

/**
 * @author The_Fireplace
 */
public class SlotSkinsuit extends Slot {
    TileEntitySkeletonMaker inv;

    public SlotSkinsuit(TileEntitySkeletonMaker inventoryIn, int index, int xPosition,
                        int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
        inv = inventoryIn;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return stack.getItem().equals(Overlord.skinsuit);
    }
}
