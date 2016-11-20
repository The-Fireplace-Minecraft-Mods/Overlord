package the_fireplace.overlord.compat.jei;

import mezz.jei.api.*;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import net.minecraft.item.ItemStack;
import the_fireplace.overlord.Overlord;

import javax.annotation.Nonnull;

/**
 * @author The_Fireplace
 */
@JEIPlugin
public class OverlordJEIPlugin implements IModPlugin {
    @Override
    public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {

    }

    @Override
    public void registerIngredients(IModIngredientRegistration registry) {

    }

    @Override
    public void register(@Nonnull IModRegistry registry) {
        registry.getJeiHelpers().getItemBlacklist().addItemToBlacklist(new ItemStack(Overlord.sans_mask));
    }

    @Override
    public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {

    }
}
