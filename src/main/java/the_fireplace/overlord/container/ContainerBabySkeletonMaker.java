package the_fireplace.overlord.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import the_fireplace.overlord.Overlord;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author The_Fireplace
 */
@ParametersAreNonnullByDefault
public class ContainerBabySkeletonMaker extends Container {
    private IInventory te;
    private static final EntityEquipmentSlot[] EQUIPMENT_SLOTS = new EntityEquipmentSlot[] {EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};

    public ContainerBabySkeletonMaker(InventoryPlayer invPlayer, IInventory entity) {
        this.te = entity;

        for (int x = 0; x < 9; x++) {
            this.addSlotToContainer(new Slot(invPlayer, x, 8 + x * 18, 142));//player inventory IDs 0 to 8
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlotToContainer(new Slot(invPlayer, 9 + x + y * 9, 8 + x * 18, 84 + y * 18));//player inventory IDs 9 to 35
            }
        }

        this.addSlotToContainer(new SlotSeal(entity, 0, 96, 6));//tile entity ID 0

        this.addSlotToContainer(new SlotBone(entity, 1, 118, 6));//tile entity ID 1

        this.addSlotToContainer(new SlotMilk(entity, 2, 154, 34));//tile entity ID 2

        this.addSlotToContainer(new SlotOutput(entity, 3, 154, 62));//tile entity ID 3

        for (int x = 0; x < 4; ++x)
        {
            final EntityEquipmentSlot entityequipmentslot = EQUIPMENT_SLOTS[x];
            this.addSlotToContainer(new Slot(entity, 4 + (3 - x), 28, 8 + x * 18)//tile entity IDs 4 to 7
            {
                @Override
                public int getSlotStackLimit()
                {
                    return 1;
                }

                @Override
                public boolean isItemValid(@Nullable ItemStack stack)
                {
                    return stack != null && stack.getItem().isValidArmor(stack, entityequipmentslot, null);
                }
                @Override
                @SideOnly(Side.CLIENT)
                public String getSlotTexture()
                {
                    return ItemArmor.EMPTY_SLOT_NAMES[entityequipmentslot.getIndex()];
                }
            });
        }

        this.addSlotToContainer(new Slot(entity, 8, 8, 26));//tile entity ID 8

        this.addSlotToContainer(new SlotSkinsuit(entity, 9, 6, 6));//tile entity ID 9
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return te.isUsableByPlayer(playerIn);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int i) {
        Slot slot = getSlot(i);
        if (slot != null && slot.getHasStack()) {
            ItemStack is = slot.getStack();
            ItemStack result = is.copy();

            if (i >= 36) {
                if (!mergeItemStack(is, 0, 36, false)) {
                    return null;
                }
            } else if (!mergeItemStack(is, 36, 36 + te.getSizeInventory(), is.getItem() == Overlord.skinsuit)) {
                return null;
            }
            if (is.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }
            slot.onPickupFromSlot(player, is);
            return result;
        }
        return null;
    }
}
