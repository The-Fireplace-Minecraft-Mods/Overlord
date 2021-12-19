package dev.the_fireplace.overlord.entity.creation.ingredient;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.TagKey;

public class TagIngredient extends AbstractIngredient
{
    private final TagKey<Item> tag;

    public TagIngredient(TagKey<Item> tag) {
        this.tag = tag;
    }

    @Override
    public boolean matches(ItemStack stack) {
        return stack.isIn(tag);
    }

    public TagKey<Item> getTag() {
        return tag;
    }
}
