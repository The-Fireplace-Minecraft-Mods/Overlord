package dev.the_fireplace.overlord.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.api.internal.ThrowableRegistry;
import dev.the_fireplace.overlord.api.mechanic.Tombstone;
import dev.the_fireplace.overlord.entity.OwnedSkeletonEntity;
import dev.the_fireplace.overlord.tags.OverlordItemTags;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.*;
import net.minecraft.text.LiteralText;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.*;
import java.util.stream.Collectors;

public class SkeletonBuilder {
    public static final int REQUIRED_BONE_COUNT = 64;
    public static final int REQUIRED_MILK_COUNT = 2;
    public static final int REQUIRED_MUSCLE_COUNT = 32;
    public static final int REQUIRED_SKIN_COUNT = 32;

    public static boolean hasEssentialContents(Inventory casket) {
        int boneCount = 0, milkCount = 0;
        for(int slot=0;slot<casket.getInvSize();slot++) {
            ItemStack stack = casket.getInvStack(slot);
            if(stack.isEmpty())
                continue;
            if(stack.getItem().equals(Items.BONE))
                boneCount += stack.getCount();
            else if(stack.getItem().equals(Items.MILK_BUCKET))
                milkCount += stack.getCount();
        }
        return boneCount >= REQUIRED_BONE_COUNT && milkCount >= REQUIRED_MILK_COUNT;
    }

    public static void removeEssentialContents(Inventory casket) {
        int boneCount = REQUIRED_BONE_COUNT, milkCount = REQUIRED_MILK_COUNT;
        for(int slot=0;slot<casket.getInvSize() && (boneCount > 0 || milkCount > 0);slot++) {
            ItemStack stack = casket.getInvStack(slot);
            if(stack.isEmpty())
                continue;
            if(stack.getItem().equals(Items.BONE) && boneCount > 0)
                boneCount = reduceStack(casket, slot, stack, boneCount);
            else if(stack.getItem().equals(Items.MILK_BUCKET) && milkCount > 0)
                milkCount = reduceStack(casket, slot, stack, milkCount);
        }
    }

    public static boolean hasMuscles(Inventory casket) {
        int muscleCount = 0;
        for(int slot=0;slot<casket.getInvSize();slot++) {
            ItemStack stack = casket.getInvStack(slot);
            if(stack.isEmpty())
                continue;
            if(stack.getItem().isIn(OverlordItemTags.MUSCLE_MEAT))
                muscleCount += stack.getCount();
        }
        return muscleCount >= REQUIRED_MUSCLE_COUNT;
    }

    public static void removeMuscles(Inventory casket) {
        int muscleCount = REQUIRED_MUSCLE_COUNT;
        for(int slot=0;slot<casket.getInvSize() && muscleCount > 0;slot++) {
            ItemStack stack = casket.getInvStack(slot);
            if(stack.isEmpty())
                continue;
            if(stack.getItem().isIn(OverlordItemTags.MUSCLE_MEAT))
                muscleCount = reduceStack(casket, slot, stack, muscleCount);
        }
    }

    public static boolean hasSkin(Inventory casket) {
        int skinCount = 0;
        for(int slot=0;slot<casket.getInvSize();slot++) {
            ItemStack stack = casket.getInvStack(slot);
            if(stack.isEmpty())
                continue;
            if(stack.getItem().isIn(OverlordItemTags.FLESH))
                skinCount += stack.getCount();
        }
        return skinCount >= REQUIRED_SKIN_COUNT;
    }

    public static void removeSkin(Inventory casket) {
        int skinCount = REQUIRED_SKIN_COUNT;
        for(int slot=0;slot<casket.getInvSize() && skinCount > 0;slot++) {
            ItemStack stack = casket.getInvStack(slot);
            if(stack.isEmpty())
                continue;
            if(stack.getItem().isIn(OverlordItemTags.FLESH))
                skinCount = reduceStack(casket, slot, stack, skinCount);
        }
    }

