package dev.the_fireplace.overlord.domain.data.objects;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public interface Squad
{
    UUID getSquadId();

    UUID getOwner();

    ResourceLocation getPatternId();

    ItemStack getItem();

    void updatePattern(ResourceLocation patternId, ItemStack capeItem);

    String getName();

    void setName(String name);
}
