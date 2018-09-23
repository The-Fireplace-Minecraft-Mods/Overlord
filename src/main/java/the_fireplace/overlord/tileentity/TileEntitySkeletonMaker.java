package the_fireplace.overlord.tileentity;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.config.ConfigValues;
import the_fireplace.overlord.entity.EntitySkeletonWarrior;
import the_fireplace.overlord.items.ItemOverlordsSeal;
import the_fireplace.overlord.registry.AugmentRegistry;
import the_fireplace.overlord.registry.MilkRegistry;
import the_fireplace.overlord.tools.SkinType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

/**
 * @author The_Fireplace
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TileEntitySkeletonMaker extends TileEntity implements ITickable, ISidedInventory, ISkeletonMaker, IFluidHandler, IFluidTank {
	private ItemStack[] inventory;
	private int heldMilkAmount;
	public static final int heldMilkAmountMax = 2000;
	public static final int[] clearslots = new int[]{6, 7, 8, 9, 10, 11, 12};

	public TileEntitySkeletonMaker() {
		inventory = new ItemStack[13];
	}

	@Override
	public void update() {
		if (!getStackInSlot(4).isEmpty() && MilkRegistry.getInstance().isMilk(getStackInSlot(4)) && heldMilkAmount < heldMilkAmountMax) {
			if (!getStackInSlot(5).isEmpty() && !MilkRegistry.getInstance().getEmptiedStack(getStackInSlot(4)).isEmpty() && getStackInSlot(5).getItem() == MilkRegistry.getInstance().getEmptiedStack(getStackInSlot(4)).getItem() && getStackInSlot(5).getCount() < getStackInSlot(5).getMaxStackSize()) {
				setMilk(heldMilkAmount + 1000);
				getStackInSlot(5).grow(1);
				if (getStackInSlot(4).getCount() > 1)
					getStackInSlot(4).shrink(1);
				else
					setInventorySlotContents(4, ItemStack.EMPTY);
			} else if (getStackInSlot(5).isEmpty()) {
				setMilk(heldMilkAmount + 1000);
				setInventorySlotContents(5, MilkRegistry.getInstance().getEmptiedStack(getStackInSlot(4)));
				if (getStackInSlot(4).getCount() > 1)
					getStackInSlot(4).shrink(1);
				else
					setInventorySlotContents(4, ItemStack.EMPTY);
			}
		}
	}

	@Override
	public void spawnSkeleton(@Nullable EntityPlayer player) {
		if (!canSpawnSkeleton() || world.isRemote)
			return;
		UUID owner = null;
		if (!getStackInSlot(0).isEmpty()) {
			if (getStackInSlot(0).getTagCompound() != null) {
				owner = UUID.fromString(getStackInSlot(0).getTagCompound().getString("Owner"));
				if (getStackInSlot(0).getItem() instanceof ItemOverlordsSeal)
					if (((ItemOverlordsSeal) getStackInSlot(0).getItem()).isConsumable())
						getStackInSlot(0).shrink(1);
			}
		}
		EntitySkeletonWarrior skeletonWarrior = new EntitySkeletonWarrior(world, owner);
		skeletonWarrior.setLocationAndAngles(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 1, 0);
		skeletonWarrior.setItemStackToSlot(EntityEquipmentSlot.HEAD, getStackInSlot(9));
		skeletonWarrior.setItemStackToSlot(EntityEquipmentSlot.CHEST, getStackInSlot(8));
		skeletonWarrior.setItemStackToSlot(EntityEquipmentSlot.LEGS, getStackInSlot(7));
		skeletonWarrior.setItemStackToSlot(EntityEquipmentSlot.FEET, getStackInSlot(6));
		skeletonWarrior.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, getStackInSlot(10));
		skeletonWarrior.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, getStackInSlot(11));
		if (!getStackInSlot(3).isEmpty()) {
			ItemStack augment = getStackInSlot(3).copy();
			augment.setCount(1);
			skeletonWarrior.equipInventory.setInventorySlotContents(6, augment);
			if (getStackInSlot(3).getCount() > 1) {
				getStackInSlot(3).shrink(1);
			} else {
				setInventorySlotContents(3, ItemStack.EMPTY);
			}
		}

		world.spawnEntity(skeletonWarrior);

		if (player != null && player instanceof EntityPlayerMP) {
			CriteriaTriggers.SUMMONED_ENTITY.trigger((EntityPlayerMP) player, skeletonWarrior);
		}

		world.playSound(null, pos, Overlord.CREATE_SKELETON_SOUND, SoundCategory.BLOCKS, 1.0f, 0.5f + world.rand.nextFloat());

		if (!getStackInSlot(12).isEmpty())
			skeletonWarrior.setSkinsuit(getStackInSlot(12), SkinType.getSkinTypeFromStack(getStackInSlot(12)));
		setMilk((byte) 0);
		for (int i : clearslots) {
			setInventorySlotContents(i, ItemStack.EMPTY);
		}
		if (!getStackInSlot(1).isEmpty()) {
			if (getStackInSlot(1).getCount() == ConfigValues.BONEREQ_WARRIOR)
				setInventorySlotContents(1, ItemStack.EMPTY);
			else if (getStackInSlot(1).getCount() < ConfigValues.BONEREQ_WARRIOR) {
				setInventorySlotContents(1, ItemStack.EMPTY);
				if (!getStackInSlot(2).isEmpty())
					if (getStackInSlot(2).getCount() <= ConfigValues.BONEREQ_WARRIOR)
						setInventorySlotContents(2, ItemStack.EMPTY);
					else
						getStackInSlot(2).shrink(ConfigValues.BONEREQ_WARRIOR);
			} else
				getStackInSlot(1).shrink(ConfigValues.BONEREQ_WARRIOR);
		} else {
			if (!getStackInSlot(2).isEmpty())
				if (getStackInSlot(2).getCount() <= ConfigValues.BONEREQ_WARRIOR)
					setInventorySlotContents(2, ItemStack.EMPTY);
				else
					getStackInSlot(2).shrink(ConfigValues.BONEREQ_WARRIOR);
		}
	}

	@Override
	public boolean canSpawnSkeleton() {
		if (getStackInSlot(1).isEmpty() && getStackInSlot(2).isEmpty())
			return false;
		int s1 = 0;
		int s2 = 0;
		if (!getStackInSlot(1).isEmpty())
			s1 = getStackInSlot(1).getCount();
		if (!getStackInSlot(2).isEmpty())
			s2 = getStackInSlot(2).getCount();
		return heldMilkAmount >= heldMilkAmountMax && s1 + s2 >= ConfigValues.SERVER_BONEREQ_WARRIOR;
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(this.pos, getBlockMetadata(), getUpdateTag());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}

	@Override
	public String getName() {
		return Overlord.proxy.translateToLocal("tile.skeleton_maker.name");
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentTranslation("tile.skeleton_maker.name");
	}

	@Override
	public int getSizeInventory() {
		return inventory.length;
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemStack : inventory)
			if (!itemStack.isEmpty())
				return false;
		return true;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		if (inventory[index] != null)
			return inventory[index];
		else
			return ItemStack.EMPTY;
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		ItemStack is = getStackInSlot(index);
		if (!is.isEmpty()) {
			if (is.getCount() <= count) {
				setInventorySlotContents(index, ItemStack.EMPTY);
			} else {
				is = is.splitStack(count);
				markDirty();
			}
		}
		return is;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack is = getStackInSlot(index);
		setInventorySlotContents(index, ItemStack.EMPTY);
		return is;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		inventory[index] = stack;

		if (!stack.isEmpty() && stack.getCount() > getInventoryStackLimit()) {
			stack.setCount(getInventoryStackLimit());
		}
		markDirty();
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return player.getDistanceSq(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) <= 64;
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return (index == 0 && stack.getItem() instanceof ItemOverlordsSeal) || ((index == 1 || index == 2) && stack.getItem() == Items.BONE) || (index == 3 && AugmentRegistry.getAugment(stack) != null) || (index == 4 && (stack.getItem() == Items.MILK_BUCKET || stack.getItem() == Overlord.milk_bottle)) || (index > 5 && index < 10 && stack.getItem().isValidArmor(stack, getSlotEquipmentType(index), null) || (index == 12 && stack.getItem() == Overlord.skinsuit));
	}

	private EntityEquipmentSlot getSlotEquipmentType(int index) {
		if (index == 6)
			return EntityEquipmentSlot.FEET;
		if (index == 7)
			return EntityEquipmentSlot.LEGS;
		if (index == 8)
			return EntityEquipmentSlot.CHEST;
		if (index == 9)
			return EntityEquipmentSlot.HEAD;
		if (index == 10)
			return EntityEquipmentSlot.MAINHAND;
		return EntityEquipmentSlot.OFFHAND;
	}

	@Override
	public int getField(int id) {
		switch (id) {
			case 0:
				return this.heldMilkAmount;
			default:
				return 0;
		}
	}

	@Override
	public void setField(int id, int value) {
		switch (id) {
			case 0:
				this.heldMilkAmount = value;
				break;
			default:

		}
	}

	@Override
	public int getFieldCount() {
		return 1;
	}

	@Override
	public void clear() {
		for (int i = 0; i < inventory.length; ++i) {
			inventory[i] = ItemStack.EMPTY;
		}
	}

	@Override
	@Nonnull
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);

		NBTTagList list = new NBTTagList();
		for (int i = 0; i < getSizeInventory(); i++) {
			ItemStack is = getStackInSlot(i);
			if (!is.isEmpty()) {
				NBTTagCompound item = new NBTTagCompound();

				item.setByte("SlotSkeletonMaker", (byte) i);
				is.writeToNBT(item);

				list.appendTag(item);
			}
		}
		compound.setInteger("HeldMilk", heldMilkAmount);
		compound.setTag("ItemsSkeletonMaker", list);
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		NBTTagList list = (NBTTagList) compound.getTag("ItemsSkeletonMaker");
		if (list != null) {
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound item = (NBTTagCompound) list.get(i);
				int slot = item.getByte("SlotSkeletonMaker");
				if (slot >= 0 && slot < getSizeInventory()) {
					setInventorySlotContents(slot, new ItemStack(item));
				}
			}
		} else {
			Overlord.logWarn("List was null when reading TileEntitySkeletonMaker NBTTagCompound");
		}
		if(compound.getByte("Milk") != 0)
			this.heldMilkAmount = compound.getByte("Milk")*1000;
		else
			this.heldMilkAmount = compound.getInteger("HeldMilk");
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		if (side == EnumFacing.EAST || side == EnumFacing.WEST || side == EnumFacing.NORTH || side == EnumFacing.SOUTH || side == EnumFacing.UP) {
			return new int[]{1, 2, 3, 4, 6, 7, 8, 9, 12};
		} else if (side == EnumFacing.DOWN) {
			return new int[]{5};
		} else {
			throw new IllegalArgumentException("Invalid side: " + side);
		}
	}

	@Override
	public boolean canInsertItem(int index, ItemStack stack, EnumFacing direction) {
		if (!stack.isEmpty()) {
			if (index >= 1 && index < 5 || index >= 6 && index < 10 || index == 12) {
				if (this.isItemValidForSlot(index, stack))
					return true;
			}
		}
		return false;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		if (!stack.isEmpty())
			if (index == 5)
				return true;
		return false;
	}

	IItemHandler handlerTop = new SidedInvWrapper(this, EnumFacing.UP);
	IItemHandler handlerBottom = new SidedInvWrapper(this, EnumFacing.DOWN);
	IItemHandler handlerSide = new SidedInvWrapper(this, EnumFacing.WEST);

	@SuppressWarnings({"unchecked", "Duplicates"})
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		if (facing != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			if (facing == EnumFacing.DOWN)
				return (T) handlerBottom;
			else if (facing == EnumFacing.UP)
				return (T) handlerTop;
			else
				return (T) handlerSide;
		if (MilkRegistry.isMilkRegistered() && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return (T) this;
		return super.getCapability(capability, facing);
	}

	public void setMilk(int milk) {
		this.heldMilkAmount = milk;
		markDirty();
		if (!world.isRemote)
			world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public IFluidTankProperties[] getTankProperties() {
		return getFluid() != null ? new IFluidTankProperties[]{new FluidTankProperties(new FluidStack(getFluid(), getFluidAmount()), getCapacity())} : new IFluidTankProperties[]{};
	}

	@Nullable
	@Override
	public FluidStack getFluid() {
		Fluid milk = MilkRegistry.getMilk();
		if (milk != null && getFluidAmount() > 0)
			return new FluidStack(milk, getFluidAmount());
		return null;
	}

	@Override
	public int getFluidAmount() {
		return heldMilkAmount;
	}

	@Override
	public int getCapacity() {
		return heldMilkAmountMax;
	}

	@Override
	public FluidTankInfo getInfo() {
		return new FluidTankInfo(this);
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		if(MilkRegistry.isMilkRegistered()) {
			Fluid milk = MilkRegistry.getMilk();
			int maxFillAmount = getCapacity() - getFluidAmount();
			if (milk != null && resource.getFluid().equals(milk) && maxFillAmount > 0) {
				if (maxFillAmount < resource.amount) {
					if (doFill)
						heldMilkAmount += maxFillAmount;
					return maxFillAmount;
				} else {
					if (doFill)
						heldMilkAmount += resource.amount;
					return resource.amount;
				}
			}
		}
		return 0;
	}

	@Nullable
	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		if(MilkRegistry.isMilkRegistered()) {
			Fluid milk = MilkRegistry.getMilk();
			if (resource.amount > 0 && milk != null && resource.getFluid().equals(milk)) {
				if (resource.amount < getFluidAmount()) {
					if (doDrain)
						heldMilkAmount -= resource.amount;
					return new FluidStack(milk, resource.amount);
				} else {
					int prevHeldMilk = getFluidAmount();
					if (doDrain)
						heldMilkAmount = 0;
					return new FluidStack(milk, prevHeldMilk);
				}
			}
		}
		return null;
	}

	@Nullable
	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		if(MilkRegistry.isMilkRegistered()) {
			Fluid milk = MilkRegistry.getMilk();
			if (milk != null && maxDrain > 0) {
				if (maxDrain < getFluidAmount()) {
					if (doDrain)
						heldMilkAmount -= maxDrain;
					return new FluidStack(milk, maxDrain);
				} else {
					int prevHeldWater = getFluidAmount();
					if (doDrain)
						heldMilkAmount = 0;
					return new FluidStack(milk, prevHeldWater);
				}
			}
		}
		return null;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		return (MilkRegistry.isMilkRegistered() && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) || super.hasCapability(capability, facing);
	}
}
