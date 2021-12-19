package dev.the_fireplace.overlord.advancement;

import com.google.common.collect.ImmutableSet;
import com.google.gson.*;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class EquipmentSlotItemPredicate extends ItemPredicate
{
    public static final EquipmentSlotItemPredicate ANY = new EquipmentSlotItemPredicate();

    @Nullable
    private final EquipmentSlot slot;

    private EquipmentSlotItemPredicate() {
        super();
        this.slot = null;
    }

    public EquipmentSlotItemPredicate(
        @Nullable TagKey<Item> tag,
        @Nullable Set<Item> items,
        NumberRange.IntRange count,
        NumberRange.IntRange durability,
        EnchantmentPredicate[] enchantments,
        EnchantmentPredicate[] storedEnchantments,
        @Nullable Potion potion,
        NbtPredicate nbt,
        @Nullable EquipmentSlot slot
    ) {
        super(tag, items, count, durability, enchantments, storedEnchantments, potion, nbt);
        this.slot = slot;
    }

    public static EquipmentSlotItemPredicate simple(ItemConvertible item, @Nullable EquipmentSlot slot) {
        return new EquipmentSlotItemPredicate(null, ImmutableSet.of(item.asItem()), NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, EnchantmentPredicate.ARRAY_OF_ANY, EnchantmentPredicate.ARRAY_OF_ANY, null, NbtPredicate.ANY, slot);
    }

    @Override
    public boolean test(ItemStack stack) {
        return slot == null && super.test(stack);
    }

    public boolean test(ItemStack stack, EquipmentSlot slot) {
        return (this.slot == null || this.slot == slot) && super.test(stack);
    }

    @Override
    public JsonElement toJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonElement jsonElement = super.toJson();
        if (jsonElement instanceof JsonObject jsonObject) {
            if (this.slot != null) {
                jsonObject.addProperty("equipmentSlot", this.slot.getName());
            }
            return jsonObject;
        }
        return jsonElement;
    }

    public static EquipmentSlotItemPredicate fromJson(@Nullable JsonElement el) {
        if (el != null && !el.isJsonNull()) {
            JsonObject jsonObject = JsonHelper.asObject(el, "item");
            NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("count"));
            NumberRange.IntRange intRange2 = NumberRange.IntRange.fromJson(jsonObject.get("durability"));
            if (jsonObject.has("data")) {
                throw new JsonParseException("Disallowed data tag found");
            } else {
                NbtPredicate nbtPredicate = NbtPredicate.fromJson(jsonObject.get("nbt"));
                Set<Item> set = null;
                JsonArray jsonArray = JsonHelper.getArray(jsonObject, "items", (JsonArray) null);
                if (jsonArray != null) {
                    ImmutableSet.Builder<Item> builder = ImmutableSet.builder();

                    for (JsonElement jsonElement : jsonArray) {
                        Identifier identifier = new Identifier(JsonHelper.asString(jsonElement, "item"));
                        builder.add(Registry.ITEM.getOrEmpty(identifier).orElseThrow(() -> new JsonSyntaxException("Unknown item id '" + identifier + "'")));
                    }

                    set = builder.build();
                }

                TagKey<Item> tagKey = null;
                if (jsonObject.has("tag")) {
                    Identifier identifier2 = new Identifier(JsonHelper.getString(jsonObject, "tag"));
                    tagKey = TagKey.of(Registry.ITEM_KEY, identifier2);
                }

                Potion potion = null;
                if (jsonObject.has("potion")) {
                    Identifier identifier3 = new Identifier(JsonHelper.getString(jsonObject, "potion"));
                    potion = Registry.POTION.getOrEmpty(identifier3).orElseThrow(() -> new JsonSyntaxException("Unknown potion '" + identifier3 + "'"));
                }

                EnchantmentPredicate[] enchantmentPredicates = EnchantmentPredicate.deserializeAll(jsonObject.get("enchantments"));
                EnchantmentPredicate[] enchantmentPredicates2 = EnchantmentPredicate.deserializeAll(jsonObject.get("stored_enchantments"));
                EquipmentSlot equipmentSlot = jsonObject.has("equipmentSlot")
                    ? EquipmentSlot.byName(JsonHelper.getString(jsonObject, "equipmentSlot"))
                    : null;
                return new EquipmentSlotItemPredicate(tagKey, set, intRange, intRange2, enchantmentPredicates, enchantmentPredicates2, potion, nbtPredicate, equipmentSlot);
            }
        } else {
            return new EquipmentSlotItemPredicate();
        }
    }

    public static EquipmentSlotItemPredicate[] deserializeAll(@Nullable JsonElement el) {
        if (el != null && !el.isJsonNull()) {
            JsonArray jsonArray = JsonHelper.asArray(el, "items");
            EquipmentSlotItemPredicate[] itemPredicates = new EquipmentSlotItemPredicate[jsonArray.size()];

            for (int i = 0; i < itemPredicates.length; ++i) {
                itemPredicates[i] = fromJson(jsonArray.get(i));
            }

            return itemPredicates;
        } else {
            return new EquipmentSlotItemPredicate[0];
        }
    }
}
