package the_fireplace.overlord.registry;

import com.google.common.collect.Maps;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.oredict.OreDictionary;
import the_fireplace.overlord.Overlord;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;

/**
 * @author The_Fireplace
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class MilkRegistry {
	private static MilkRegistry instance;
	private HashMap<ItemStack, ItemStack> milks;

	public static final IFluidHandler FLUID_BLACK_HOLE = new IFluidHandler() {
		@Override
		public IFluidTankProperties[] getTankProperties() {
			return new IFluidTankProperties[0];
		}

		@Override
		public int fill(FluidStack stack, boolean b) {
			return Integer.MAX_VALUE;
		}

		@Nullable
		@Override
		public FluidStack drain(FluidStack stack, boolean b) {
			return null;
		}

		@Nullable
		@Override
		public FluidStack drain(int i, boolean b) {
			return null;
		}
	};

	private MilkRegistry() {
		milks = Maps.newHashMap();
	}

	public static MilkRegistry getInstance() {
		if (instance == null)
			instance = new MilkRegistry();
		return instance;
	}

	public static boolean isMilkRegistered(){
		return Overlord.instance.isMilkRegistered;
	}

	@Nullable
	public static Fluid getMilk(){
		return isMilkRegistered() ? Overlord.instance.milk : null;
	}

	private static boolean stackContainsMilk(ItemStack stack){
		FluidStack f = FluidUtil.getFluidContained(stack);
		return isMilkRegistered() && f != null && f.getFluid().equals(getMilk());
	}

	/**
	 * Registers an item as Milk for the Skeleton Makers
	 *
	 * @param inputItem
	 * 		The Milk itemstack
	 * @param emptyItem
	 * 		The emptied version of the Milk itemstack.
	 */
	public void registerMilk(ItemStack inputItem, ItemStack emptyItem) {
		ItemStack inputCopy = new ItemStack(inputItem.getItem(), 1, inputItem.getMetadata());
		ItemStack emptyCopy = !emptyItem.isEmpty() ? new ItemStack(emptyItem.getItem(), 1, emptyItem.getMetadata()) : ItemStack.EMPTY;
		if (!milks.containsKey(inputCopy))
			milks.put(inputCopy, emptyCopy);
		else
			Overlord.logError("ItemStack was already registered as Milk: " + inputCopy.toString() + ", skipping...");
	}

	/**
	 * Checks if a stack is Milk
	 *
	 * @param stackToCheck
	 * 		The stack to check
	 * @return True if the stack is registered as Milk, false otherwise.
	 */
	public boolean isMilk(ItemStack stackToCheck) {
		if(isMilkRegistered())
			return stackContainsMilk(stackToCheck);
		else {
			for (ItemStack milk : milks.keySet()) {
				if (milk.getItem() == stackToCheck.getItem() && (milk.getMetadata() == OreDictionary.WILDCARD_VALUE || milk.getMetadata() == stackToCheck.getMetadata()))
					return true;
			}
			return false;
		}
	}

	/**
	 * Returns an emptied version of the Milk ItemStack.
	 *
	 * @param inputStack
	 * 		The Milk itemstack
	 * @return The emptied ItemStack, or null if there isn't one.
	 */
	public ItemStack getEmptiedStack(ItemStack inputStack) {
		if(isMilkRegistered()){
			FluidActionResult result = FluidUtil.tryEmptyContainer(inputStack, FLUID_BLACK_HOLE, 1000, null, false);
			return result.getResult();
		}else {
			if (inputStack.isEmpty())
				return ItemStack.EMPTY;
			for (ItemStack milk : milks.keySet()) {
				if (milk.getItem() == inputStack.getItem() && (milk.getMetadata() == OreDictionary.WILDCARD_VALUE || milk.getMetadata() == inputStack.getMetadata()))
					return milks.get(milk);
			}
			return ItemStack.EMPTY;
		}
	}
}
