package dev.the_fireplace.overlord.impl.inventory;

import com.google.common.collect.Sets;
import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.domain.inventory.CommonPriorityMappers;
import dev.the_fireplace.overlord.util.EquipmentUtils;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

@Singleton
@Implementation
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
                    for (EntityAttributeModifier modifier : armorMods) {
                        switch (modifier.getOperation()) {
                            case ADDITION:
                            case MULTIPLY_BASE:
                                totalArmorValue += modifier.getAmount();
                            case MULTIPLY_TOTAL:
                                totalArmorValue *= modifier.getAmount();
                        }
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
    public ToIntFunction<ItemStack> weapon(LivingEntity source, @Nullable LivingEntity target) {
        return stack -> {
            double damage = 0.5;
            Collection<EntityAttributeModifier> attributeModifiers = stack.getAttributeModifiers(EquipmentSlot.MAINHAND).get(EntityAttributes.ATTACK_DAMAGE.getId());
            for (EntityAttributeModifier modifier : attributeModifiers) {
                switch (modifier.getOperation()) {
                    case ADDITION:
                        damage += modifier.getAmount();
                    case MULTIPLY_BASE:
                        damage += 0.5 * modifier.getAmount();
                    case MULTIPLY_TOTAL:
                        damage *= modifier.getAmount();
                }
            }
            damage += EnchantmentHelper.getAttackDamage(stack, target != null ? target.getGroup() : EntityGroup.DEFAULT);
            damage -= 0.5;
            if ((int) damage > 0) {
                return (int) damage;
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
