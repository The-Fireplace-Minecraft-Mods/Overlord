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

    @Nullable
    Squad createNewSquad(UUID owner, String capeBase, ItemStack stack, String name);

    void removeSquad(UUID owner, UUID squadId);

    Collection<? extends Squad> getSquadsWithOwner(UUID owner);

    boolean isCapeUnused(String capeBase, ItemStack stack);

    boolean canUseCapeBase(UUID player, String capeBase);
}