    public static void findAndEquipArmor(OwnedSkeletonEntity entity, Inventory casket) {
        Map<Integer, ItemStack> armorSlots = Maps.newHashMap();
        for(int slot=0;slot<casket.getInvSize();slot++) {
            ItemStack stack = casket.getInvStack(slot);
            if(stack.isEmpty())
                continue;
            if(isArmor(stack))
                armorSlots.put(slot, stack);
        }
        //Sort armor prioritizing the high defense armors first
        List<Map.Entry<Integer, ItemStack>> m = Lists.newArrayList(armorSlots.entrySet());
        m.sort(Comparator.comparingDouble(o -> getMaxArmorValue(o.getValue())));
        if(m.isEmpty()) //No armor, so nothing to do after this
            return;
        Map<EquipmentSlot, Boolean> equipped = Maps.newHashMap();
        equipped.put(EquipmentSlot.HEAD, false);
        equipped.put(EquipmentSlot.CHEST, false);
        equipped.put(EquipmentSlot.LEGS, false);
        equipped.put(EquipmentSlot.FEET, false);
        for(Map.Entry<Integer, ItemStack> slotEntry: armorSlots.entrySet()) {
            for(Map.Entry<EquipmentSlot, Boolean> entry: Sets.newHashSet(equipped.entrySet()))
                if(!entry.getValue() && MobEntity.getPreferredEquipmentSlot(slotEntry.getValue()).equals(entry.getKey())) {
                    entity.equipStack(entry.getKey(), slotEntry.getValue());
                    casket.setInvStack(slotEntry.getKey(), ItemStack.EMPTY);
                    equipped.put(entry.getKey(), true);
                }
            //Break if skeleton is fully armored.
            if(!equipped.containsValue(false))
                break;
        }
    }

    public static void gatherWeapons(OwnedSkeletonEntity entity, Inventory casket) {
        Map<Integer, ItemStack> weaponSlots = Maps.newHashMap();
        boolean equippedOffhand = false;
        boolean hasBow = false;
        boolean hasCrossbow = false;
        for(int slot=0;slot<casket.getInvSize();slot++) {
            ItemStack stack = casket.getInvStack(slot);
            if(stack.isEmpty())
                continue;
            if(isMeleeWeapon(stack))
                weaponSlots.put(slot, stack);
            else if(!equippedOffhand && stack.getItem() instanceof ShieldItem) {
                entity.equipStack(EquipmentSlot.OFFHAND, casket.removeInvStack(slot));
                equippedOffhand = true;
            } else if(!equippedOffhand && isRangedWeapon(stack)) {
                entity.equipStack(EquipmentSlot.OFFHAND, casket.removeInvStack(slot));
                equippedOffhand = true;
                if(stack.getItem() instanceof CrossbowItem)
                    hasCrossbow = true;
                else
                    hasBow = true;
            }
        }
        //Figure out which weapons deal the most damage and collect those first
        List<Map.Entry<Integer, ItemStack>> m = Lists.newArrayList(weaponSlots.entrySet());
        m.sort(Comparator.comparingDouble(o -> EnchantmentHelper.getAttackDamage(o.getValue(), EntityGroup.DEFAULT)));
        if(!m.isEmpty()) {
            //TODO Don't log, this is just to find out which end has the strongest weapon
            Overlord.LOGGER.info(m.get(0).getValue().toString());
            Overlord.LOGGER.info(m.get(m.size() - 1).getValue().toString());
            entity.equipStack(EquipmentSlot.MAINHAND, casket.removeInvStack(m.get(0).getKey()));
        }
        //Collect weapons, tools
        for(int slot=0;slot<casket.getInvSize();slot++) {
            ItemStack stack = casket.getInvStack(slot);
            if(stack.isEmpty())
                continue;
            if(isMeleeWeapon(stack)) {
                entity.getInventory().insertStack(casket.getInvStack(slot));
            } else if(isRangedWeapon(stack)) {
                entity.getInventory().insertStack(casket.getInvStack(slot));
                if(stack.getItem() instanceof CrossbowItem)
                    hasCrossbow = true;
                else
                    hasBow = true;
            }
        }
        //Collect ammo
        if(hasCrossbow || hasBow) {
            for(int slot=0;slot<casket.getInvSize();slot++) {
                ItemStack stack = casket.getInvStack(slot);
                if(stack.isEmpty())
                    continue;
                if(isAmmo(stack, hasCrossbow))
                    entity.getInventory().insertStack(casket.getInvStack(slot));
            }
        }
        //Collect throwables
        for (int slot=0;slot<casket.getInvSize();slot++) {
            ItemStack stack = casket.getInvStack(slot);
            if (stack.isEmpty())
                continue;
            if (ThrowableRegistry.getInstance().isThrowable(Registry.ITEM.getId(stack.getItem())))
                entity.getInventory().insertStack(casket.getInvStack(slot));
        }
    }

