package dev.the_fireplace.overlord.util;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;

public final class EquipmentUtils
{
    public static boolean isMeleeWeapon(ItemStack stack) {
        return (stack.getAttributeModifiers(EquipmentSlot.MAINHAND).containsKey(EntityAttributes.ATTACK_DAMAGE.getId()) || getEnchantmentDamageModifier(stack) > 0) && !isRangedWeapon(stack);
    }

    private static float getEnchantmentDamageModifier(ItemStack stack) {
        return EnchantmentHelper.getAttackDamage(stack, EntityGroup.DEFAULT);
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

    public static boolean requiresAmmo(ItemStack weapon) {
        if (weapon.getItem() instanceof BowItem && EnchantmentHelper.getLevel(Enchantments.INFINITY, weapon) > 0) {
            return false;
        }

        return isRangedWeapon(weapon);
    }
}
