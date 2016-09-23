package the_fireplace.skeletonwars.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import the_fireplace.skeletonwars.tileentity.TileEntitySkeletonMaker;

/**
 * @author The_Fireplace
 */
public class ContainerSkeletonMaker extends Container {
    private TileEntitySkeletonMaker te;
    private static final EntityEquipmentSlot[] EQUIPMENT_SLOTS = new EntityEquipmentSlot[] {EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};

    public ContainerSkeletonMaker(InventoryPlayer invPlayer, TileEntitySkeletonMaker entity) {
        this.te = entity;

        for (int x = 0; x < 9; x++) {
            this.addSlotToContainer(new Slot(invPlayer, x, 8 + x * 18, 142));//player inventory IDs 0 to 8
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlotToContainer(new Slot(invPlayer, 9 + x + y * 9, 8 + x * 18, 84 + y * 18));//player inventory IDs 9 to 35
            }
        }

        this.addSlotToContainer(new SlotNamePlate(entity, 0, 96, 6));//tile entity ID 0

        for (int x = 0; x < 3; x++) {
            this.addSlotToContainer(new SlotBone(entity, 1+x, 118 + x * 18, 6));//tile entity IDs 1 to 3
        }

        this.addSlotToContainer(new SlotMilk(entity, 4, 154, 34));//tile entity ID 4

        this.addSlotToContainer(new SlotOutput(entity, 5, 154, 62));//tile entity ID 5

        for (int x = 0; x < 4; ++x)
        {
            final EntityEquipmentSlot entityequipmentslot = EQUIPMENT_SLOTS[x];
            this.addSlotToContainer(new Slot(entity, 6 + (3 - x), 28, 8 + x * 18)//tile entity IDs 6 to 9
            {
                public int getSlotStackLimit()
                {
                    return 1;
                }

                public boolean isItemValid(ItemStack stack)
                {
                    return stack != null && stack.getItem().isValidArmor(stack, entityequipmentslot, null);
                }
                @SideOnly(Side.CLIENT)
                public String getSlotTexture()
                {
                    return ItemArmor.EMPTY_SLOT_NAMES[entityequipmentslot.getIndex()];
                }
            });
        }

        this.addSlotToContainer(new Slot(entity, 10, 8, 26));//tile entity ID 10

        this.addSlotToContainer(new Slot(entity, 11, 48, 26));//tile entity ID 11
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return te.isUseableByPlayer(playerIn);
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
            } else if (!mergeItemStack(is, 36, 36 + te.getSizeInventory(), false)) {
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
