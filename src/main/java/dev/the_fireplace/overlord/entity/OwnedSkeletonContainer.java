package dev.the_fireplace.overlord.entity;

import dev.the_fireplace.overlord.container.ContainerEquipmentSlot;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import java.util.Objects;

public class OwnedSkeletonContainer extends ScreenHandler
{
    private static final EquipmentSlot[] EQUIPMENT_SLOT_ORDER = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    public final boolean onServer;
    private final OwnedSkeletonEntity owner;
    private final SkeletonInventory inventory;

    public OwnedSkeletonContainer(PlayerInventory playerInventory, boolean onServer, OwnedSkeletonEntity owner, int syncId) {
        super(OverlordEntities.OWNED_SKELETON_SCREEN_HANDLER, syncId);
        this.onServer = onServer;
        this.owner = owner;
        this.inventory = owner.getInventory();

        addSlots(playerInventory);
    }

    //region addSlots
    private void addSlots(PlayerInventory playerInventory) {
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

    private void addPlayerSlots(PlayerInventory playerInventory) {
        addPlayerInventorySlots(playerInventory);
        addPlayerHotbarSlots(playerInventory);
    }

    /**
     * Inventory indexes 9-35
     * Slot IDs 42-68
     */
    private void addPlayerInventorySlots(PlayerInventory playerInventory) {
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
    private void addPlayerHotbarSlots(PlayerInventory playerInventory) {
        for (int xIndex = 0; xIndex < 9; ++xIndex) {
            this.addSlot(new Slot(playerInventory, xIndex, 8 + xIndex * 18, 142 + 86));
        }
    }
    //endregion

    @Override
    public ItemStack transferSlot(PlayerEntity player, int invSlot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            int minSkeletonSlot = 0;
            int maxSkeletonSlot = 41;
            int minPlayerSlot = 42;
            int maxPlayerSlot = 77;
            //TODO prioritize weapon slot only for weapons, off hand only for ammo/shields
            if (invSlot <= maxSkeletonSlot) {
                // Transfer from skeleton to player
                if (!this.insertItem(itemStack2, minPlayerSlot, maxPlayerSlot, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Transfer from player to skeleton
                if (!this.insertItem(itemStack2, minSkeletonSlot, maxSkeletonSlot, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
        }

        return itemStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return FabricLoader.getInstance().isDevelopmentEnvironment() || Objects.equals(owner.getOwnerUuid(), player.getUuid());
    }

    public OwnedSkeletonEntity getOwner() {
        return owner;
    }
}
