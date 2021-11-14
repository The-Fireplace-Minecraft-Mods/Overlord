package dev.the_fireplace.overlord.entity.creation.ingredient;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.Tag;

public class TagIngredient extends AbstractIngredient
{
    private final Tag<Item> tag;

    public TagIngredient(Tag<Item> tag) {
        this.tag = tag;
    }

    @Override
    public boolean matches(ItemStack stack) {
        return tag.contains(stack.getItem());
    }
}
