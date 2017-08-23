package the_fireplace.overlord.compat;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public interface ICompat {
	void preInit(FMLPreInitializationEvent event);

	void init(FMLInitializationEvent event);
}
