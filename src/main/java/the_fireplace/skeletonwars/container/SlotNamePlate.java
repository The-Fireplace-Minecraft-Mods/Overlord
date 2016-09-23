package the_fireplace.skeletonwars.container;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import the_fireplace.skeletonwars.SkeletonWars;
import the_fireplace.skeletonwars.tileentity.TileEntitySkeletonMaker;

/**
 * @author The_Fireplace
 */
public class SlotNamePlate extends Slot {
    TileEntitySkeletonMaker inv;

    public SlotNamePlate(TileEntitySkeletonMaker inventoryIn, int index, int xPosition,
                         int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
        inv = inventoryIn;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return stack.getItem().equals(SkeletonWars.name_plate);
    }
}
