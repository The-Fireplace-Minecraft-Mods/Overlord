package dev.the_fireplace.overlord.impl.data;

import dev.the_fireplace.overlord.domain.data.SquadPatterns;
import dev.the_fireplace.overlord.domain.data.Squads;
import dev.the_fireplace.overlord.domain.data.objects.Squad;
import net.minecraft.item.ItemStack;

import java.util.UUID;

public abstract class AbstractSquadPatterns implements SquadPatterns
{
    protected final Squads squads;

    public AbstractSquadPatterns(Squads squads) {
        this.squads = squads;
    }

    @Override
    public boolean isPatternUnused(String pattern, ItemStack stack) {
        return isPatternUnusedByOtherSquads(pattern, stack, null, null);
    }

    @Override
    public boolean isPatternUnusedByOtherSquads(String pattern, ItemStack stack, UUID owner, UUID squadId) {
        for (Squad squad : squads.getSquads()) {
            if (squad.getOwner().equals(owner) && squad.getSquadId().equals(squadId)) {
                continue;
            }
            if (ItemStack.areItemsEqual(squad.getItem(), stack) && ItemStack.areTagsEqual(squad.getItem(), stack) && squad.getPattern().equals(pattern)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean canUsePattern(UUID player, String pattern) {
        //TODO adjust when more capes are added
        return true;
    }
}
