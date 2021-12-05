package dev.the_fireplace.overlord.domain.data.objects;

import net.minecraft.item.ItemStack;

import java.util.UUID;

public interface Squad
{
    UUID getSquadId();

    UUID getOwner();

    String getPattern();

    ItemStack getItem();

    void updatePattern(String capeBase, ItemStack capeItem);

    String getName();

    void setName(String name);
}
