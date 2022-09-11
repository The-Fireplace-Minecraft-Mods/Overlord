package dev.the_fireplace.overlord.entity.ai;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import dev.the_fireplace.overlord.domain.registry.EntityRegistry;
import dev.the_fireplace.overlord.domain.registry.EquipmentRegistry;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.core.util.UuidUtil;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.UUID;

@Singleton
public class AIListManager
{
    public static final UUID EMPTY_LIST_ID = UUID.fromString("00000000-0000-0000-0000-000000000000"),
        ALL_MOBS_LIST_ID = UUID.fromString("00000000-0000-0000-0000-000000000001"),
        ALL_ANIMALS_LIST_ID = UUID.fromString("00000000-0000-0000-0000-000000000002"),
        ALL_EQUIPMENT_LIST_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");

    private final EntityRegistry entityRegistry;
    private final EquipmentRegistry equipmentRegistry;

    @Inject
    public AIListManager(EntityRegistry entityRegistry, EquipmentRegistry equipmentRegistry) {
        this.entityRegistry = entityRegistry;
        this.equipmentRegistry = equipmentRegistry;
    }

    private final BiMap<UUID, ImmutableList<ResourceLocation>> lists = HashBiMap.create();

    public UUID getId(List<ResourceLocation> list) {
        list = deduplicateAndSort(list);
        if (lists.containsValue(list)) {
            return lists.inverse().get(list);
        }
        UUID id = UuidUtil.getTimeBasedUuid();
        lists.put(id, (ImmutableList<ResourceLocation>) list);
        return id;
    }

    private ImmutableList<ResourceLocation> deduplicateAndSort(List<ResourceLocation> list) {
        //TODO unit test as proof that it still works instead of using this comment as a reminder
        //Use ImmutableSortedSet.copyOf instead of ImmutableList.sortedCopyOf because ImmutableList does not deduplicate.
        return ImmutableSortedSet.copyOf(list).asList();
    }

    public ImmutableList<ResourceLocation> getList(UUID id) {
        if (EMPTY_LIST_ID.equals(id)) {
            return ImmutableList.of();
        } else if (ALL_MOBS_LIST_ID.equals(id)) {
            return ImmutableSortedSet.copyOf(entityRegistry.getMonsterIds()).asList();
        } else if (ALL_ANIMALS_LIST_ID.equals(id)) {
            return ImmutableSortedSet.copyOf(entityRegistry.getAnimalIds()).asList();
        } else if (ALL_EQUIPMENT_LIST_ID.equals(id)) {
            return ImmutableSortedSet.copyOf(equipmentRegistry.getEquipmentIds()).asList();
        } else {
            return lists.containsKey(id) ? lists.get(id) : ImmutableList.of();
        }
    }

    public void save() {
        //TODO
    }

    public void load() {
        //TODO
    }
}
