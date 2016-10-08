package the_fireplace.overlord.compat.jei;

import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.item.ItemStack;
import the_fireplace.overlord.Overlord;

import javax.annotation.Nonnull;

/**
 * @author The_Fireplace
 */
@JEIPlugin
public class OverlordJEIPlugin implements IModPlugin {
    @Override
    public void register(@Nonnull IModRegistry registry) {
        registry.getJeiHelpers().getItemBlacklist().addItemToBlacklist(new ItemStack(Overlord.sans_mask));
    }

    @Override
    public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {

    }
}
