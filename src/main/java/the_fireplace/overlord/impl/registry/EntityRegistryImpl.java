package the_fireplace.overlord.impl.registry;

import com.google.common.collect.Lists;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import the_fireplace.overlord.api.internal.EntityRegistry;

import java.util.Collection;
import java.util.List;

public final class EntityRegistryImpl implements EntityRegistry {
    @Deprecated
    public static final EntityRegistry INSTANCE = new EntityRegistryImpl().init();
    private EntityRegistryImpl(){}

    private final List<Identifier> mobIds = Lists.newArrayList();
    private final List<Identifier> animalIds = Lists.newArrayList();

    @Override
    public Collection<Identifier> getMonsterIds() {
        return mobIds;
    }

    @Override
    public Collection<Identifier> getAnimalIds() {
        return animalIds;
    }

    private EntityRegistry init() {
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

        return this;
    }
}
