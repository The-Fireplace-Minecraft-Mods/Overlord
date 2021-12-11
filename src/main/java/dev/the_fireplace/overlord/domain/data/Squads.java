package dev.the_fireplace.overlord.domain.data;

import dev.the_fireplace.overlord.domain.data.objects.Squad;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;

public interface Squads
{
    @Nullable
    Squad getSquad(UUID owner, UUID squadId);

    Squad createNewSquad(UUID owner, String pattern, ItemStack stack, String name);

    boolean removeSquad(UUID owner, UUID squadId);

    Collection<? extends Squad> getSquadsWithOwner(UUID owner);

    Collection<? extends Squad> getSquads();
}
