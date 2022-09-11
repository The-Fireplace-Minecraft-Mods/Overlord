package dev.the_fireplace.overlord.advancement;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.entity.ArmyEntity;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;

public class ArmyMemberMilkedCowCriterion extends SimpleCriterionTrigger<ArmyMemberMilkedCowCriterion.Conditions>
{
    public static final ResourceLocation ID = new ResourceLocation(OverlordConstants.MODID, "army_member_gathered_milk");

    public ArmyMemberMilkedCowCriterion() {
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Conditions createInstance(JsonObject jsonObject, JsonDeserializationContext advancementEntityPredicateDeserializer) {
        //noinspection rawtypes
        EntityType entityType = null;
        if (jsonObject.has("entityType")) {
            ResourceLocation identifier = new ResourceLocation(GsonHelper.getAsString(jsonObject, "entityType"));
            entityType = Registry.ENTITY_TYPE.getOptional(identifier).orElseThrow(() -> new JsonSyntaxException("Unknown entity type '" + identifier + "'"));
        }

        //noinspection unchecked
        return new Conditions(entityType);
    }

    public void trigger(ServerPlayer player, EntityType<?> entityType) {
        this.trigger(player.getAdvancements(), (conditions) -> conditions.matches(entityType));
    }

    public static class Conditions extends AbstractCriterionTriggerInstance
    {
        private final EntityType<?> entityType;

        public Conditions(EntityType<? extends ArmyEntity> entityType) {
            super(ID);
            this.entityType = entityType;
        }

        public static Conditions any() {
            return new Conditions(null);
        }

        public static Conditions of(EntityType<? extends ArmyEntity> entityType) {
            return new Conditions(entityType);
        }

        public boolean matches(EntityType<?> entityType) {
            return this.entityType == null || this.entityType.equals(entityType);
        }

        @Override
        public JsonObject serializeToJson() {
            JsonObject jsonObject = new JsonObject();
            if (this.entityType != null) {
                jsonObject.addProperty("entityType", Registry.ENTITY_TYPE.getKey(this.entityType).toString());
            }

            return jsonObject;
        }
    }
}
