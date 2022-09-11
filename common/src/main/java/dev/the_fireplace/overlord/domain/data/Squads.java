package dev.the_fireplace.overlord.domain.data;

import dev.the_fireplace.overlord.domain.data.objects.Squad;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;

public interface Squads
{
    @Nullable
    Squad getSquad(UUID owner, UUID squadId);

    Squad createNewSquad(UUID owner, ResourceLocation patternId, ItemStack stack, String name);

    boolean removeSquad(UUID owner, UUID squadId);

    Collection<? extends Squad> getSquadsWithOwner(UUID owner);

    Collection<? extends Squad> getSquads();
}
