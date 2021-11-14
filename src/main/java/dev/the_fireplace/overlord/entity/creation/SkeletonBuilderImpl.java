package dev.the_fireplace.overlord.entity.creation;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import dev.the_fireplace.annotateddi.api.DIContainer;
import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.domain.blockentity.Tombstone;
import dev.the_fireplace.overlord.domain.entity.creation.SkeletonBuilder;
import dev.the_fireplace.overlord.domain.inventory.CommonPriorityMappers;
import dev.the_fireplace.overlord.domain.inventory.InventorySearcher;
import dev.the_fireplace.overlord.domain.registry.HeadBlockAugmentRegistry;
import dev.the_fireplace.overlord.entity.OwnedSkeletonEntity;
import dev.the_fireplace.overlord.util.EquipmentUtils;
import dev.the_fireplace.overlord.util.PlayerNameHelper;
import io.netty.util.internal.ConcurrentSet;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.*;
import net.minecraft.text.Text;
import net.minecraft.util.UserCache;
import net.minecraft.world.World;

import javax.inject.Singleton;
import java.util.*;

@Implementation
@Singleton
public class SkeletonBuilderImpl implements SkeletonBuilder
{
    private final Collection<SkeletonRecipe> skeletonRecipes = new ConcurrentSet<>();

    @Override
    public boolean canBuildWithIngredients(Inventory inventory) {
        return skeletonRecipes.stream().anyMatch(recipe -> recipe.hasEssentialContents(inventory));
    }

    @Override
    public OwnedSkeletonEntity build(Inventory inventory, World world, Tombstone tombstone) {
        Optional<SkeletonRecipe> recipeOrEmpty = skeletonRecipes.stream().filter(filterRecipe -> filterRecipe.hasEssentialContents(inventory)).findFirst();
        if (recipeOrEmpty.isEmpty()) {
            return null;
        }
        SkeletonRecipe recipe = recipeOrEmpty.get();
        Collection<ItemStack> byproducts = new HashSet<>(recipe.processEssentialIngredients(inventory));
        OwnedSkeletonEntity entity = OwnedSkeletonEntity.create(world, tombstone.getOwner());
        if (recipe.hasMuscleContents(inventory)) {
            byproducts.addAll(recipe.processMuscleIngredients(inventory));
            entity.setHasMuscles(true);
        }
        String tombstoneName = tombstone.getNameText().trim();
        if (recipe.hasSkinContents(inventory)) {
            byproducts.addAll(recipe.processSkinIngredients(inventory));
            entity.setHasSkin(true);
            if (!tombstoneName.isEmpty() && PlayerNameHelper.VALID_NAME_REGEX.matcher(tombstoneName).matches() && recipe.hasPlayerColorContents(inventory)) {
                UserCache.setUseRemote(true);
                Optional<GameProfile> profile = world.getServer().getUserCache().findByName(tombstoneName);
                if (profile.isPresent()) {
                    byproducts.addAll(recipe.processPlayerColorIngredients(inventory));
                    entity.setSkinsuit(profile.get().getId());
                }
            }
        }
        if (!tombstoneName.isEmpty()) {
            entity.setCustomName(Text.of(tombstoneName));
        }
        for (ItemStack byproduct : byproducts) {
            entity.getInventory().insertStack(byproduct);
        }
        findAndEquipArmor(entity, inventory);
        gatherWeapons(entity, inventory);
        // gather augment before armor - no deception from wearing the skeleton skulls instead of using them as augments
        gatherAugment(entity, inventory);
        gatherExtraArmor(entity, inventory);

        return entity;
    }

    public void setSkeletonRecipes(Collection<SkeletonRecipe> skeletonRecipes) {
        this.skeletonRecipes.clear();
        this.skeletonRecipes.addAll(skeletonRecipes);
    }

    private void findAndEquipArmor(OwnedSkeletonEntity entity, Inventory casket) {
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

    private void gatherWeapons(OwnedSkeletonEntity entity, Inventory casket) {
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

    private void gatherExtraArmor(OwnedSkeletonEntity entity, Inventory casket) {
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

    private void gatherAugment(OwnedSkeletonEntity entity, Inventory casket) {
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

    private int reduceStack(Inventory inv, int slot, ItemStack stack, int amount) {
        if (stack.getCount() > amount) {
            stack.setCount(stack.getCount() - amount);
            return 0;
        } else {
            inv.setStack(slot, stack.getItem() instanceof BucketItem ? new ItemStack(Items.BUCKET) : ItemStack.EMPTY);
            return amount - stack.getCount();
        }
    }
}
