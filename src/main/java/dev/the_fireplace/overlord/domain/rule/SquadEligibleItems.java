package dev.the_fireplace.overlord.domain.rule;

import dev.the_fireplace.overlord.domain.data.objects.Squad;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Collection;

public interface SquadEligibleItems
{
    Collection<ItemStack> getEligibleItems(Collection<Squad> squads, @Nullable PlayerEntity player, @Nullable Entity armyEntity);

    ItemStack convertToSquadItem(ItemStack stack);
}
