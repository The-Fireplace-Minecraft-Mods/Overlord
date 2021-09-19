package dev.the_fireplace.overlord.impl.registry;

import com.google.common.collect.Lists;
import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.domain.registry.EntityRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import javax.inject.Singleton;
import java.util.Collection;
import java.util.List;

@Implementation
@Singleton
public final class EntityRegistryImpl implements EntityRegistry {

    private final List<Identifier> mobIds = Lists.newArrayList();
    private final List<Identifier> animalIds = Lists.newArrayList();

    public EntityRegistryImpl() {
        if (Registry.ENTITY_TYPE.isEmpty()) {
            throw new IllegalStateException("Tried to access the entity type registry before it was initialized!");
        }
        for (EntityType<?> entityType : Registry.ENTITY_TYPE) {
            if (!entityType.getCategory().isPeaceful()) {
                mobIds.add(Registry.ENTITY_TYPE.getId(entityType));
            }
            if (entityType.getCategory().isAnimal()) {
                animalIds.add(Registry.ENTITY_TYPE.getId(entityType));
            }
        }
    }

    @Override
    public Collection<Identifier> getMonsterIds() {
        return mobIds;
    }

    @Override
    public Collection<Identifier> getAnimalIds() {
        return animalIds;
    }
}
