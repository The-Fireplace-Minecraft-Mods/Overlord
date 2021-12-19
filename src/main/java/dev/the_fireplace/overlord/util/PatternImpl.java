package dev.the_fireplace.overlord.util;

import dev.the_fireplace.annotateddi.api.DIContainer;
import dev.the_fireplace.overlord.domain.advancement.AdvancementProgressFinder;
import dev.the_fireplace.overlord.domain.data.objects.Pattern;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public final class PatternImpl implements Pattern
{
    private final Identifier patternId;
    private final Advancement advancementCriterion;
    private final boolean isDonatorOnly;

    public PatternImpl(Identifier patternId) {
        this.patternId = patternId;
        this.advancementCriterion = null;
        this.isDonatorOnly = false;
    }

    public PatternImpl(Identifier patternId, Advancement advancementCriterion) {
        this.patternId = patternId;
        this.advancementCriterion = advancementCriterion;
        this.isDonatorOnly = false;
    }

    public PatternImpl(Identifier patternId, boolean isDonatorOnly) {
        this.patternId = patternId;
        this.advancementCriterion = null;
        this.isDonatorOnly = isDonatorOnly;
    }

    @Override
    public Identifier getId() {
        return patternId;
    }

    @Override
    public boolean canBeUsedBy(PlayerEntity player) {
        if (isDonatorOnly) {
            return false;
        }
        if (advancementCriterion == null) {
            return true;
        }
        AdvancementProgress progress = DIContainer.get().getInstance(AdvancementProgressFinder.class).getProgress(player, advancementCriterion);

        return progress.isDone();
    }

    @Override
    public Identifier getTextureLocation() {
        return new Identifier(patternId.getNamespace(), "textures/entity/cape/" + patternId.getPath() + ".png");
    }
}
