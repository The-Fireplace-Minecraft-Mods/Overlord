package dev.the_fireplace.overlord.model;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import dev.the_fireplace.overlord.domain.internal.EntityRegistry;
import dev.the_fireplace.overlord.domain.internal.EquipmentRegistry;
import dev.the_fireplace.overlord.domain.internal.ThrowableRegistry;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.core.util.UuidUtil;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.UUID;

@Singleton
public class AIListManager {
    public static final UUID EMPTY_LIST_ID = UUID.fromString("00000000-0000-0000-0000-000000000000"),
        ALL_MOBS_LIST_ID = UUID.fromString("00000000-0000-0000-0000-000000000001"),
        ALL_ANIMALS_LIST_ID = UUID.fromString("00000000-0000-0000-0000-000000000002"),
        ALL_THROWABLES_LIST_ID = UUID.fromString("00000000-0000-0000-0000-000000000003"),
        ALL_EQUIPMENT_LIST_ID = UUID.fromString("00000000-0000-0000-0000-000000000004");

    private final EntityRegistry entityRegistry;
    private final EquipmentRegistry equipmentRegistry;
    private final ThrowableRegistry throwableRegistry;

    @Inject
    public AIListManager(EntityRegistry entityRegistry, EquipmentRegistry equipmentRegistry, ThrowableRegistry throwableRegistry) {
        this.entityRegistry = entityRegistry;
        this.equipmentRegistry = equipmentRegistry;
        this.throwableRegistry = throwableRegistry;
    }

    private final BiMap<UUID, ImmutableList<Identifier>> lists = HashBiMap.create();

    public UUID getId(List<Identifier> list) {
        //Use ImmutableSortedSet.copyOf instead of ImmutableList.sortedCopyOf because we want to deduplicate if there are duplicates as well as sort the data.
        list = ImmutableSortedSet.copyOf(list).asList();
        if(lists.containsValue(list))
            return lists.inverse().get(list);
        UUID id = UuidUtil.getTimeBasedUuid();
        lists.put(id, (ImmutableList<Identifier>)list);
        return id;
    }

    public ImmutableList<Identifier> getList(UUID id) {
        if(EMPTY_LIST_ID.equals(id))
            return ImmutableList.of();
        else if(ALL_MOBS_LIST_ID.equals(id))
            return ImmutableSortedSet.copyOf(entityRegistry.getMonsterIds()).asList();
        else if(ALL_ANIMALS_LIST_ID.equals(id))
            return ImmutableSortedSet.copyOf(entityRegistry.getAnimalIds()).asList();
        else if(ALL_EQUIPMENT_LIST_ID.equals(id))
            return ImmutableSortedSet.copyOf(equipmentRegistry.getEquipmentIds()).asList();
        else if(ALL_THROWABLES_LIST_ID.equals(id))
            return ImmutableSortedSet.copyOf(throwableRegistry.getThrowableIds()).asList();
        else
            return lists.containsKey(id) ? lists.get(id) : ImmutableList.of();
    }

    public void save() {
        //TODO
    }

    public void load() {
        //TODO
    }
}
