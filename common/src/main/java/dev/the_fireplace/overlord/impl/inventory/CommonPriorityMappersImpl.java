package dev.the_fireplace.overlord.impl.inventory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.domain.inventory.CommonPriorityMappers;
import dev.the_fireplace.overlord.util.EquipmentUtils;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

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
                    Collection<AttributeModifier> armorMods = Lists.newArrayList(stack.getAttributeModifiers(slot).get(SharedMonsterAttributes.ARMOR.getName()));
                    armorMods.addAll(stack.getAttributeModifiers(slot).get(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName()));
                    double totalArmorValue = 0;
                    for (AttributeModifier modifier : armorMods) {
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
            Collection<AttributeModifier> attributeModifiers = stack.getAttributeModifiers(EquipmentSlot.MAINHAND).get(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
            for (AttributeModifier modifier : attributeModifiers) {
                switch (modifier.getOperation()) {
                    case ADDITION:
                        damage += modifier.getAmount();
                    case MULTIPLY_BASE:
                        damage += 0.5 * modifier.getAmount();
                    case MULTIPLY_TOTAL:
                        damage *= modifier.getAmount();
                }
            }
            damage += EnchantmentHelper.getDamageBonus(stack, target != null ? target.getMobType() : MobType.UNDEFINED);
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
}
