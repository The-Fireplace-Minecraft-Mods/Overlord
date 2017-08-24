package the_fireplace.overlord.handlers;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.registry.MilkRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MilkBottleWrapper implements IFluidHandlerItem, ICapabilityProvider {
	@Nonnull
	protected ItemStack container;

	public MilkBottleWrapper(@Nonnull ItemStack container)
	{
		this.container = container;
	}

	protected void setFluid(@Nullable FluidStack fluidStack)
	{
		if (fluidStack == null)
			container = new ItemStack(Items.GLASS_BOTTLE);
		else
			container = new ItemStack(Overlord.milk_bottle);
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		return MilkRegistry.isMilkRegistered() && capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY;
	}

	@Nullable
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		if (MilkRegistry.isMilkRegistered() && capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
			return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.cast(this);
		return null;
	}

	@Nonnull
	@Override
	public ItemStack getContainer() {
		return container;
	}

	@Override
	public IFluidTankProperties[] getTankProperties() {
		return new FluidTankProperties[] { new FluidTankProperties(getFluid(), Fluid.BUCKET_VOLUME) };
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		if (container.getCount() != 1 || resource == null || resource.amount < Fluid.BUCKET_VOLUME || getFluid() != null || !resource.getFluid().getName().toLowerCase().equals("milk"))
			return 0;
		if (doFill)
			setFluid(resource);
		return Fluid.BUCKET_VOLUME;
	}

	@Nullable
	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		if (container.getCount() != 1 || resource == null || resource.amount < Fluid.BUCKET_VOLUME)
			return null;

		FluidStack fluidStack = getFluid();
		if (fluidStack != null && fluidStack.isFluidEqual(resource)) {
			if (doDrain)
				setFluid(null);
			return fluidStack;
		}

		return null;
	}

	@Nullable
	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		if (container.getCount() != 1 || maxDrain < Fluid.BUCKET_VOLUME)
			return null;

		FluidStack fluidStack = getFluid();
		if (fluidStack != null) {
			if (doDrain)
				setFluid(null);
			return fluidStack;
		}

		return null;
	}

	@Nullable
	public FluidStack getFluid()
	{
		Item item = container.getItem();
		if (MilkRegistry.isMilkRegistered() && item == Overlord.milk_bottle)
			return FluidRegistry.getFluidStack("milk", Fluid.BUCKET_VOLUME);
		return null;
	}
}
