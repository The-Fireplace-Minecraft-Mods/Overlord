package dev.the_fireplace.overlord.advancement;

import com.google.gson.JsonObject;
import dev.the_fireplace.overlord.Overlord;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class SkeletonDrankMilkCriterion extends AbstractCriterion<SkeletonDrankMilkCriterion.Conditions>
{
    public static final Identifier ID = new Identifier(Overlord.MODID, "skeleton_drank_milk");

    public SkeletonDrankMilkCriterion() {
    }

    public Identifier getId() {
        return ID;
    }

    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer) {
        int amount = 0;
        if (jsonObject.has("totalBucketsDrank")) {
            amount = JsonHelper.getInt(jsonObject, "totalBucketsDrank");
        }

        return new Conditions(extended, amount);
    }

    public void trigger(ServerPlayerEntity player, int amount) {
        this.trigger(player, (conditions) -> conditions.matches(amount));
    }

    public static class Conditions extends AbstractCriterionConditions
    {
        private final int totalBucketsDrank;

        public Conditions(EntityPredicate.Extended player, int totalBucketsDrank) {
            super(ID, player);
            this.totalBucketsDrank = totalBucketsDrank;
        }

        public static Conditions any() {
            return new Conditions(EntityPredicate.Extended.EMPTY, 0);
        }

        public static Conditions of(int totalMilkBucketsDrank) {
            return new Conditions(EntityPredicate.Extended.EMPTY, totalMilkBucketsDrank);
        }

        public boolean matches(int totalBucketsDrank) {
            return totalBucketsDrank >= this.totalBucketsDrank;
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.addProperty("totalBucketsDrank", this.totalBucketsDrank);

            return jsonObject;
        }
    }
}
