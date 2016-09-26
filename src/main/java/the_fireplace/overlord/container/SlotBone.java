package the_fireplace.overlord.container;

import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import the_fireplace.overlord.tileentity.TileEntitySkeletonMaker;

/**
 * @author The_Fireplace
 */
public class SlotBone extends Slot {
    TileEntitySkeletonMaker inv;

    public SlotBone(TileEntitySkeletonMaker inventoryIn, int index, int xPosition,
                         int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
        inv = inventoryIn;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return stack.getItem().equals(Items.BONE);
    }
}