    public static void gatherExtraArmor(OwnedSkeletonEntity entity, Inventory casket) {
        Map<Integer, ItemStack> armorSlots = Maps.newHashMap();
        for(int slot=0;slot<casket.getInvSize();slot++) {
            ItemStack stack = casket.getInvStack(slot);
            if(stack.isEmpty())
                continue;
            if(isArmor(stack) || stack.getItem() instanceof ShieldItem)
                armorSlots.put(slot, stack);
        }
        //Sort armor prioritizing the high defense armors first
        List<Map.Entry<Integer, ItemStack>> m = Lists.newArrayList(armorSlots.entrySet());
        m.sort(Comparator.comparingDouble(o -> getMaxArmorValue(o.getValue())));
        //Collect armor
        for(int slot: armorSlots.keySet())
            entity.getInventory().insertStack(casket.getInvStack(slot));
    }



    public static boolean isMeleeWeapon(ItemStack stack) {
        return EnchantmentHelper.getAttackDamage(stack, EntityGroup.DEFAULT) > 0;
    }

    public static boolean isArmor(ItemStack stack) {
        return MobEntity.getPreferredEquipmentSlot(stack).getType().equals(EquipmentSlot.Type.ARMOR);
    }

    public static double getMaxArmorValue(ItemStack stack) {
        if(stack.getItem() instanceof ShieldItem) {
            //Default armor value of a diamond chestplate + toughness + 1, for the purposes of collecting armor we value shields above most armor because of their ability to block damage.
            return 11;
        } else {
            double max = 0;
            for(EquipmentSlot slot: Sets.newHashSet(EquipmentSlot.values()).stream().filter(s -> s.getType().equals(EquipmentSlot.Type.HAND)).collect(Collectors.toSet())) {
                Collection<EntityAttributeModifier> armorMods = stack.getAttributeModifiers(slot).get(EntityAttributes.ARMOR.getId());
                armorMods.addAll(stack.getAttributeModifiers(slot).get(EntityAttributes.ARMOR_TOUGHNESS.getId()));
                double totalArmorValue = 0;
                for(EntityAttributeModifier mod: armorMods)
                    totalArmorValue += mod.getAmount();
                if(totalArmorValue > max)
                    max = totalArmorValue;
            }
            return max;
        }
    }

    public static boolean isRangedWeapon(ItemStack stack) {
        return stack.getItem() instanceof RangedWeaponItem;
    }

    public static boolean isAmmo(ItemStack stack, boolean isCrossbow) {
        return (isCrossbow && RangedWeaponItem.CROSSBOW_HELD_PROJECTILES.test(stack)) || RangedWeaponItem.BOW_PROJECTILES.test(stack);
    }

    public static OwnedSkeletonEntity build(Inventory casket, World world, Tombstone tombstone) {
        OwnedSkeletonEntity entity = OwnedSkeletonEntity.create(world, tombstone.getOwner());
        removeEssentialContents(casket);
        boolean hasSkin = hasSkin(casket);
        if(hasMuscles(casket)) {
            entity.setHasMuscles(true);
            removeMuscles(casket);
        }
        if(hasSkin) {
            entity.setHasSkin(true);
            removeSkin(casket);
        }
        if(tombstone.getNameText() != null) {
            if(hasSkin) {
                GameProfile profile = Objects.requireNonNull(world.getServer()).getUserCache().findByName(tombstone.getNameText());
                if (profile != null)
                    entity.setSkinsuit(profile.getId());
            }
            entity.setCustomName(new LiteralText(tombstone.getNameText()));
        }
        findAndEquipArmor(entity, casket);
        gatherWeapons(entity, casket);
        gatherExtraArmor(entity, casket);
        //TODO Augments?
        return entity;
    }

    public static int reduceStack(Inventory inv, int slot, ItemStack stack, int amount) {
        if(stack.getCount() > amount) {
            stack.setCount(stack.getCount() - amount);
            return 0;
        } else {
            inv.setInvStack(slot, stack.getItem() instanceof BucketItem ? new ItemStack(Items.BUCKET) : ItemStack.EMPTY);
            return amount - stack.getCount();
        }
    }
}
