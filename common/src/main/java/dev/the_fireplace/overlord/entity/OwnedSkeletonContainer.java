package dev.the_fireplace.overlord.entity;

import dev.the_fireplace.annotateddi.impl.domain.loader.LoaderHelper;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.container.ContainerEquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class OwnedSkeletonContainer extends AbstractContainerMenu
{
    private static final EquipmentSlot[] EQUIPMENT_SLOT_ORDER = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    public final boolean onServer;
    private final OwnedSkeletonEntity owner;
    private final SkeletonInventory inventory;

    public OwnedSkeletonContainer(Inventory playerInventory, boolean onServer, OwnedSkeletonEntity owner, int syncId) {
        super(OverlordConstants.getInjector().getInstance(OverlordEntities.class).getOwnedSkeletonScreenHandler(), syncId);
        this.onServer = onServer;
        this.owner = owner;
        this.inventory = owner.getInventory();

        addSlots(playerInventory);
    }

    //region addSlots
    private void addSlots(Inventory playerInventory) {
        addSkeletonSlots();
        addPlayerSlots(playerInventory);
    }

    private void addSkeletonSlots() {
        addSkeletonEquipmentSlots();
        addSkeletonInventorySlots();
    }

    /**
     * Inventory indexes 36-41
     * Slot IDs 0-5
     */
    private void addSkeletonEquipmentSlots() {
        for (int yIndex = 0; yIndex < 4; ++yIndex) {
            this.addSlot(new ContainerEquipmentSlot(
                EQUIPMENT_SLOT_ORDER[yIndex],
                inventory,
                39 - yIndex,
                8,
                8 + yIndex * 18
            ));
        }

        this.addSlot(new ContainerEquipmentSlot(EquipmentSlot.MAINHAND, inventory, 40, 77, 62 - 18));
        this.addSlot(new ContainerEquipmentSlot(EquipmentSlot.OFFHAND, inventory, 41, 77, 62));
    }

    /**
     * Inventory indexes 0-35
     * Slot IDs 6-41
     */
    private void addSkeletonInventorySlots() {
        for (int yIndex = 0; yIndex < 4; ++yIndex) {
            for (int xIndex = 0; xIndex < 9; ++xIndex) {
                this.addSlot(new Slot(
                    inventory,
                    xIndex + yIndex * 9,
                    8 + xIndex * 18,
                    84 + yIndex * 18
                ));
            }
        }
    }

    private void addPlayerSlots(Inventory playerInventory) {
        addPlayerInventorySlots(playerInventory);
        addPlayerHotbarSlots(playerInventory);
    }

    /**
     * Inventory indexes 9-35
     * Slot IDs 42-68
     */
    private void addPlayerInventorySlots(Inventory playerInventory) {
        for (int yIndex = 0; yIndex < 3; ++yIndex) {
            for (int xIndex = 0; xIndex < 9; ++xIndex) {
                this.addSlot(new Slot(
                    playerInventory,
                    xIndex + (yIndex + 1) * 9,
                    8 + xIndex * 18,
                    84 + 86 + yIndex * 18
                ));
            }
        }
    }

    /**
     * Inventory indexes 0-8
     * Slot IDs 69-77
     */
    private void addPlayerHotbarSlots(Inventory playerInventory) {
        for (int xIndex = 0; xIndex < 9; ++xIndex) {
            this.addSlot(new Slot(playerInventory, xIndex, 8 + xIndex * 18, 142 + 86));
        }
    }
    //endregion

    @Override
    public ItemStack quickMoveStack(Player player, int invSlot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasItem()) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.copy();
            int minSkeletonSlot = 0;
            int maxSkeletonSlot = 41;
            int minPlayerSlot = 42;
            int maxPlayerSlot = 77;
            //TODO prioritize weapon slot only for weapons, off hand only for ammo/shields
            if (invSlot <= maxSkeletonSlot) {
                // Transfer from skeleton to player
                if (!this.moveItemStackTo(itemStack2, minPlayerSlot, maxPlayerSlot, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Transfer from player to skeleton
                if (!this.moveItemStackTo(itemStack2, minSkeletonSlot, maxSkeletonSlot, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (itemStack2.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
        }

        return itemStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return OverlordConstants.getInjector().getInstance(LoaderHelper.class).isDevelopmentEnvironment() || Objects.equals(owner.getOwnerUUID(), player.getUUID());
    }

    public OwnedSkeletonEntity getOwner() {
        return owner;
    }
}
