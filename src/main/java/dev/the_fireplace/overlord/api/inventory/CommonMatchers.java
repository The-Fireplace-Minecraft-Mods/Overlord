package dev.the_fireplace.overlord.api.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import java.util.function.Predicate;

public interface CommonMatchers {
    Predicate<ItemStack> id(String id);
    Predicate<ItemStack> id(Identifier id);
    Predicate<ItemStack> tag(Tag<?> tag);
}
