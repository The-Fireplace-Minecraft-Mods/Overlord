package the_fireplace.overlord.tools;

import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

@SuppressWarnings("WeakerAccess")
public final class SkeletonNBTUtil {
	public static void readArmorInventoryFromNBT(NBTTagList armorInv, InventoryBasic inventory) {
		for (int i = 0; i < armorInv.tagCount(); i++) {
			NBTTagCompound item = (NBTTagCompound) armorInv.get(i);
			int slot = item.getByte("SlotSkeletonEquipment");
			if (slot >= 0 && slot < inventory.getSizeInventory()) {
				inventory.setInventorySlotContents(slot, new ItemStack(item));
			}
		}
	}

	public static void readInventoryFromNBT(NBTTagList mainInv, InventoryBasic inventory) {
		for (int i = 0; i < mainInv.tagCount(); i++) {
			NBTTagCompound item = (NBTTagCompound) mainInv.get(i);
			int slot = item.getByte("SlotSkeletonInventory");
			if (slot >= 0 && slot < inventory.getSizeInventory()) {
				inventory.setInventorySlotContents(slot, new ItemStack(item));
			}
		}
	}

	public static void writeEquipmentToNBT(NBTTagList armorInv, InventoryBasic inventory) {
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack is = inventory.getStackInSlot(i);
			if (!is.isEmpty()) {
				NBTTagCompound item = new NBTTagCompound();

				item.setByte("SlotSkeletonEquipment", (byte) i);
				is.writeToNBT(item);

				armorInv.appendTag(item);
			}
		}
	}

	public static void writeInventoryToNBT(NBTTagList mainInv, InventoryBasic inventory) {
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack is = inventory.getStackInSlot(i);
			if (!is.isEmpty()) {
				NBTTagCompound item = new NBTTagCompound();

				item.setByte("SlotSkeletonInventory", (byte) i);
				is.writeToNBT(item);

				mainInv.appendTag(item);
			}
		}
	}
}
