package dev.the_fireplace.overlord.domain.rule;

import dev.the_fireplace.overlord.domain.data.objects.Squad;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Collection;

public interface SquadEligibleItems
{
    Collection<ItemStack> getEligibleItems(Collection<Squad> squads, @Nullable Player player, @Nullable Entity armyEntity);

    ItemStack convertToSquadItem(ItemStack stack);
}
