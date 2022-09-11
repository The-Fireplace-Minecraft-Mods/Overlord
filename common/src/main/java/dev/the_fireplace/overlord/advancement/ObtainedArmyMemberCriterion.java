package dev.the_fireplace.overlord.advancement;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.entity.ArmyEntity;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

public class ObtainedArmyMemberCriterion extends SimpleCriterionTrigger<ObtainedArmyMemberCriterion.Conditions>
{
    public static final ResourceLocation ID = new ResourceLocation(OverlordConstants.MODID, "obtained_army_member");

    public ObtainedArmyMemberCriterion() {
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Conditions createInstance(JsonObject jsonObject, EntityPredicate.Composite extended, DeserializationContext advancementEntityPredicateDeserializer) {
        //noinspection rawtypes
        EntityType entityType = null;
        if (jsonObject.has("entityType")) {
            ResourceLocation identifier = new ResourceLocation(GsonHelper.getAsString(jsonObject, "entityType"));
            entityType = Registry.ENTITY_TYPE.getOptional(identifier).orElseThrow(() -> new JsonSyntaxException("Unknown entity type '" + identifier + "'"));
        }

        //noinspection unchecked
        return new Conditions(extended, entityType);
    }

    public void trigger(ServerPlayer player, EntityType<?> entityType) {
        this.trigger(player, (conditions) -> conditions.matches(entityType));
    }

    public static class Conditions extends AbstractCriterionTriggerInstance
    {
        @Nullable
        private final EntityType<?> entityType;

        public Conditions(EntityPredicate.Composite player, @Nullable EntityType<? extends ArmyEntity> entityType) {
            super(ID, player);
            this.entityType = entityType;
        }

        public static Conditions any() {
            return new Conditions(EntityPredicate.Composite.ANY, null);
        }

        public boolean matches(EntityType<?> entityType) {
            return this.entityType == null || this.entityType.equals(entityType);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext predicateSerializer) {
            JsonObject jsonObject = super.serializeToJson(predicateSerializer);
            if (this.entityType != null) {
                jsonObject.addProperty("entityType", Registry.ENTITY_TYPE.getKey(this.entityType).toString());
            }

            return jsonObject;
        }
    }
}
