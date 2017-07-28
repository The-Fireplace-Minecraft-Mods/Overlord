package the_fireplace.overlord.registry;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.oredict.ShapedOreRecipe;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.items.ItemOverlordsSeal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

/**
 * @author The_Fireplace
 */
public class SealRecipe extends ShapedOreRecipe {
	public SealRecipe(ResourceLocation loc, Block result, Object... recipe) {
		super(loc, result, recipe);
	}

	public SealRecipe(ResourceLocation loc, Item result, Object... recipe) {
		super(loc, result, recipe);
	}

	public SealRecipe(ResourceLocation loc, @Nonnull ItemStack result, Object... recipe) {
		super(loc, result, recipe);
	}

	public SealRecipe(ResourceLocation loc, @Nonnull ItemStack result, CraftingHelper.ShapedPrimer recipe) {
		super(loc, result, recipe);
	}

	@Nonnull
	@Override
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
		ItemStack output = super.getCraftingResult(inv);
		EntityPlayer user = findPlayer(inv);
		if (output.getItem() instanceof ItemOverlordsSeal && user != null) {
			if (output.getTagCompound() == null)
				output.setTagCompound(new NBTTagCompound());
			if (!output.getTagCompound().hasKey("Owner")) {
				output.getTagCompound().setString("Owner", user.getUniqueID().toString());
				output.getTagCompound().setString("OwnerName", user.getDisplayNameString());
			}
		}
		return output;
	}

	private static final Field eventHandlerField = ReflectionHelper.findField(InventoryCrafting.class, "eventHandler", "field_70465_c");
	private static final Field containerPlayerPlayerField = ReflectionHelper.findField(ContainerPlayer.class, "player", "field_82862_h");
	private static final Field slotCraftingPlayerField = ReflectionHelper.findField(SlotCrafting.class, "player", "field_75238_b");

	@Nullable
	private static EntityPlayer findPlayer(@Nonnull InventoryCrafting inv) {
		try {
			Container container = (Container) eventHandlerField.get(inv);
			if (container instanceof ContainerPlayer) {
				return (EntityPlayer) containerPlayerPlayerField.get(container);
			} else if (container instanceof ContainerWorkbench) {
				return (EntityPlayer) slotCraftingPlayerField.get(container.getSlot(0));
			} else {
				Overlord.logWarn("Unable to find player for crafting inventory: " + inv.getName());
				return null;
			}
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
	}

	public static class Factory implements IRecipeFactory {

		@Override
		public IRecipe parse(final JsonContext context, final JsonObject json) {
			String group = JsonUtils.getString(json, "group", "");

			Map<Character, Ingredient> ingMap = Maps.newHashMap();
			for (Map.Entry<String, JsonElement> entry : JsonUtils.getJsonObject(json, "key").entrySet())
			{
				if (entry.getKey().length() != 1)
					throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey() + "' is an invalid symbol (must be 1 character only).");
				if (" ".equals(entry.getKey()))
					throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");

				ingMap.put(entry.getKey().toCharArray()[0], CraftingHelper.getIngredient(entry.getValue(), context));
			}

			ingMap.put(' ', Ingredient.EMPTY);

			JsonArray patternJ = JsonUtils.getJsonArray(json, "pattern");

			if (patternJ.size() == 0)
				throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");

			String[] pattern = new String[patternJ.size()];
			for (int x = 0; x < pattern.length; ++x)
			{
				String line = JsonUtils.getString(patternJ.get(x), "pattern[" + x + "]");
				if (x > 0 && pattern[0].length() != line.length())
					throw new JsonSyntaxException("Invalid pattern: each row must  be the same width");
				pattern[x] = line;
			}

			CraftingHelper.ShapedPrimer primer = new CraftingHelper.ShapedPrimer();
			primer.width = pattern[0].length();
			primer.height = pattern.length;
			primer.mirrored = JsonUtils.getBoolean(json, "mirrored", true);
			primer.input = NonNullList.withSize(primer.width * primer.height, Ingredient.EMPTY);

			Set<Character> keys = Sets.newHashSet(ingMap.keySet());
			keys.remove(' ');

			int x = 0;
			for (String line : pattern)
			{
				for (char chr : line.toCharArray())
				{
					Ingredient ing = ingMap.get(chr);
					if (ing == null)
						throw new JsonSyntaxException("Pattern references symbol '" + chr + "' but it's not defined in the key");
					primer.input.set(x++, ing);
					keys.remove(chr);
				}
			}

			if (!keys.isEmpty())
				throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + keys);

			ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);
			return new SealRecipe(group.isEmpty() ? null : new ResourceLocation(group), result, primer);
		}
	}
}
