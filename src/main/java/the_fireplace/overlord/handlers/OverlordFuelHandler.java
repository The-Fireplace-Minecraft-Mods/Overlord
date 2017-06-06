package the_fireplace.overlord.handlers;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.IFuelHandler;
import the_fireplace.overlord.Overlord;

public class OverlordFuelHandler implements IFuelHandler {
	@Override
	public int getBurnTime(ItemStack fuel) {
		Item ifuel = fuel.getItem();
		if (ifuel == Overlord.rallying_horn)
			return 360;
		else
			return 0;
	}
}
