package dev.the_fireplace.overlord.entity.creation;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.domain.blockentity.Tombstone;
import dev.the_fireplace.overlord.domain.entity.creation.SkeletonBuilder;
import dev.the_fireplace.overlord.domain.entity.creation.SkeletonRecipeRegistry;
import dev.the_fireplace.overlord.domain.inventory.CommonPriorityMappers;
import dev.the_fireplace.overlord.domain.inventory.InventorySearcher;
import dev.the_fireplace.overlord.domain.registry.HeadBlockAugmentRegistry;
import dev.the_fireplace.overlord.entity.OwnedSkeletonEntity;
import dev.the_fireplace.overlord.util.EquipmentUtils;
import dev.the_fireplace.overlord.util.PlayerNameHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Implementation
@Singleton
public final class SkeletonBuilderImpl implements SkeletonBuilder
{
    private final SkeletonRecipeRegistry skeletonRecipeRegistry;
    private final CommonPriorityMappers commonPriorityMappers;
    private final InventorySearcher inventorySearcher;
    private final HeadBlockAugmentRegistry headBlockAugmentRegistry;

    @Inject
    public SkeletonBuilderImpl(
        SkeletonRecipeRegistry skeletonRecipeRegistry,
        CommonPriorityMappers commonPriorityMappers,
        InventorySearcher inventorySearcher,
        HeadBlockAugmentRegistry headBlockAugmentRegistry
    ) {
        this.skeletonRecipeRegistry = skeletonRecipeRegistry;
        this.commonPriorityMappers = commonPriorityMappers;
        this.inventorySearcher = inventorySearcher;
        this.headBlockAugmentRegistry = headBlockAugmentRegistry;
    }

    @Override
    public boolean canBuildWithIngredients(Container inventory) {
        return skeletonRecipeRegistry.getRecipes().stream().anyMatch(recipe -> recipe.hasEssentialContents(inventory));
    }

    @Override
    public OwnedSkeletonEntity build(Container inventory, Level world, Tombstone tombstone) {
        Optional<SkeletonRecipe> recipeOrEmpty = skeletonRecipeRegistry.getRecipes().stream().filter(filterRecipe -> filterRecipe.hasEssentialContents(inventory)).findFirst();
        if (!recipeOrEmpty.isPresent()) {
            return null;
        }
        boolean hasMuscles = false;
        boolean hasSkin = false;
        boolean hasPlayerSkin = false;
        SkeletonRecipe recipe = recipeOrEmpty.get();
        Collection<ItemStack> byproducts = new HashSet<>(recipe.processEssentialIngredients(inventory));
        OwnedSkeletonEntity entity = OwnedSkeletonEntity.create(world, tombstone.getOwner());
        if (recipe.hasMuscleContents(inventory)) {
            byproducts.addAll(recipe.processMuscleIngredients(inventory));
            entity.setHasMuscles(true);
            hasMuscles = true;
        }
        String tombstoneName = tombstone.getNameText().trim();
        if (recipe.hasSkinContents(inventory)) {
            byproducts.addAll(recipe.processSkinIngredients(inventory));
            entity.setHasSkin(true);
            hasSkin = true;
            if (!tombstoneName.isEmpty() && PlayerNameHelper.VALID_NAME_REGEX.matcher(tombstoneName).matches() && recipe.hasPlayerColorContents(inventory)) {
                GameProfileCache.setUsesAuthentication(true);
                GameProfile profile = world.getServer().getProfileCache().get(tombstoneName);
                if (profile != null) {
                    byproducts.addAll(recipe.processPlayerColorIngredients(inventory));
                    entity.setSkinsuit(profile.getId());
                    hasPlayerSkin = true;
                }
            }
        }
        if (!tombstoneName.isEmpty()) {
            entity.setCustomName(Component.nullToEmpty(tombstoneName));
        }
        for (ItemStack byproduct : byproducts) {
            entity.getInventory().insertStack(byproduct);
        }
        int totalMaxHealth = recipe.getTotalMaxHealth(hasSkin, hasMuscles, hasPlayerSkin);
        entity.setMaxHealth(totalMaxHealth);
        findAndEquipArmor(entity, inventory);
        gatherWeapons(entity, inventory);
        // gather augment before armor - no deception from wearing the skeleton skulls instead of using them as augments
        gatherAugment(entity, inventory);
        gatherExtraArmor(entity, inventory);
        entity.setHealth(entity.getMaxHealth());

        return entity;
    }

