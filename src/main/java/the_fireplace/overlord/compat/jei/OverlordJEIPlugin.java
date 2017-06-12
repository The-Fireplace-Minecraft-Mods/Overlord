package the_fireplace.overlord.compat.jei;

import mezz.jei.api.*;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;
import the_fireplace.overlord.Overlord;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author The_Fireplace
 */
@JEIPlugin
@ParametersAreNonnullByDefault
public class OverlordJEIPlugin implements IModPlugin {
	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {

	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {

	}

	@Override
	public void registerIngredients(IModIngredientRegistration registry) {

	}

	@Override
	public void register(IModRegistry registry) {
		registry.getJeiHelpers().getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(Overlord.sans_mask));
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {

	}
}
