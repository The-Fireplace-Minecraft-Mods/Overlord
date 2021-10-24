package dev.the_fireplace.overlord.util;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;

public final class EquipmentUtils
{
    public static boolean isMeleeWeapon(ItemStack stack) {
        return EnchantmentHelper.getAttackDamage(stack, EntityGroup.DEFAULT) > 0 && !isRangedWeapon(stack);
    }

    public static boolean isArmor(ItemStack stack) {
        return MobEntity.getPreferredEquipmentSlot(stack).getType().equals(EquipmentSlot.Type.ARMOR);
    }

    public static boolean isRangedWeapon(ItemStack stack) {
        return stack.getItem() instanceof RangedWeaponItem;
    }

    public static boolean isAmmoFor(ItemStack weapon, ItemStack testItem) {
        //TODO proper ammo/AI registry for easy mod support
        return (weapon.getItem() instanceof CrossbowItem && RangedWeaponItem.CROSSBOW_HELD_PROJECTILES.test(testItem))
            || (weapon.getItem() instanceof BowItem && RangedWeaponItem.BOW_PROJECTILES.test(testItem));
    }
}
