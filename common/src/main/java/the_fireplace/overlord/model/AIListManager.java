package the_fireplace.overlord.model;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import org.apache.logging.log4j.core.util.UuidUtil;
import the_fireplace.overlord.OverlordHelper;

import java.util.List;
import java.util.UUID;

public class AIListManager {
    public static final UUID
        EMPTY_LIST_ID = UUID.fromString("00000000-0000-0000-0000-000000000000"),
        ALL_MOBS_LIST_ID = UUID.fromString("00000000-0000-0000-0000-000000000001"),
        ALL_ANIMALS_LIST_ID = UUID.fromString("00000000-0000-0000-0000-000000000002"),
        ALL_THROWABLES_LIST_ID = UUID.fromString("00000000-0000-0000-0000-000000000003"),
        ALL_EQUIPMENT_LIST_ID = UUID.fromString("00000000-0000-0000-0000-000000000004");

    private static BiMap<UUID, ImmutableList<String>> lists = HashBiMap.create();

    public static UUID getId(List<String> list) {
        //Use ImmutableSortedSet.copyOf instead of ImmutableList.sortedCopyOf because we want to deduplicate if there are duplicates as well as sort the data.
        list = ImmutableSortedSet.copyOf(list).asList();
        if(lists.containsValue(list))
            return lists.inverse().get(list);
        UUID id = UuidUtil.getTimeBasedUuid();
        lists.put(id, (ImmutableList<String>)list);
        return id;
    }

    public static ImmutableList<String> getList(UUID id) {
        if(EMPTY_LIST_ID.equals(id))
            return ImmutableList.of();
        else if(ALL_MOBS_LIST_ID.equals(id))
            return ImmutableSortedSet.copyOf(OverlordHelper.getLoaderHelper().getMobIds()).asList();
        else if(ALL_ANIMALS_LIST_ID.equals(id))
            return ImmutableSortedSet.copyOf(OverlordHelper.getLoaderHelper().getAnimalIds()).asList();
        return lists.containsKey(id) ? lists.get(id) : ImmutableList.of();
    }
}
