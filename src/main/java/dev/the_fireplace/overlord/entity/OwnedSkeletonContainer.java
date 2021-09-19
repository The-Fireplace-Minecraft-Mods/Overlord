package dev.the_fireplace.overlord.entity;

import dev.the_fireplace.overlord.container.ContainerEquipmentSlot;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

public class OwnedSkeletonContainer extends Container {
    private static final EquipmentSlot[] EQUIPMENT_SLOT_ORDER = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    public final boolean onServer;
    private final OwnedSkeletonEntity owner;
    private final SkeletonInventory inventory;
    public OwnedSkeletonContainer(PlayerInventory playerInventory, boolean onServer, OwnedSkeletonEntity owner, int syncId) {
        super(null, syncId);
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
            EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(itemStack);
            if (invSlot == 0) {
                if (!this.insertItem(itemStack2, 9, 45, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onStackChanged(itemStack2, itemStack);
            } else if (invSlot >= 1 && invSlot < 5) {
                if (!this.insertItem(itemStack2, 9, 45, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (invSlot >= 5 && invSlot < 9) {
                if (!this.insertItem(itemStack2, 9, 45, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (equipmentSlot.getType() == EquipmentSlot.Type.ARMOR && !this.slots.get(8 - equipmentSlot.getEntitySlotId()).hasStack()) {
                int i = 8 - equipmentSlot.getEntitySlotId();
                if (!this.insertItem(itemStack2, i, i + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (equipmentSlot == EquipmentSlot.OFFHAND && !this.slots.get(45).hasStack()) {
                if (!this.insertItem(itemStack2, 45, 46, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (invSlot >= 9 && invSlot < 36) {
                if (!this.insertItem(itemStack2, 36, 45, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (invSlot >= 36 && invSlot < 45) {
                if (!this.insertItem(itemStack2, 9, 36, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack2, 9, 45, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            ItemStack itemStack3 = slot.onTakeItem(player, itemStack2);
            if (invSlot == 0) {
                player.dropItem(itemStack3, false);
            }
        }

        return itemStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return FabricLoader.getInstance().isDevelopmentEnvironment() || owner.getOwnerId().equals(player.getUuid());
    }

    public OwnedSkeletonEntity getOwner() {
        return owner;
    }
}
