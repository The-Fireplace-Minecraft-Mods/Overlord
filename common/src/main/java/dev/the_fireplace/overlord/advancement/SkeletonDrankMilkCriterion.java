package dev.the_fireplace.overlord.advancement;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import dev.the_fireplace.overlord.OverlordConstants;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
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
    public Conditions createInstance(JsonObject jsonObject, JsonDeserializationContext advancementEntityPredicateDeserializer) {
        int amount = 0;
        if (jsonObject.has("totalBucketsDrank")) {
            amount = GsonHelper.getAsInt(jsonObject, "totalBucketsDrank");
        }

        return new Conditions(amount);
    }

    public void trigger(ServerPlayer player, int amount) {
        this.trigger(player.getAdvancements(), (conditions) -> conditions.matches(amount));
    }

    public static class Conditions extends AbstractCriterionTriggerInstance
    {
        private final int totalBucketsDrank;

        public Conditions(int totalBucketsDrank) {
            super(ID);
            this.totalBucketsDrank = totalBucketsDrank;
        }

        public static Conditions any() {
            return new Conditions(0);
        }

        public static Conditions of(int totalMilkBucketsDrank) {
            return new Conditions(totalMilkBucketsDrank);
        }

        public boolean matches(int totalBucketsDrank) {
            return totalBucketsDrank >= this.totalBucketsDrank;
        }

        @Override
        public JsonObject serializeToJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("totalBucketsDrank", this.totalBucketsDrank);

            return jsonObject;
        }
    }
}
