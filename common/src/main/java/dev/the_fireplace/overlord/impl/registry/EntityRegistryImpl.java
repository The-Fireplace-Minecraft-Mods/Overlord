package dev.the_fireplace.overlord.impl.registry;

import com.google.common.collect.Lists;
import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.domain.registry.EntityRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import javax.inject.Singleton;
import java.util.Collection;
import java.util.List;

@Implementation
@Singleton
public final class EntityRegistryImpl implements EntityRegistry
{

    private final List<ResourceLocation> mobIds = Lists.newArrayList();
    private final List<ResourceLocation> animalIds = Lists.newArrayList();

    public EntityRegistryImpl() {
        if (Registry.ENTITY_TYPE.stream().toArray().length == 0) {
            throw new IllegalStateException("Tried to access the entity type registry before it was initialized!");
        }
        for (EntityType<?> entityType : Registry.ENTITY_TYPE) {
            if (!entityType.getCategory().isFriendly()) {
                mobIds.add(Registry.ENTITY_TYPE.getKey(entityType));
            } else {
                animalIds.add(Registry.ENTITY_TYPE.getKey(entityType));
            }
        }
    }

    @Override
    public Collection<ResourceLocation> getMonsterIds() {
        return mobIds;
    }

    @Override
    public Collection<ResourceLocation> getAnimalIds() {
        return animalIds;
    }
}
