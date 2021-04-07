package the_fireplace.overlord.model;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import dev.the_fireplace.lib.api.util.EmptyUUID;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.core.util.UuidUtil;
import the_fireplace.overlord.api.internal.EntityRegistry;
import the_fireplace.overlord.api.internal.EquipmentRegistry;
import the_fireplace.overlord.api.internal.ThrowableRegistry;

import java.util.List;
import java.util.UUID;

public class AIListManager {
    public static final UUID
        EMPTY_LIST_ID = EmptyUUID.EMPTY_UUID,
        ALL_MOBS_LIST_ID = UUID.fromString("00000000-0000-0000-0000-000000000001"),
        ALL_ANIMALS_LIST_ID = UUID.fromString("00000000-0000-0000-0000-000000000002"),
        ALL_THROWABLES_LIST_ID = UUID.fromString("00000000-0000-0000-0000-000000000003"),
        ALL_EQUIPMENT_LIST_ID = UUID.fromString("00000000-0000-0000-0000-000000000004");

    private static final BiMap<UUID, ImmutableList<Identifier>> lists = HashBiMap.create();

    public static UUID getId(List<Identifier> list) {
        //Use ImmutableSortedSet.copyOf instead of ImmutableList.sortedCopyOf because we want to deduplicate if there are duplicates as well as sort the data.
        list = ImmutableSortedSet.copyOf(list).asList();
        if(lists.containsValue(list))
            return lists.inverse().get(list);
        UUID id = UuidUtil.getTimeBasedUuid();
        lists.put(id, (ImmutableList<Identifier>)list);
        return id;
    }

    public static ImmutableList<Identifier> getList(UUID id) {
        if(EMPTY_LIST_ID.equals(id))
            return ImmutableList.of();
        else if(ALL_MOBS_LIST_ID.equals(id))
            return ImmutableSortedSet.copyOf(EntityRegistry.getInstance().getMonsterIds()).asList();
        else if(ALL_ANIMALS_LIST_ID.equals(id))
            return ImmutableSortedSet.copyOf(EntityRegistry.getInstance().getAnimalIds()).asList();
        else if(ALL_EQUIPMENT_LIST_ID.equals(id))
            return ImmutableSortedSet.copyOf(EquipmentRegistry.getInstance().getEquipmentIds()).asList();
        else if(ALL_THROWABLES_LIST_ID.equals(id))
            return ImmutableSortedSet.copyOf(ThrowableRegistry.getInstance().getThrowableIds()).asList();
        else
            return lists.containsKey(id) ? lists.get(id) : ImmutableList.of();
    }

    public static void save() {
        //TODO
    }

    public static void load() {
        //TODO
    }
}
