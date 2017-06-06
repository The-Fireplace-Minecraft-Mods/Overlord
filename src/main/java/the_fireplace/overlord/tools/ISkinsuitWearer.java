package the_fireplace.overlord.tools;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public interface ISkinsuitWearer {
	/**
	 * Gets the type of skinsuit the entity is wearing
	 *
	 * @return The correct type, or SkinType.NONE if there isn't one.
	 */
	SkinType getSkinType();

	/**
	 * Gets the name of the skinsuit. Used for determining player type's skin, and naming entities which support it.
	 *
	 * @return The name, or an empty string if there isn't one.
	 */
	String getSkinName();

	/**
	 * Applies a skinsuit to the entity. Don't forget to handle removal of a skinsuit if the type is SkinType.NONE
	 *
	 * @param stack
	 * 		The stack to be applied, or ItemStack.EMPTY if there is none
	 * @param type
	 * 		The type of skin, or SkinType.NONE if there is none
	 */
	void setSkinsuit(ItemStack stack, SkinType type);
}
