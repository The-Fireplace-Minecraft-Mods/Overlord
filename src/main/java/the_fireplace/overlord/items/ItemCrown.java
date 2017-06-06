package the_fireplace.overlord.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import the_fireplace.overlord.Overlord;

import javax.annotation.Nonnull;

/**
 * @author The_Fireplace
 */
public class ItemCrown extends ItemArmor {
	public ItemCrown(ArmorMaterial materialIn) {
		super(materialIn, -1, EntityEquipmentSlot.HEAD);
		setUnlocalizedName("crown");
	}

	@Override
	@Nonnull
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		return Overlord.MODID + ":textures/armor/crown.png";
	}

	@SideOnly(Side.CLIENT)
	@Override
	public CreativeTabs getCreativeTab() {
		return Overlord.tabOverlord;
	}
}
