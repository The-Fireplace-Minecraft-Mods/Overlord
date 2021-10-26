package dev.the_fireplace.overlord.entity.ai.goal;

import dev.the_fireplace.overlord.domain.inventory.CommonPriorityMappers;
import dev.the_fireplace.overlord.domain.inventory.InventorySearcher;
import dev.the_fireplace.overlord.domain.world.ItemDropper;
import dev.the_fireplace.overlord.entity.ArmyEntity;
import dev.the_fireplace.overlord.util.EquipmentUtils;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

@Singleton
public class AIEquipmentHelper
{
    protected final InventorySearcher inventorySearcher;
    protected final CommonPriorityMappers commonPriorityMappers;
    protected final ItemDropper itemDropper;

    @Inject
    public AIEquipmentHelper(InventorySearcher inventorySearcher, CommonPriorityMappers commonPriorityMappers, ItemDropper itemDropper) {
        this.inventorySearcher = inventorySearcher;
        this.commonPriorityMappers = commonPriorityMappers;
        this.itemDropper = itemDropper;
    }

    public boolean isUsingRanged(ArmyEntity armyEntity) {
        return EquipmentUtils.isRangedWeapon(armyEntity.getMainHandStack());
    }

    public boolean hasAmmoEquipped(ArmyEntity armyEntity) {
        return EquipmentUtils.isAmmoFor(armyEntity.getMainHandStack(), armyEntity.getOffHandStack());
    }

    public boolean shouldEquipShield(ArmyEntity armyEntity) {
        if (armyEntity.getOffHandStack().getItem() instanceof ShieldItem) {
            return false;
        }
        //TODO preserve equipment check
        if (!inventorySearcher.hasSlotMatching(armyEntity.getInventory(), stack -> stack.getItem() instanceof ShieldItem)) {
            return false;
        }

        return true;
    }

    public boolean hasUsableRangedWeapon(ArmyEntity armyEntity) {
        //TODO check preserve damaged equipment setting
        Inventory inventory = armyEntity.getInventory();

        Map<Integer, Integer> rangedWeaponSlots = inventorySearcher.getSlotsMatchingByPriority(inventory, EquipmentUtils::isRangedWeapon, commonPriorityMappers.weapon(armyEntity, armyEntity.getTarget()));
        for (int rangedWeaponSlot : rangedWeaponSlots.keySet()) {
            ItemStack weapon = inventory.getInvStack(rangedWeaponSlot);
            boolean hasAmmo = hasAmmoForWeapon(inventory, weapon);
            if (hasAmmo) {
                return true;
            }
        }

        return false;
    }

    public boolean hasAmmoForWeapon(Inventory inventory, ItemStack weapon) {
        return inventorySearcher.hasSlotMatching(
            inventory,
            stack -> EquipmentUtils.isAmmoFor(weapon, stack)
        );
    }

    public void equipUsableRangedWeapon(ArmyEntity armyEntity) {
        //TODO check preserve damaged equipment setting
        Inventory inventory = armyEntity.getInventory();

        Map<Integer, Integer> rangedWeaponSlots = inventorySearcher.getSlotsMatchingByPriority(inventory, EquipmentUtils::isRangedWeapon, commonPriorityMappers.weapon(armyEntity, armyEntity.getTarget()));
        for (int rangedWeaponSlot : rangedWeaponSlots.keySet()) {
            ItemStack weapon = inventory.getInvStack(rangedWeaponSlot);
            boolean hasAmmo = hasAmmoForWeapon(inventory, weapon);
            if (hasAmmo) {
                ItemStack newWeapon = inventory.removeInvStack(rangedWeaponSlot);
                ItemStack oldWeapon = armyEntity.getMainHandStack().copy();
                armyEntity.equipStack(EquipmentSlot.MAINHAND, newWeapon);
                if (!armyEntity.giveItemStack(oldWeapon)) {
                    this.itemDropper.dropItem(oldWeapon, armyEntity);
                }
                return;
            }
        }
    }

    public void equipUsableAmmo(ArmyEntity armyEntity) {
        //TODO check preserve damaged equipment setting
        Inventory inventory = armyEntity.getInventory();

        ItemStack weapon = armyEntity.getMainHandStack();
        Map<Integer, Integer> ammoPriority = inventorySearcher.getSlotsMatchingByPriority(
            inventory,
            stack -> EquipmentUtils.isAmmoFor(weapon, stack),
            commonPriorityMappers.ammo(weapon)
        );
        int oldAmmoSlot = ammoPriority.keySet().toArray(new Integer[0])[0];
        ItemStack newAmmo = inventory.removeInvStack(oldAmmoSlot);
        ItemStack oldOffHandStack = armyEntity.getOffHandStack().copy();
        armyEntity.equipStack(EquipmentSlot.OFFHAND, newAmmo);
        if (!armyEntity.giveItemStack(oldOffHandStack)) {
            this.itemDropper.dropItem(oldOffHandStack, armyEntity);
        }
    }

    public void equipMeleeWeapon(ArmyEntity armyEntity) {
        //TODO check preserve damaged equipment setting
        Inventory inventory = armyEntity.getInventory();

        Map<Integer, Integer> meleePriority = inventorySearcher.getSlotsMatchingByPriority(inventory, EquipmentUtils::isMeleeWeapon, commonPriorityMappers.weapon(armyEntity, armyEntity.getTarget()));
        ItemStack newWeapon = ItemStack.EMPTY;
        if (!meleePriority.isEmpty()) {
            int oldWeaponSlot = meleePriority.keySet().toArray(new Integer[0])[0];
            newWeapon = inventory.removeInvStack(oldWeaponSlot);
        }
        ItemStack oldMainHandStack = armyEntity.getMainHandStack().copy();
        armyEntity.equipStack(EquipmentSlot.MAINHAND, newWeapon);
        if (!armyEntity.giveItemStack(oldMainHandStack)) {
            this.itemDropper.dropItem(oldMainHandStack, armyEntity);
        }
        boolean rangedWeaponWentBackIntoMainHand = newWeapon.isEmpty() && isUsingRanged(armyEntity);
        if (rangedWeaponWentBackIntoMainHand) {
            oldMainHandStack = armyEntity.getMainHandStack().copy();
            armyEntity.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            //TODO it's a stopgap, since they'll likely pick it back up a few seconds later, but better than nothing.
            itemDropper.throwItem(oldMainHandStack, armyEntity);
        }
    }

    public void equipUsableShield(ArmyEntity armyEntity) {
        //TODO check preserve damaged equipment setting
        Inventory inventory = armyEntity.getInventory();

        Map<Integer, Integer> shieldPriority = inventorySearcher.getSlotsMatchingByPriority(
            inventory,
            stack -> stack.getItem() instanceof ShieldItem,
            stack -> stack.getMaxDamage() - stack.getDamage()
        );
        int oldShieldSlot = shieldPriority.keySet().toArray(new Integer[0])[0];
        ItemStack newShield = inventory.removeInvStack(oldShieldSlot);
        ItemStack oldOffHandStack = armyEntity.getOffHandStack().copy();
        armyEntity.equipStack(EquipmentSlot.OFFHAND, newShield);
        if (!armyEntity.giveItemStack(oldOffHandStack)) {
            this.itemDropper.dropItem(oldOffHandStack, armyEntity);
        }
    }
}
