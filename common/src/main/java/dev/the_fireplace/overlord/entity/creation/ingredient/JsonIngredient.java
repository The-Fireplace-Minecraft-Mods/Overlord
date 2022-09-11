package dev.the_fireplace.overlord.entity.creation.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.JsonOps;
import dev.the_fireplace.overlord.domain.entity.creation.SkeletonIngredient;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.Optional;

public class JsonIngredient
{
    public static SkeletonIngredient parse(JsonObject jsonObject) throws JsonParseException {
        boolean isTag = jsonObject.has("type") && jsonObject.get("type").getAsString().equalsIgnoreCase("tag");
        AbstractIngredient ingredient;
        if (!jsonObject.has("id")) {
            throw new JsonParseException(String.format("Missing identifier for ingredient: %s", jsonObject));
        }
        ResourceLocation identifier = new ResourceLocation(jsonObject.get("id").getAsString());
        if (isTag) {
            TagKey<Item> itemTag = TagKey.create(Registry.ITEM_REGISTRY, identifier);
            ingredient = new TagIngredient(itemTag);
        } else {
            Optional<Item> item = Registry.ITEM.getOptional(identifier);
            if (item.isEmpty()) {
                throw new JsonParseException(String.format("Item not found: %s", identifier));
            }
            ingredient = new ItemIngredient(item.get());
            if (jsonObject.has("nbt")) {
                CompoundTag nbt = parseNbt(jsonObject);
                ((ItemIngredient) ingredient).setNbtCompound(nbt);
            }
        }

        if (jsonObject.has("count")) {
            int count = jsonObject.get("count").getAsInt();
            ingredient.setRequiredCount(count);
        }

        return ingredient;
    }

    public static CompoundTag parseNbt(JsonObject jsonObject) {
        JsonElement jsonNbtData = jsonObject.get("nbt");
        if (jsonNbtData.isJsonObject()) {
            Tag element = JsonOps.INSTANCE.convertTo(NbtOps.INSTANCE, jsonNbtData);
            return (CompoundTag) element;
        } else {
            String nbtString = jsonNbtData.getAsString();
            CompoundTag nbt;
            try {
                nbt = TagParser.parseTag(nbtString);
            } catch (CommandSyntaxException exception) {
                throw new JsonParseException(String.format("Unable to read NBT for ingredient: %s", nbtString));
            }
            return nbt;
        }
    }
}
