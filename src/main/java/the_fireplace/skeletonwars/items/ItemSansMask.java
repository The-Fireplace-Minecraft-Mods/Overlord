package the_fireplace.skeletonwars.items;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;

/**
 * @author The_Fireplace
 */
public class ItemSansMask extends ItemArmor {
    public ItemSansMask(ArmorMaterial materialIn) {
        super(materialIn, -1, EntityEquipmentSlot.HEAD);
    }
}
