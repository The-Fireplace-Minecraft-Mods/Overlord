package dev.the_fireplace.overlord.domain.data;

import dev.the_fireplace.overlord.domain.data.objects.Squad;
import net.minecraft.item.ItemStack;

import java.util.UUID;

public interface Squads
{
    void saveSquad(Squad squad);

    Squad getSquad(UUID owner, UUID squadId);

    Squad removeSquad(UUID owner, UUID squadId);

    boolean isCapeUnused(String capeBase, ItemStack stack);

    boolean canUseCapeBase(UUID player, String capeBase);
}
