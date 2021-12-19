package dev.the_fireplace.overlord.advancement;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.entity.ArmyEntity;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.entity.EntityType;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class ObtainedArmyMemberCriterion extends AbstractCriterion<ObtainedArmyMemberCriterion.Conditions>
{
    public static final Identifier ID = new Identifier(Overlord.MODID, "obtained_army_member");

    public ObtainedArmyMemberCriterion() {
    }

    public Identifier getId() {
        return ID;
    }

    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer) {
        //noinspection rawtypes
        EntityType entityType = null;
        if (jsonObject.has("entityType")) {
            Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "entityType"));
            entityType = Registry.ENTITY_TYPE.getOrEmpty(identifier).orElseThrow(() -> new JsonSyntaxException("Unknown entity type '" + identifier + "'"));
        }

        //noinspection unchecked
        return new Conditions(extended, entityType);
    }

    public void trigger(ServerPlayerEntity player, EntityType<?> entityType) {
        this.trigger(player, (conditions) -> conditions.matches(entityType));
    }

    public static class Conditions extends AbstractCriterionConditions
    {
        @Nullable
        private final EntityType<?> entityType;

        public Conditions(EntityPredicate.Extended player, @Nullable EntityType<? extends ArmyEntity> entityType) {
            super(ID, player);
            this.entityType = entityType;
        }

        public static Conditions any() {
            return new Conditions(EntityPredicate.Extended.EMPTY, null);
        }

        public boolean matches(EntityType<?> entityType) {
            return this.entityType == null || this.entityType.equals(entityType);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            if (this.entityType != null) {
                jsonObject.addProperty("entityType", Registry.ENTITY_TYPE.getId(this.entityType).toString());
            }

            return jsonObject;
        }
    }
}
