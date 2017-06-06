package the_fireplace.overlord.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import the_fireplace.overlord.entity.EntityBabySkeleton;

import javax.annotation.Nonnull;

/**
 * @author The_Fireplace
 */
public class ContainerBabySkeleton extends Container {
	private EntityBabySkeleton entity;
	private static final EntityEquipmentSlot[] EQUIPMENT_SLOTS = new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};

	public ContainerBabySkeleton(InventoryPlayer invPlayer, EntityBabySkeleton entity) {
		this.entity = entity;
		InventoryBasic armorInv = entity.equipInventory;
		for (int x = 0; x < 9; x++) {
			this.addSlotToContainer(new Slot(invPlayer, x, 8 + x * 18, 142));//player inventory IDs 0 to 8
		}

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				this.addSlotToContainer(new Slot(invPlayer, 9 + x + y * 9, 8 + x * 18, 84 + y * 18));//player inventory IDs 9 to 35
			}
		}

		for (int x = 0; x < 4; ++x) {
			final EntityEquipmentSlot entityequipmentslot = EQUIPMENT_SLOTS[x];
			this.addSlotToContainer(new Slot(armorInv, (3 - x), 8, 8 + x * 18)//Entity Equipment IDs 0 to 3
			{
				@Override
				public int getSlotStackLimit() {
					return 1;
				}

				@Override
				public boolean isItemValid(ItemStack stack) {
					return !stack.isEmpty() && stack.getItem().isValidArmor(stack, entityequipmentslot, entity);
				}

				@Override
				@SideOnly(Side.CLIENT)
				public String getSlotTexture() {
					return ItemArmor.EMPTY_SLOT_NAMES[entityequipmentslot.getIndex()];
				}
			});
		}

		this.addSlotToContainer(new Slot(armorInv, 4, 28, 44));//Entity Equipment ID 4
	}

	@Override
	public boolean canInteractWith(@Nonnull EntityPlayer playerIn) {
		return entity.getOwner() != null && entity.getOwner().equals(playerIn);
	}

	@Override
	@Nonnull
	public ItemStack transferStackInSlot(EntityPlayer player, int index) {
		Slot slot = getSlot(index);
		if (slot != null && slot.getHasStack()) {
			ItemStack is = slot.getStack();
			ItemStack result = is.copy();

			if (index >= 36) {
				if (!mergeItemStack(is, 0, 36, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!mergeItemStack(is, 36, 36 + entity.equipInventory.getSizeInventory(), false)) {
				return ItemStack.EMPTY;
			}
			if (is.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}
			slot.onTake(player, is);
			return result;
		}
		return ItemStack.EMPTY;
	}
}
