package the_fireplace.overlord.compat.nei;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.api.NEIPlugin;
import net.minecraft.item.ItemStack;
import the_fireplace.overlord.Overlord;

@NEIPlugin
public class OverlordNEIPlugin implements IConfigureNEI {
	@Override
	public void loadConfig() {
		API.hideItem(new ItemStack(Overlord.sans_mask));
	}

	@Override
	public String getName() {
		return Overlord.MODNAME;
	}

	@Override
	public String getVersion() {
		return "${version}";
	}
}
