package dev.the_fireplace.overlord.advancement;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.entity.SkeletonInventory;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;
import java.util.List;

public class SkeletonInventoryChangedCriterion extends SimpleCriterionTrigger<SkeletonInventoryChangedCriterion.Conditions>
{
    static final ResourceLocation ID = new ResourceLocation(OverlordConstants.MODID, "skeleton_inventory_changed");

    public SkeletonInventoryChangedCriterion() {
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Conditions createInstance(JsonObject jsonObject, JsonDeserializationContext advancementEntityPredicateDeserializer) {
        JsonObject jsonObject2 = GsonHelper.getAsJsonObject(jsonObject, "slots", new JsonObject());
        MinMaxBounds.Ints intRange = MinMaxBounds.Ints.fromJson(jsonObject2.get("occupied"));
        MinMaxBounds.Ints intRange2 = MinMaxBounds.Ints.fromJson(jsonObject2.get("full"));
        MinMaxBounds.Ints intRange3 = MinMaxBounds.Ints.fromJson(jsonObject2.get("empty"));
        EquipmentSlotItemPredicate[] itemPredicates = EquipmentSlotItemPredicate.deserializeAll(jsonObject.get("items"));
        return new Conditions(intRange, intRange2, intRange3, itemPredicates);
    }

    public void trigger(ServerPlayer player, SkeletonInventory inventory, ItemStack stack, @Nullable EquipmentSlot slot) {
        int i = 0;
        int j = 0;
        int k = 0;

        for (int l = 0; l < inventory.getContainerSize(); ++l) {
            ItemStack itemStack = inventory.getItem(l);
            if (itemStack.isEmpty()) {
                ++j;
            } else {
                ++k;
                if (itemStack.getCount() >= itemStack.getMaxStackSize()) {
                    ++i;
                }
            }
        }

        this.trigger(player, inventory, stack, i, j, k, slot);
    }

    private void trigger(ServerPlayer player, SkeletonInventory inventory, ItemStack stack, int full, int empty, int occupied, @Nullable EquipmentSlot equipmentSlot) {
        this.trigger(player.getAdvancements(), (conditions) -> conditions.matches(inventory, stack, full, empty, occupied, equipmentSlot));
    }

    public static class Conditions extends AbstractCriterionTriggerInstance
    {
        private final MinMaxBounds.Ints occupied;
        private final MinMaxBounds.Ints full;
        private final MinMaxBounds.Ints empty;
        private final EquipmentSlotItemPredicate[] itemPredicates;

        public Conditions(MinMaxBounds.Ints occupied, MinMaxBounds.Ints full, MinMaxBounds.Ints empty, EquipmentSlotItemPredicate[] itemPredicates) {
            super(ID);
            this.occupied = occupied;
            this.full = full;
            this.empty = empty;
            this.itemPredicates = itemPredicates;
        }

        public static Conditions items(EquipmentSlotItemPredicate... items) {
            return new Conditions(MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, items);
        }

        public static Conditions items(ItemLike... items) {
            EquipmentSlotItemPredicate[] itemPredicates = new EquipmentSlotItemPredicate[items.length];

            for (int i = 0; i < items.length; ++i) {
                itemPredicates[i] = new EquipmentSlotItemPredicate(null, items[i].asItem(), MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, EnchantmentPredicate.NONE, EnchantmentPredicate.NONE, null, NbtPredicate.ANY, null);
            }

            return items(itemPredicates);
        }

        @Override
        public JsonObject serializeToJson() {
            JsonObject jsonObject = new JsonObject();
            if (!this.occupied.isAny() || !this.full.isAny() || !this.empty.isAny()) {
                JsonObject jsonObject2 = new JsonObject();
                jsonObject2.add("occupied", this.occupied.serializeToJson());
                jsonObject2.add("full", this.full.serializeToJson());
                jsonObject2.add("empty", this.empty.serializeToJson());
                jsonObject.add("slots", jsonObject2);
            }

            if (this.itemPredicates.length > 0) {
                JsonArray jsonArray = new JsonArray();

                for (ItemPredicate itemPredicate : this.itemPredicates) {
                    jsonArray.add(itemPredicate.serializeToJson());
                }

                jsonObject.add("items", jsonArray);
            }

            return jsonObject;
        }

        public boolean matches(SkeletonInventory inventory, ItemStack stack, int full, int empty, int occupied, @Nullable EquipmentSlot equipmentSlot) {
            if (!this.full.matches(full)) {
                return false;
            } else if (!this.empty.matches(empty)) {
                return false;
            } else if (!this.occupied.matches(occupied)) {
                return false;
            }
            int itemPredicateCount = this.itemPredicates.length;
            if (itemPredicateCount == 0) {
                return true;
            } else if (itemPredicateCount != 1) {
                List<EquipmentSlotItemPredicate> remainingPredicates = new ObjectArrayList<>(this.itemPredicates);
                int inventorySlotCount = inventory.getContainerSize();

                for (int slotIndex = 0; slotIndex < inventorySlotCount; ++slotIndex) {
                    if (remainingPredicates.isEmpty()) {
                        return true;
                    }
                    EquipmentSlot equipmentSlotByIndex = inventory.getEquipmentTypeByIndex(slotIndex);

                    ItemStack itemStack = inventory.getItem(slotIndex);
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
