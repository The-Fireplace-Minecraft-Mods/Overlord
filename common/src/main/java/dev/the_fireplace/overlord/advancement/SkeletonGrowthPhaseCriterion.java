package dev.the_fireplace.overlord.advancement;

import com.google.gson.JsonObject;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.entity.SkeletonGrowthPhase;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;

import javax.annotation.Nullable;

public class SkeletonGrowthPhaseCriterion extends SimpleCriterionTrigger<SkeletonGrowthPhaseCriterion.Conditions>
{
    private static final ResourceLocation ID = new ResourceLocation(OverlordConstants.MODID, "skeleton_grown");
    public static final ResourceLocation ANY_AUGMENT = new ResourceLocation(OverlordConstants.MODID, "any_augment");

    public SkeletonGrowthPhaseCriterion() {
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Conditions createInstance(JsonObject jsonObject, EntityPredicate.Composite extended, DeserializationContext advancementEntityPredicateDeserializer) {
        SkeletonGrowthPhase skeletonGrowthPhase = SkeletonGrowthPhase.BABY;
        @Nullable
        Boolean hasSkin = null;
        @Nullable
        Boolean hasMuscles = null;
        @Nullable
        Boolean hasPlayerSkin = null;
        @Nullable
        ResourceLocation augmentId = null;

        if (jsonObject.has("phase")) {
            skeletonGrowthPhase = SkeletonGrowthPhase.valueOf(GsonHelper.getAsString(jsonObject, "phase"));
        }
        if (jsonObject.has("hasSkin")) {
            hasSkin = GsonHelper.getAsBoolean(jsonObject, "hasSkin");
        }
        if (jsonObject.has("hasMuscles")) {
            hasMuscles = GsonHelper.getAsBoolean(jsonObject, "hasMuscles");
        }
        if (jsonObject.has("hasPlayerSkin")) {
            hasPlayerSkin = GsonHelper.getAsBoolean(jsonObject, "hasPlayerSkin");
        }
        if (jsonObject.has("augmentId")) {
            augmentId = new ResourceLocation(GsonHelper.getAsString(jsonObject, "augmentId"));
        }

        return new Conditions(extended, skeletonGrowthPhase, hasSkin, hasMuscles, hasPlayerSkin, augmentId);
    }

    public void trigger(ServerPlayer player, SkeletonGrowthPhase skeletonGrowthPhase, boolean hasSkin, boolean hasMuscles, boolean hasPlayerSkin, ResourceLocation augmentId) {
        this.trigger(player, (conditions) -> conditions.matches(skeletonGrowthPhase, hasSkin, hasMuscles, hasPlayerSkin, augmentId));
    }

    public static class Conditions extends AbstractCriterionTriggerInstance
    {
        private final SkeletonGrowthPhase phase;
        private final Boolean hasSkin;
        private final Boolean hasMuscles;
        private final Boolean hasPlayerSkin;
        private final ResourceLocation augmentId;

        public Conditions(
            EntityPredicate.Composite player,
            SkeletonGrowthPhase phase,
            @Nullable Boolean hasSkin,
            @Nullable Boolean hasMuscles,
            @Nullable Boolean hasPlayerSkin,
            @Nullable ResourceLocation augmentId
        ) {
            super(ID, player);
            this.phase = phase;
            this.hasSkin = hasSkin;
            this.hasMuscles = hasMuscles;
            this.hasPlayerSkin = hasPlayerSkin;
            this.augmentId = augmentId;
        }

        public static Conditions any() {
            return new Conditions(EntityPredicate.Composite.ANY, SkeletonGrowthPhase.BABY, null, null, null, null);
        }

        public static Conditions of(SkeletonGrowthPhase growthPhase, @Nullable Boolean hasSkin, @Nullable Boolean hasMuscles, @Nullable Boolean hasPlayerSkin, @Nullable ResourceLocation augmentId) {
            return new Conditions(EntityPredicate.Composite.ANY, growthPhase, hasSkin, hasMuscles, hasPlayerSkin, augmentId);
        }

        public boolean matches(SkeletonGrowthPhase growthPhase, boolean hasSkin, boolean hasMuscles, boolean hasPlayerSkin, @Nullable ResourceLocation augmentId) {
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
        public JsonObject serializeToJson(SerializationContext predicateSerializer) {
            JsonObject jsonObject = super.serializeToJson(predicateSerializer);
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
