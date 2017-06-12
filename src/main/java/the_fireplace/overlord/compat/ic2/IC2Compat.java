package the_fireplace.overlord.compat.ic2;

import ic2.api.item.IC2Items;
import ic2.core.ref.ItemName;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import the_fireplace.overlord.augments.AugmentOverclock;
import the_fireplace.overlord.compat.ICompat;
import the_fireplace.overlord.registry.AugmentRegistry;

public class IC2Compat implements ICompat {
	@Override
	public void preInit(FMLPreInitializationEvent event) {

	}

	@Override
	public void init(FMLInitializationEvent event) {
		AugmentRegistry.registerAugment(IC2Items.getItem(ItemName.upgrade.name(), "overclocker"), new AugmentOverclock());
	}
}
