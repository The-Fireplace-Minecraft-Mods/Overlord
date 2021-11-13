package dev.the_fireplace.overlord.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import dev.the_fireplace.annotateddi.api.DIContainer;
import dev.the_fireplace.overlord.domain.blockentity.Tombstone;
import dev.the_fireplace.overlord.domain.inventory.CommonPriorityMappers;
import dev.the_fireplace.overlord.domain.inventory.InventorySearcher;
import dev.the_fireplace.overlord.domain.registry.HeadBlockAugmentRegistry;
import dev.the_fireplace.overlord.entity.OwnedSkeletonEntity;
import dev.the_fireplace.overlord.tags.OverlordItemTags;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.*;
import net.minecraft.text.LiteralText;
import net.minecraft.util.UserCache;
import net.minecraft.world.World;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SkeletonBuilder
{
    public static final int REQUIRED_BONE_COUNT = 64;
    public static final int REQUIRED_MILK_COUNT = 2;
    public static final int REQUIRED_MUSCLE_COUNT = 32;
    public static final int REQUIRED_SKIN_COUNT = 32;
    public static final int REQUIRED_DYE_COUNT = 8;

    public static boolean hasEssentialContents(Inventory casket) {
        int boneCount = 0, milkCount = 0;
        for (int slot = 0; slot < casket.size(); slot++) {
            ItemStack stack = casket.getStack(slot);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.getItem().equals(Items.BONE)) {
                boneCount += stack.getCount();
            } else if (stack.getItem().equals(Items.MILK_BUCKET)) {
                milkCount += stack.getCount();
            }
        }
        return boneCount >= REQUIRED_BONE_COUNT && milkCount >= REQUIRED_MILK_COUNT;
    }

    public static void removeEssentialContents(Inventory casket) {
        int boneCount = REQUIRED_BONE_COUNT, milkCount = REQUIRED_MILK_COUNT;
        for (int slot = 0; slot < casket.size() && (boneCount > 0 || milkCount > 0); slot++) {
            ItemStack stack = casket.getStack(slot);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.getItem().equals(Items.BONE) && boneCount > 0) {
                boneCount = reduceStack(casket, slot, stack, boneCount);
            } else if (stack.getItem().equals(Items.MILK_BUCKET) && milkCount > 0) {
                milkCount = reduceStack(casket, slot, stack, milkCount);
            }
        }
    }

    public static boolean hasMuscles(Inventory casket) {
        int muscleCount = 0;
        for (int slot = 0; slot < casket.size(); slot++) {
            ItemStack stack = casket.getStack(slot);
            if (stack.isEmpty()) {
                continue;
            }
            if (OverlordItemTags.MUSCLE_MEAT.contains(stack.getItem())) {
                muscleCount += stack.getCount();
            }
        }
        return muscleCount >= REQUIRED_MUSCLE_COUNT;
    }

    public static void removeMuscles(Inventory casket) {
        int muscleCount = REQUIRED_MUSCLE_COUNT;
        for (int slot = 0; slot < casket.size() && muscleCount > 0; slot++) {
            ItemStack stack = casket.getStack(slot);
            if (stack.isEmpty()) {
                continue;
            }
            if (OverlordItemTags.MUSCLE_MEAT.contains(stack.getItem())) {
                muscleCount = reduceStack(casket, slot, stack, muscleCount);
            }
        }
    }

    public static boolean hasSkin(Inventory casket) {
        int skinCount = 0;
        for (int slot = 0; slot < casket.size(); slot++) {
            ItemStack stack = casket.getStack(slot);
            if (stack.isEmpty()) {
                continue;
            }
            if (OverlordItemTags.FLESH.contains(stack.getItem())) {
                skinCount += stack.getCount();
            }
        }
        return skinCount >= REQUIRED_SKIN_COUNT;
    }

    public static void removeSkin(Inventory casket) {
        int skinCount = REQUIRED_SKIN_COUNT;
        for (int slot = 0; slot < casket.size() && skinCount > 0; slot++) {
            ItemStack stack = casket.getStack(slot);
            if (stack.isEmpty()) {
                continue;
            }
            if (OverlordItemTags.FLESH.contains(stack.getItem())) {
                skinCount = reduceStack(casket, slot, stack, skinCount);
            }
        }
    }

    public static boolean hasDye(Inventory casket) {
        int dyeCount = 0;
        for (int slot = 0; slot < casket.size(); slot++) {
            ItemStack stack = casket.getStack(slot);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.getItem() instanceof DyeItem) {
                dyeCount += stack.getCount();
            }
        }
        return dyeCount >= REQUIRED_DYE_COUNT;
    }

    public static void removeDye(Inventory casket) {
        int dyeCount = REQUIRED_DYE_COUNT;
        for (int slot = 0; slot < casket.size() && dyeCount > 0; slot++) {
            ItemStack stack = casket.getStack(slot);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.getItem() instanceof DyeItem) {
                dyeCount = reduceStack(casket, slot, stack, dyeCount);
            }
        }
    }

    public static void findAndEquipArmor(OwnedSkeletonEntity entity, Inventory casket) {
        CommonPriorityMappers commonPriorityMappers = DIContainer.get().getInstance(CommonPriorityMappers.class);
        Map<Integer, ItemStack> armorSlots = Maps.newHashMap();
        for (int slot = 0; slot < casket.size(); slot++) {
            ItemStack stack = casket.getStack(slot);
            if (stack.isEmpty()) {
                continue;
            }
            if (EquipmentUtils.isArmor(stack)) {
                armorSlots.put(slot, stack);
            }
        }
        //Sort armor prioritizing the high defense armors first
        List<Map.Entry<Integer, ItemStack>> armorByWeight = Lists.newArrayList(armorSlots.entrySet());
        armorByWeight.sort(Comparator.comparingDouble(o -> commonPriorityMappers.armor().applyAsInt(o.getValue())));
        if (armorByWeight.isEmpty()) {
            return;
        }
        Map<EquipmentSlot, Boolean> equipped = Maps.newHashMap();
        equipped.put(EquipmentSlot.HEAD, false);
        equipped.put(EquipmentSlot.CHEST, false);
        equipped.put(EquipmentSlot.LEGS, false);
        equipped.put(EquipmentSlot.FEET, false);
        for (Map.Entry<Integer, ItemStack> slotEntry : armorByWeight) {
            for (Map.Entry<EquipmentSlot, Boolean> entry : Sets.newHashSet(equipped.entrySet())) {
                if (!entry.getValue() && LivingEntity.getPreferredEquipmentSlot(slotEntry.getValue()).equals(entry.getKey())) {
                    entity.equipStack(entry.getKey(), slotEntry.getValue());
                    casket.setStack(slotEntry.getKey(), ItemStack.EMPTY);
                    equipped.put(entry.getKey(), true);
                }
            }
            boolean skeletonIsFullyArmored = !equipped.containsValue(false);
            if (skeletonIsFullyArmored) {
                break;
            }
        }
    }

    public static void gatherWeapons(OwnedSkeletonEntity entity, Inventory casket) {
        CommonPriorityMappers commonPriorityMappers = DIContainer.get().getInstance(CommonPriorityMappers.class);
        Map<Integer, ItemStack> weaponSlots = Maps.newHashMap();
        boolean equippedOffhand = false;
        boolean hasBow = false;
        boolean hasCrossbow = false;
        ItemStack crossbow = null;
        ItemStack bow = null;
        for (int slot = 0; slot < casket.size(); slot++) {
            ItemStack stack = casket.getStack(slot);
            if (stack.isEmpty()) {
                continue;
            }
            if (EquipmentUtils.isMeleeWeapon(stack)) {
                weaponSlots.put(slot, stack);
            } else if (!equippedOffhand && stack.getItem() instanceof ShieldItem) {
                entity.equipStack(EquipmentSlot.OFFHAND, casket.removeStack(slot));
                equippedOffhand = true;
            } else if (!equippedOffhand && EquipmentUtils.isRangedWeapon(stack)) {
                entity.equipStack(EquipmentSlot.OFFHAND, casket.removeStack(slot));
                equippedOffhand = true;
                if (stack.getItem() instanceof CrossbowItem) {
                    hasCrossbow = true;
                } else {
                    hasBow = true;
                }
            }
        }
        //Figure out which weapons deal the most damage and collect those first
        List<Map.Entry<Integer, ItemStack>> weaponWeights = Lists.newArrayList(weaponSlots.entrySet());
        weaponWeights.sort(Comparator.comparingInt(o -> commonPriorityMappers.weapon(entity, entity.getTarget()).applyAsInt(o.getValue())));
        if (!weaponWeights.isEmpty()) {
            entity.equipStack(EquipmentSlot.MAINHAND, casket.removeStack(weaponWeights.get(0).getKey()));
        }
        //Collect weapons, tools
        for (int slot = 0; slot < casket.size(); slot++) {
            ItemStack stack = casket.getStack(slot);
            if (stack.isEmpty()) {
                continue;
            }
            if (EquipmentUtils.isMeleeWeapon(stack)) {
                entity.getInventory().insertStack(casket.getStack(slot));
            } else if (EquipmentUtils.isRangedWeapon(stack)) {
                entity.getInventory().insertStack(casket.getStack(slot));
                if (stack.getItem() instanceof CrossbowItem) {
                    hasCrossbow = true;
                    crossbow = stack;
                } else if (stack.getItem() instanceof BowItem) {
                    hasBow = true;
                    bow = stack;
                }
            }
        }
        //Collect ammo
        if (hasCrossbow || hasBow) {
            for (int slot = 0; slot < casket.size(); slot++) {
                ItemStack stack = casket.getStack(slot);
                if (stack.isEmpty()) {
                    continue;
                }
                //TODO ammo registry
                if (crossbow != null && EquipmentUtils.isAmmoFor(crossbow, stack)) {
                    entity.getInventory().insertStack(casket.getStack(slot));
                } else if (bow != null && EquipmentUtils.isAmmoFor(bow, stack)) {
                    entity.getInventory().insertStack(casket.getStack(slot));
                }
            }
        }
    }

    public static void gatherExtraArmor(OwnedSkeletonEntity entity, Inventory casket) {
        CommonPriorityMappers commonPriorityMappers = DIContainer.get().getInstance(CommonPriorityMappers.class);
        Map<Integer, ItemStack> armorSlots = Maps.newHashMap();
        for (int slot = 0; slot < casket.size(); slot++) {
            ItemStack stack = casket.getStack(slot);
            if (stack.isEmpty()) {
                continue;
            }
            if (EquipmentUtils.isArmor(stack) || stack.getItem() instanceof ShieldItem) {
                armorSlots.put(slot, stack);
            }
        }
        //Sort armor prioritizing the high defense armors first
        List<Map.Entry<Integer, ItemStack>> m = Lists.newArrayList(armorSlots.entrySet());
        m.sort(Comparator.comparingInt(o -> commonPriorityMappers.armor().applyAsInt(o.getValue())));
        //Collect armor
        for (int slot : armorSlots.keySet()) {
            entity.getInventory().insertStack(casket.getStack(slot));
        }
    }

    public static void gatherAugment(OwnedSkeletonEntity entity, Inventory casket) {
        InventorySearcher inventorySearcher = DIContainer.get().getInstance(InventorySearcher.class);
        HeadBlockAugmentRegistry headBlockAugmentRegistry = DIContainer.get().getInstance(HeadBlockAugmentRegistry.class);
        Integer slot = inventorySearcher.getFirstSlotMatching(casket, stack -> {
            Item item = stack.getItem();
            return item instanceof BlockItem && headBlockAugmentRegistry.has(((BlockItem) item).getBlock());
        });
        if (slot == null) {
            return;
        }
        ItemStack stack = casket.getStack(slot);
        ItemStack augmentStack = stack.split(1);
        if (stack.isEmpty()) {
            casket.setStack(slot, ItemStack.EMPTY);
        }
        entity.setAugmentBlock(augmentStack);
    }

    public static OwnedSkeletonEntity build(Inventory casket, World world, Tombstone tombstone) {
        OwnedSkeletonEntity entity = OwnedSkeletonEntity.create(world, tombstone.getOwner());
        removeEssentialContents(casket);
        boolean hasSkin = hasSkin(casket);
        if (hasMuscles(casket)) {
            entity.setHasMuscles(true);
            removeMuscles(casket);
        }
        if (hasSkin) {
            entity.setHasSkin(true);
            removeSkin(casket);
        }
        if (!tombstone.getNameText().isEmpty()) {
            String skinName = tombstone.getNameText().trim();
            if (hasSkin && PlayerNameHelper.VALID_NAME_REGEX.matcher(skinName).matches() && hasDye(casket)) {
                UserCache.setUseRemote(true);
                Optional<GameProfile> profile = world.getServer().getUserCache().findByName(skinName);
                if (profile.isPresent()) {
                    removeDye(casket);
                    entity.setSkinsuit(profile.get().getId());
                }
            }
            entity.setCustomName(new LiteralText(tombstone.getNameText()));
        }
        findAndEquipArmor(entity, casket);
        gatherWeapons(entity, casket);
        // gather augment before armor - no deception from wearing the skeleton skulls instead of using them as augments
        gatherAugment(entity, casket);
        gatherExtraArmor(entity, casket);
        return entity;
    }

    public static int reduceStack(Inventory inv, int slot, ItemStack stack, int amount) {
        if (stack.getCount() > amount) {
            stack.setCount(stack.getCount() - amount);
            return 0;
        } else {
            inv.setStack(slot, stack.getItem() instanceof BucketItem ? new ItemStack(Items.BUCKET) : ItemStack.EMPTY);
            return amount - stack.getCount();
        }
    }
}
