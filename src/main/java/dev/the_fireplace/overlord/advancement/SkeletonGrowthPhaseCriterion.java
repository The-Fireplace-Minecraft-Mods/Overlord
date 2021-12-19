package dev.the_fireplace.overlord.advancement;

import com.google.gson.JsonObject;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.entity.SkeletonGrowthPhase;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import javax.annotation.Nullable;

public class SkeletonGrowthPhaseCriterion extends AbstractCriterion<SkeletonGrowthPhaseCriterion.Conditions>
{
    private static final Identifier ID = new Identifier(Overlord.MODID, "skeleton_grown");
    public static final Identifier ANY_AUGMENT = new Identifier(Overlord.MODID, "any_augment");

    public SkeletonGrowthPhaseCriterion() {
    }

    public Identifier getId() {
        return ID;
    }

    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer) {
        SkeletonGrowthPhase skeletonGrowthPhase = SkeletonGrowthPhase.BABY;
        @Nullable
        Boolean hasSkin = null;
        @Nullable
        Boolean hasMuscles = null;
        @Nullable
        Boolean hasPlayerSkin = null;
        @Nullable
        Identifier augmentId = null;

        if (jsonObject.has("phase")) {
            skeletonGrowthPhase = SkeletonGrowthPhase.valueOf(JsonHelper.getString(jsonObject, "phase"));
        }
        if (jsonObject.has("hasSkin")) {
            hasSkin = JsonHelper.getBoolean(jsonObject, "hasSkin");
        }
        if (jsonObject.has("hasMuscles")) {
            hasMuscles = JsonHelper.getBoolean(jsonObject, "hasMuscles");
        }
        if (jsonObject.has("hasPlayerSkin")) {
            hasPlayerSkin = JsonHelper.getBoolean(jsonObject, "hasPlayerSkin");
        }
        if (jsonObject.has("augmentId")) {
            augmentId = new Identifier(JsonHelper.getString(jsonObject, "augmentId"));
        }

        return new Conditions(extended, skeletonGrowthPhase, hasSkin, hasMuscles, hasPlayerSkin, augmentId);
    }

    public void trigger(ServerPlayerEntity player, SkeletonGrowthPhase skeletonGrowthPhase, boolean hasSkin, boolean hasMuscles, boolean hasPlayerSkin, Identifier augmentId) {
        this.trigger(player, (conditions) -> conditions.matches(skeletonGrowthPhase, hasSkin, hasMuscles, hasPlayerSkin, augmentId));
    }

    public static class Conditions extends AbstractCriterionConditions
    {
        private final SkeletonGrowthPhase phase;
        private final Boolean hasSkin;
        private final Boolean hasMuscles;
        private final Boolean hasPlayerSkin;
        private final Identifier augmentId;

        public Conditions(
            EntityPredicate.Extended player,
            SkeletonGrowthPhase phase,
            @Nullable Boolean hasSkin,
            @Nullable Boolean hasMuscles,
            @Nullable Boolean hasPlayerSkin,
            @Nullable Identifier augmentId
        ) {
            super(ID, player);
            this.phase = phase;
            this.hasSkin = hasSkin;
            this.hasMuscles = hasMuscles;
            this.hasPlayerSkin = hasPlayerSkin;
            this.augmentId = augmentId;
        }

        public static Conditions any() {
            return new Conditions(EntityPredicate.Extended.EMPTY, SkeletonGrowthPhase.BABY, null, null, null, null);
        }

        public static Conditions of(SkeletonGrowthPhase growthPhase, @Nullable Boolean hasSkin, @Nullable Boolean hasMuscles, @Nullable Boolean hasPlayerSkin, @Nullable Identifier augmentId) {
            return new Conditions(EntityPredicate.Extended.EMPTY, growthPhase, hasSkin, hasMuscles, hasPlayerSkin, augmentId);
        }

        public boolean matches(SkeletonGrowthPhase growthPhase, boolean hasSkin, boolean hasMuscles, boolean hasPlayerSkin, @Nullable Identifier augmentId) {
            if (!growthPhase.isAtLeast(this.phase)) {
                return false;
            }
            if (this.hasSkin != null && this.hasSkin != hasSkin) {
                return false;
            } else if (this.hasMuscles != null && this.hasMuscles != hasMuscles) {
                return false;
            } else if (this.hasPlayerSkin != null && this.hasPlayerSkin != hasPlayerSkin) {
                return false;
            } else {
                return this.augmentId == null || this.augmentId.equals(augmentId) || (this.augmentId.equals(ANY_AUGMENT) && augmentId != null);
            }
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.addProperty("phase", this.phase.name());
            if (this.hasSkin != null) {
                jsonObject.addProperty("hasSkin", this.hasSkin);
            }
            if (this.hasMuscles != null) {
                jsonObject.addProperty("hasMuscles", this.hasMuscles);
            }
            if (this.hasPlayerSkin != null) {
                jsonObject.addProperty("hasPlayerSkin", this.hasPlayerSkin);
            }
            if (this.augmentId != null) {
                jsonObject.addProperty("augmentId", this.augmentId.toString());
            }

            return jsonObject;
        }
    }
}
