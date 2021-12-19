package dev.the_fireplace.overlord.advancement;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.entity.SkeletonInventory;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import javax.annotation.Nullable;
import java.util.List;

public class SkeletonInventoryChangedCriterion extends AbstractCriterion<SkeletonInventoryChangedCriterion.Conditions>
{
    static final Identifier ID = new Identifier(Overlord.MODID, "skeleton_inventory_changed");

    public SkeletonInventoryChangedCriterion() {
    }

    public Identifier getId() {
        return ID;
    }

    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer) {
        JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "slots", new JsonObject());
        NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject2.get("occupied"));
        NumberRange.IntRange intRange2 = NumberRange.IntRange.fromJson(jsonObject2.get("full"));
        NumberRange.IntRange intRange3 = NumberRange.IntRange.fromJson(jsonObject2.get("empty"));
        EquipmentSlotItemPredicate[] itemPredicates = EquipmentSlotItemPredicate.deserializeAll(jsonObject.get("items"));
        return new Conditions(extended, intRange, intRange2, intRange3, itemPredicates);
    }

    public void trigger(ServerPlayerEntity player, SkeletonInventory inventory, ItemStack stack, @Nullable EquipmentSlot slot) {
        int i = 0;
        int j = 0;
        int k = 0;

        for (int l = 0; l < inventory.size(); ++l) {
            ItemStack itemStack = inventory.getStack(l);
            if (itemStack.isEmpty()) {
                ++j;
            } else {
                ++k;
                if (itemStack.getCount() >= itemStack.getMaxCount()) {
                    ++i;
                }
            }
        }

        this.trigger(player, inventory, stack, i, j, k, slot);
    }

    private void trigger(ServerPlayerEntity player, SkeletonInventory inventory, ItemStack stack, int full, int empty, int occupied, @Nullable EquipmentSlot equipmentSlot) {
        this.trigger(player, (conditions) -> conditions.matches(inventory, stack, full, empty, occupied, equipmentSlot));
    }

    public static class Conditions extends AbstractCriterionConditions
    {
        private final NumberRange.IntRange occupied;
        private final NumberRange.IntRange full;
        private final NumberRange.IntRange empty;
        private final EquipmentSlotItemPredicate[] itemPredicates;

        public Conditions(EntityPredicate.Extended player, NumberRange.IntRange occupied, NumberRange.IntRange full, NumberRange.IntRange empty, EquipmentSlotItemPredicate[] itemPredicates) {
            super(ID, player);
            this.occupied = occupied;
            this.full = full;
            this.empty = empty;
            this.itemPredicates = itemPredicates;
        }

        public static Conditions items(EquipmentSlotItemPredicate... items) {
            return new Conditions(EntityPredicate.Extended.EMPTY, NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, items);
        }

        public static Conditions items(ItemConvertible... items) {
            EquipmentSlotItemPredicate[] itemPredicates = new EquipmentSlotItemPredicate[items.length];

            for (int i = 0; i < items.length; ++i) {
                itemPredicates[i] = new EquipmentSlotItemPredicate(null, ImmutableSet.of(items[i].asItem()), NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, EnchantmentPredicate.ARRAY_OF_ANY, EnchantmentPredicate.ARRAY_OF_ANY, null, NbtPredicate.ANY, null);
            }

            return items(itemPredicates);
        }

        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            if (!this.occupied.isDummy() || !this.full.isDummy() || !this.empty.isDummy()) {
                JsonObject jsonObject2 = new JsonObject();
                jsonObject2.add("occupied", this.occupied.toJson());
                jsonObject2.add("full", this.full.toJson());
                jsonObject2.add("empty", this.empty.toJson());
                jsonObject.add("slots", jsonObject2);
            }

            if (this.itemPredicates.length > 0) {
                JsonArray jsonArray = new JsonArray();

                for (ItemPredicate itemPredicate : this.itemPredicates) {
                    jsonArray.add(itemPredicate.toJson());
                }

                jsonObject.add("items", jsonArray);
            }

            return jsonObject;
        }

        public boolean matches(SkeletonInventory inventory, ItemStack stack, int full, int empty, int occupied, @Nullable EquipmentSlot equipmentSlot) {
            if (!this.full.test(full)) {
                return false;
            } else if (!this.empty.test(empty)) {
                return false;
            } else if (!this.occupied.test(occupied)) {
                return false;
            }
            int itemPredicateCount = this.itemPredicates.length;
            if (itemPredicateCount == 0) {
                return true;
            } else if (itemPredicateCount != 1) {
                List<EquipmentSlotItemPredicate> remainingPredicates = new ObjectArrayList<>(this.itemPredicates);
                int inventorySlotCount = inventory.size();

                for (int slotIndex = 0; slotIndex < inventorySlotCount; ++slotIndex) {
                    if (remainingPredicates.isEmpty()) {
                        return true;
                    }
                    EquipmentSlot equipmentSlotByIndex = inventory.getEquipmentTypeByIndex(slotIndex);

                    ItemStack itemStack = inventory.getStack(slotIndex);
                    if (!itemStack.isEmpty()) {
                        remainingPredicates.removeIf((itemPredicate) -> itemPredicate.test(itemStack, equipmentSlotByIndex));
                    }
                }

                return remainingPredicates.isEmpty();
            } else {
                return !stack.isEmpty() && this.itemPredicates[0].test(stack, equipmentSlot);
            }
        }
    }
}
