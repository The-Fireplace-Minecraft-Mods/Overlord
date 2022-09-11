package dev.the_fireplace.overlord.util;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public final class EquipmentUtils
{
    public static boolean isMeleeWeapon(ItemStack stack) {
        return (stack.getAttributeModifiers(EquipmentSlot.MAINHAND).containsKey(Attributes.ATTACK_DAMAGE) || getEnchantmentDamageModifier(stack) > 0) && !isRangedWeapon(stack);
    }

    private static float getEnchantmentDamageModifier(ItemStack stack) {
        return EnchantmentHelper.getDamageBonus(stack, MobType.UNDEFINED);
    }

    public static boolean isArmor(ItemStack stack) {
        return Mob.getEquipmentSlotForItem(stack).getType().equals(EquipmentSlot.Type.ARMOR);
    }

    public static boolean isRangedWeapon(ItemStack stack) {
        return stack.getItem() instanceof ProjectileWeaponItem;
    }

    public static boolean isAmmoFor(ItemStack weapon, ItemStack testItem) {
        //TODO proper ammo/AI registry for easy mod support
        return (weapon.getItem() instanceof CrossbowItem && ProjectileWeaponItem.ARROW_OR_FIREWORK.test(testItem))
            || (weapon.getItem() instanceof BowItem && ProjectileWeaponItem.ARROW_ONLY.test(testItem));
    }

    public static boolean requiresAmmo(ItemStack weapon) {
        if (weapon.getItem() instanceof BowItem && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, weapon) > 0) {
            return false;
        }

        return isRangedWeapon(weapon);
    }
}
