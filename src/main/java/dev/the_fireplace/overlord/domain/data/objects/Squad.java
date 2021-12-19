package dev.the_fireplace.overlord.domain.data.objects;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.UUID;

public interface Squad
{
    UUID getSquadId();

    UUID getOwner();

    Identifier getPatternId();

    ItemStack getItem();

    void updatePattern(Identifier patternId, ItemStack capeItem);

    String getName();

    void setName(String name);
}
