package dev.the_fireplace.overlord.impl.data;

import dev.the_fireplace.overlord.domain.data.SquadPatterns;
import dev.the_fireplace.overlord.domain.data.Squads;
import dev.the_fireplace.overlord.domain.data.objects.Squad;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public abstract class AbstractSquadPatterns implements SquadPatterns
{
    protected final Squads squads;

    public AbstractSquadPatterns(Squads squads) {
        this.squads = squads;
    }

    @Override
    public boolean isPatternUnused(ResourceLocation patternId, ItemStack stack) {
        return isPatternUnusedByOtherSquads(patternId, stack, null, null);
    }

    @Override
    public boolean isPatternUnusedByOtherSquads(ResourceLocation patternId, ItemStack stack, UUID owner, UUID squadId) {
        for (Squad squad : squads.getSquads()) {
            if (squad.getOwner().equals(owner) && squad.getSquadId().equals(squadId)) {
                continue;
            }
            if (ItemStack.matches(squad.getItem(), stack) && squad.getPatternId().equals(patternId)) {
                return false;
            }
        }

        return true;
    }
}
