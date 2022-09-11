package dev.the_fireplace.overlord.advancement;

import com.google.gson.JsonObject;
import dev.the_fireplace.overlord.OverlordConstants;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;

public class SkeletonDrankMilkCriterion extends SimpleCriterionTrigger<SkeletonDrankMilkCriterion.Conditions>
{
    public static final ResourceLocation ID = new ResourceLocation(OverlordConstants.MODID, "skeleton_drank_milk");

    public SkeletonDrankMilkCriterion() {
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Conditions createInstance(JsonObject jsonObject, EntityPredicate.Composite extended, DeserializationContext advancementEntityPredicateDeserializer) {
        int amount = 0;
        if (jsonObject.has("totalBucketsDrank")) {
            amount = GsonHelper.getAsInt(jsonObject, "totalBucketsDrank");
        }

        return new Conditions(extended, amount);
    }

    public void trigger(ServerPlayer player, int amount) {
        this.trigger(player, (conditions) -> conditions.matches(amount));
    }

    public static class Conditions extends AbstractCriterionTriggerInstance
    {
        private final int totalBucketsDrank;

        public Conditions(EntityPredicate.Composite player, int totalBucketsDrank) {
            super(ID, player);
            this.totalBucketsDrank = totalBucketsDrank;
        }

        public static Conditions any() {
            return new Conditions(EntityPredicate.Composite.ANY, 0);
        }

        public static Conditions of(int totalMilkBucketsDrank) {
            return new Conditions(EntityPredicate.Composite.ANY, totalMilkBucketsDrank);
        }

        public boolean matches(int totalBucketsDrank) {
            return totalBucketsDrank >= this.totalBucketsDrank;
        }

        @Override
        public JsonObject serializeToJson(SerializationContext predicateSerializer) {
            JsonObject jsonObject = super.serializeToJson(predicateSerializer);
            jsonObject.addProperty("totalBucketsDrank", this.totalBucketsDrank);

            return jsonObject;
        }
    }
}
