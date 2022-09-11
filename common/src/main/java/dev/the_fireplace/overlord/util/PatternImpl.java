package dev.the_fireplace.overlord.util;

import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.domain.advancement.AdvancementProgressFinder;
import dev.the_fireplace.overlord.domain.data.objects.Pattern;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public final class PatternImpl implements Pattern
{
    private final ResourceLocation patternId;
    private final Advancement advancementCriterion;
    private final boolean isDonatorOnly;//TODO remove

    public PatternImpl(ResourceLocation patternId) {
        this.patternId = patternId;
        this.advancementCriterion = null;
        this.isDonatorOnly = false;
    }

    public PatternImpl(ResourceLocation patternId, Advancement advancementCriterion) {
        this.patternId = patternId;
        this.advancementCriterion = advancementCriterion;
        this.isDonatorOnly = false;
    }

    public PatternImpl(ResourceLocation patternId, boolean isDonatorOnly) {
        this.patternId = patternId;
        this.advancementCriterion = null;
        this.isDonatorOnly = isDonatorOnly;
    }

    @Override
    public ResourceLocation getId() {
        return patternId;
    }

    @Override
    public boolean canBeUsedBy(Player player) {
        if (isDonatorOnly) {
            return false;
        }
        if (advancementCriterion == null) {
            return true;
        }
        AdvancementProgress progress = OverlordConstants.getInjector().getInstance(AdvancementProgressFinder.class).getProgress(player, advancementCriterion);

        return progress.isDone();
    }

    @Override
    public ResourceLocation getTextureLocation() {
        return new ResourceLocation(patternId.getNamespace(), "textures/entity/cape/" + patternId.getPath() + ".png");
    }
}