    private void findAndEquipArmor(OwnedSkeletonEntity entity, Container casket) {
        Map<Integer, ItemStack> armorSlots = Maps.newHashMap();
        for (int slot = 0; slot < casket.getContainerSize(); slot++) {
            ItemStack stack = casket.getItem(slot);
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
                if (!entry.getValue() && Mob.getEquipmentSlotForItem(slotEntry.getValue()).equals(entry.getKey())) {
                    entity.setItemSlot(entry.getKey(), slotEntry.getValue());
                    casket.setItem(slotEntry.getKey(), ItemStack.EMPTY);
                    equipped.put(entry.getKey(), true);
                }
            }
            boolean skeletonIsFullyArmored = !equipped.containsValue(false);
            if (skeletonIsFullyArmored) {
                break;
            }
        }
    }

    private void gatherWeapons(OwnedSkeletonEntity entity, Container casket) {
        Map<Integer, ItemStack> weaponSlots = Maps.newHashMap();
        boolean equippedOffhand = false;
        boolean hasBow = false;
        boolean hasCrossbow = false;
        ItemStack crossbow = null;
        ItemStack bow = null;
        for (int slot = 0; slot < casket.getContainerSize(); slot++) {
            ItemStack stack = casket.getItem(slot);
            if (stack.isEmpty()) {
                continue;
            }
            if (EquipmentUtils.isMeleeWeapon(stack)) {
                weaponSlots.put(slot, stack);
            } else if (!equippedOffhand && stack.getItem() instanceof ShieldItem) {
                entity.setItemSlot(EquipmentSlot.OFFHAND, casket.removeItemNoUpdate(slot));
                equippedOffhand = true;
            } else if (!equippedOffhand && EquipmentUtils.isRangedWeapon(stack)) {
                entity.setItemSlot(EquipmentSlot.OFFHAND, casket.removeItemNoUpdate(slot));
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
            entity.setItemSlot(EquipmentSlot.MAINHAND, casket.removeItemNoUpdate(weaponWeights.get(0).getKey()));
        }
        //Collect weapons, tools
        for (int slot = 0; slot < casket.getContainerSize(); slot++) {
            ItemStack stack = casket.getItem(slot);
            if (stack.isEmpty()) {
                continue;
            }
            if (EquipmentUtils.isMeleeWeapon(stack)) {
                entity.getInventory().insertStack(casket.getItem(slot));
            } else if (EquipmentUtils.isRangedWeapon(stack)) {
                entity.getInventory().insertStack(casket.getItem(slot));
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
            for (int slot = 0; slot < casket.getContainerSize(); slot++) {
                ItemStack stack = casket.getItem(slot);
                if (stack.isEmpty()) {
                    continue;
                }
                //TODO ammo registry
                if (crossbow != null && EquipmentUtils.isAmmoFor(crossbow, stack)) {
                    entity.getInventory().insertStack(casket.getItem(slot));
                } else if (bow != null && EquipmentUtils.isAmmoFor(bow, stack)) {
                    entity.getInventory().insertStack(casket.getItem(slot));
                }
            }
        }
    }

    private void gatherExtraArmor(OwnedSkeletonEntity entity, Container casket) {
        Map<Integer, ItemStack> armorSlots = Maps.newHashMap();
        for (int slot = 0; slot < casket.getContainerSize(); slot++) {
            ItemStack stack = casket.getItem(slot);
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
            entity.getInventory().insertStack(casket.getItem(slot));
        }
    }

    private void gatherAugment(OwnedSkeletonEntity entity, Container casket) {
        Integer slot = inventorySearcher.getFirstSlotMatching(casket, stack -> {
            Item item = stack.getItem();
            return item instanceof BlockItem && headBlockAugmentRegistry.has(((BlockItem) item).getBlock());
        });
        if (slot == null) {
            return;
        }
        ItemStack stack = casket.getItem(slot);
        ItemStack augmentStack = stack.split(1);
        if (stack.isEmpty()) {
            casket.setItem(slot, ItemStack.EMPTY);
        }
        entity.setAugmentBlock(augmentStack);
    }
}
