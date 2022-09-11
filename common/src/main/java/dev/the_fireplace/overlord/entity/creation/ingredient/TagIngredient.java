package dev.the_fireplace.overlord.entity.creation.ingredient;

import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class TagIngredient extends AbstractIngredient
{
    private final Tag<Item> tag;

    public TagIngredient(Tag<Item> tag) {
        this.tag = tag;
    }

    @Override
    public boolean matches(ItemStack stack) {
        return stack.is(tag);
    }

    public Tag<Item> getTag() {
        return tag;
    }
}
