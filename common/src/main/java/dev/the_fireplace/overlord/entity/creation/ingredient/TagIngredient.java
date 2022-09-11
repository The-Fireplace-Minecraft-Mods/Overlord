package dev.the_fireplace.overlord.entity.creation.ingredient;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class TagIngredient extends AbstractIngredient
{
    private final TagKey<Item> tag;

    public TagIngredient(TagKey<Item> tag) {
        this.tag = tag;
    }

    @Override
    public boolean matches(ItemStack stack) {
        return stack.is(tag);
    }

    public TagKey<Item> getTag() {
        return tag;
    }
}
