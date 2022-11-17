package dev.the_fireplace.overlord.domain.inventory;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public interface CommonMatchers {
    Predicate<ItemStack> id(String id);

    Predicate<ItemStack> id(ResourceLocation id);

    Predicate<ItemStack> tag(TagKey<?> tag);
}
