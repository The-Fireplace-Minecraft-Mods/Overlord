package dev.the_fireplace.overlord.advancement;

import com.google.gson.*;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.loader.TagHelper;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.ItemLike;

import java.util.Set;

public class EquipmentSlotItemPredicate extends ItemPredicate
{
    public static final EquipmentSlotItemPredicate ANY = new EquipmentSlotItemPredicate();

    private final EquipmentSlot slot;

    private EquipmentSlotItemPredicate() {
        super();
        this.slot = null;
    }

    public EquipmentSlotItemPredicate(
        Tag.Named<Item> tag,
        Item item,
        MinMaxBounds.Ints count,
        MinMaxBounds.Ints durability,
        EnchantmentPredicate[] enchantments,
        EnchantmentPredicate[] storedEnchantments,
        Potion potion,
        NbtPredicate nbt,
        EquipmentSlot slot
    ) {
        super(tag, item, count, durability, enchantments, storedEnchantments, potion, nbt);
        this.slot = slot;
    }

    public static EquipmentSlotItemPredicate simple(ItemLike item, EquipmentSlot slot) {
        return new EquipmentSlotItemPredicate(null, item.asItem(), MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, EnchantmentPredicate.NONE, EnchantmentPredicate.NONE, null, NbtPredicate.ANY, slot);
    }

    @Override
    public boolean matches(ItemStack stack) {
        return slot == null && super.matches(stack);
    }

    public boolean test(ItemStack stack, EquipmentSlot slot) {
        return (this.slot == null || this.slot == slot) && super.matches(stack);
    }

    @Override
    public JsonElement serializeToJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonElement jsonElement = super.serializeToJson();
        if (jsonElement instanceof JsonObject) {
            JsonObject jsonObject = (JsonObject) jsonElement;
            if (this.slot != null) {
                jsonObject.addProperty("equipmentSlot", this.slot.getName());
            }
            return jsonObject;
        }
        return jsonElement;
    }

    public static EquipmentSlotItemPredicate fromJson(JsonElement el) {
        if (el != null && !el.isJsonNull()) {
            JsonObject jsonObject = GsonHelper.convertToJsonObject(el, "item");
            MinMaxBounds.Ints intRange = MinMaxBounds.Ints.fromJson(jsonObject.get("count"));
            MinMaxBounds.Ints intRange2 = MinMaxBounds.Ints.fromJson(jsonObject.get("durability"));
            if (jsonObject.has("data")) {
                throw new JsonParseException("Disallowed data tag found");
            } else {
                NbtPredicate nbtPredicate = NbtPredicate.fromJson(jsonObject.get("nbt"));
                Set<Item> set = null;
                Item item = null;
                if (jsonObject.has("item")) {
                    ResourceLocation var5 = new ResourceLocation(GsonHelper.getAsString(jsonObject, "item"));
                    item = Registry.ITEM.getOptional(var5).orElseThrow(() -> new JsonSyntaxException("Unknown item id '" + var5 + "'"));
                }

                Tag.Named<Item> tagKey = null;
                if (jsonObject.has("tag")) {
                    TagHelper tagHelper = OverlordConstants.getInjector().getInstance(TagHelper.class);
                    ResourceLocation identifier2 = new ResourceLocation(GsonHelper.getAsString(jsonObject, "tag"));
                    tagKey = tagHelper.createItemTag(identifier2);
                }

                Potion potion = null;
                if (jsonObject.has("potion")) {
                    ResourceLocation identifier3 = new ResourceLocation(GsonHelper.getAsString(jsonObject, "potion"));
                    potion = Registry.POTION.getOptional(identifier3).orElseThrow(() -> new JsonSyntaxException("Unknown potion '" + identifier3 + "'"));
                }

                EnchantmentPredicate[] enchantmentPredicates = EnchantmentPredicate.fromJsonArray(jsonObject.get("enchantments"));
                EnchantmentPredicate[] enchantmentPredicates2 = EnchantmentPredicate.fromJsonArray(jsonObject.get("stored_enchantments"));
                EquipmentSlot equipmentSlot = jsonObject.has("equipmentSlot")
                    ? EquipmentSlot.byName(GsonHelper.getAsString(jsonObject, "equipmentSlot"))
                    : null;
                return new EquipmentSlotItemPredicate(tagKey, item, intRange, intRange2, enchantmentPredicates, enchantmentPredicates2, potion, nbtPredicate, equipmentSlot);
            }
        } else {
            return new EquipmentSlotItemPredicate();
        }
    }

    public static EquipmentSlotItemPredicate[] deserializeAll(JsonElement el) {
        if (el != null && !el.isJsonNull()) {
            JsonArray jsonArray = GsonHelper.convertToJsonArray(el, "items");
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
