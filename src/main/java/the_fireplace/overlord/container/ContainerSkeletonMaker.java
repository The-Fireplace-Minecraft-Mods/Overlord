package the_fireplace.overlord.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import the_fireplace.overlord.items.ItemSkinsuit;

import javax.annotation.Nonnull;

/**
 * @author The_Fireplace
 */
public class ContainerSkeletonMaker extends Container {
    private IInventory te;

    private int milk;

    private static final EntityEquipmentSlot[] EQUIPMENT_SLOTS = new EntityEquipmentSlot[] {EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};

    public ContainerSkeletonMaker(InventoryPlayer invPlayer, IInventory entity) {
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

        this.addSlotToContainer(new SlotBone(entity, 2, 118 + 18, 6));//tile entity ID 2

        this.addSlotToContainer(new SlotAugment(entity, 3, 118 + 2 * 18, 6));//tile entity ID 3

        this.addSlotToContainer(new SlotMilk(entity, 4, 154, 34));//tile entity ID 4

        this.addSlotToContainer(new SlotOutput(entity, 5, 154, 62));//tile entity ID 5

        for (int x = 0; x < 4; ++x)
        {
            final EntityEquipmentSlot entityequipmentslot = EQUIPMENT_SLOTS[x];
            this.addSlotToContainer(new Slot(entity, 6 + (3 - x), 28, 8 + x * 18)//tile entity IDs 6 to 9
            {
                @Override
                public int getSlotStackLimit()
                {
                    return 1;
                }

                @Override
                public boolean isItemValid(ItemStack stack)
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

        this.addSlotToContainer(new Slot(entity, 10, 8, 26));//tile entity ID 10

        this.addSlotToContainer(new Slot(entity, 11, 48, 26){
            @Override
            @SideOnly(Side.CLIENT)
            public String getSlotTexture()
            {
                return "minecraft:items/empty_armor_slot_shield";
            }
        });//tile entity ID 11

        this.addSlotToContainer(new SlotSkinsuit(entity, 12, 6, 6));//tile entity ID 12
    }

    @Override
    public void addListener(IContainerListener listener)
    {
        super.addListener(listener);
        listener.sendAllWindowProperties(this, te);
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (int i = 0; i < this.listeners.size(); ++i)
        {
            IContainerListener icontainerlistener = this.listeners.get(i);

            if (this.milk != te.getField(0))
            {
                icontainerlistener.sendProgressBarUpdate(this, 0, te.getField(0));
            }
        }

        this.milk = te.getField(0);
    }

    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data)
    {
        this.te.setField(id, data);
    }

    @Override
    public boolean canInteractWith(@Nonnull EntityPlayer playerIn) {
        return te.isUsableByPlayer(playerIn);
    }

    @Override
    @Nonnull
    public ItemStack transferStackInSlot(EntityPlayer player, int i) {
        Slot slot = getSlot(i);
        if (slot != null && slot.getHasStack()) {
            ItemStack is = slot.getStack();
            ItemStack result = is.copy();

            if (i >= 36) {
                if (!mergeItemStack(is, 0, 36, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!mergeItemStack(is, 36, 36 + te.getSizeInventory(), is.getItem() instanceof ItemSkinsuit)) {
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
