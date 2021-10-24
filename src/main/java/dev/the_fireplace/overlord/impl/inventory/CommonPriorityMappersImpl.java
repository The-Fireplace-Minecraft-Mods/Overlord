package dev.the_fireplace.overlord.impl.inventory;

import com.google.common.collect.Sets;
import dev.the_fireplace.overlord.domain.inventory.CommonPriorityMappers;
import dev.the_fireplace.overlord.util.EquipmentUtils;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;

import java.util.Collection;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

public class CommonPriorityMappersImpl implements CommonPriorityMappers
{
    @Override
    public ToIntFunction<ItemStack> armor() {
        return stack -> {
            if (stack.getItem() instanceof ShieldItem) {
                //Default armor value of a diamond chestplate + toughness + 1, for the purposes of collecting armor we value shields above most armor because of their ability to block damage.
                return 11;
            } else {
                double max = 0;
                for (EquipmentSlot slot : Sets.newHashSet(EquipmentSlot.values()).stream().filter(s -> s.getType().equals(EquipmentSlot.Type.HAND)).collect(Collectors.toSet())) {
                    Collection<EntityAttributeModifier> armorMods = stack.getAttributeModifiers(slot).get(EntityAttributes.ARMOR.getId());
                    armorMods.addAll(stack.getAttributeModifiers(slot).get(EntityAttributes.ARMOR_TOUGHNESS.getId()));
                    double totalArmorValue = 0;
                    for (EntityAttributeModifier mod : armorMods) {
                        totalArmorValue += mod.getAmount();
                    }
                    if (totalArmorValue > max) {
                        max = totalArmorValue;
                    }
                }
                return (int) max;
            }
        };
    }

    @Override
    public ToIntFunction<ItemStack> slotArmor(EquipmentSlot slot) {
        return armor();//TODO properly check the slot
    }

    @Override
    public ToIntFunction<ItemStack> weapon() {
        return stack -> {
            int damage = (int) EnchantmentHelper.getAttackDamage(stack, EntityGroup.DEFAULT);
            if (damage > 0) {
                return damage;
            }
            if (EquipmentUtils.isRangedWeapon(stack)) {
                return 1;
            }

            return -1;
        };
    }

    @Override
    public ToIntFunction<ItemStack> ammo(ItemStack forWeapon) {
        return testStack -> {
            //TODO priority to more harmful arrows?
            return EquipmentUtils.isAmmoFor(forWeapon, testStack) ? 1 : -1;
        };
    }

    @Override
    public ToIntFunction<ItemStack> throwable(Entity target) {
        return testStack -> 0;//TODO once throwables are figured out, maybe sort by damage?
    }
}
