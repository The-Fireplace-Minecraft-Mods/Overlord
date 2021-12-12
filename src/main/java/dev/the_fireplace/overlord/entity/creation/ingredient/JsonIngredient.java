package dev.the_fireplace.overlord.entity.creation.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.JsonOps;
import dev.the_fireplace.overlord.domain.entity.creation.SkeletonIngredient;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Optional;

public class JsonIngredient
{
    public static SkeletonIngredient parse(JsonObject jsonObject) throws JsonParseException {
        boolean isTag = jsonObject.has("type") && jsonObject.get("type").getAsString().equalsIgnoreCase("tag");
        AbstractIngredient ingredient;
        if (!jsonObject.has("id")) {
            throw new JsonParseException(String.format("Missing identifier for ingredient: %s", jsonObject));
        }
        Identifier identifier = new Identifier(jsonObject.get("id").getAsString());
        if (isTag) {
            Tag<Item> itemTag = TagRegistry.item(identifier);
            ingredient = new TagIngredient(itemTag);
        } else {
            Optional<Item> item = Registry.ITEM.getOrEmpty(identifier);
            if (!item.isPresent()) {
                throw new JsonParseException(String.format("Item not found: %s", identifier));
            }
            ingredient = new ItemIngredient(item.get());
            if (jsonObject.has("nbt")) {
                NbtCompound nbt = parseNbt(jsonObject);
                ((ItemIngredient) ingredient).setNbtCompound(nbt);
            }
        }

        if (jsonObject.has("count")) {
            int count = jsonObject.get("count").getAsInt();
            ingredient.setRequiredCount(count);
        }

        return ingredient;
    }

    public static NbtCompound parseNbt(JsonObject jsonObject) {
        JsonElement jsonNbtData = jsonObject.get("nbt");
        if (jsonNbtData.isJsonObject()) {
            NbtElement element = JsonOps.INSTANCE.convertTo(NbtOps.INSTANCE, jsonNbtData);
            return (NbtCompound) element;
        } else {
            String nbtString = jsonNbtData.getAsString();
            NbtCompound nbt;
            try {
                nbt = StringNbtReader.parse(nbtString);
            } catch (CommandSyntaxException exception) {
                throw new JsonParseException(String.format("Unable to read NBT for ingredient: %s", nbtString));
            }
            return nbt;
        }
    }
}
