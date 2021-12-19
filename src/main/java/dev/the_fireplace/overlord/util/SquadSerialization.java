package dev.the_fireplace.overlord.util;

import dev.the_fireplace.overlord.domain.data.objects.Squad;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.Collection;

public class SquadSerialization
{
    public static CompoundTag collectionToNbt(Collection<? extends Squad> squads) {
        ListTag nbtList = new ListTag();
        for (Squad squad : squads) {
            nbtList.add(toNbt(squad));
        }

        CompoundTag compound = new CompoundTag();
        compound.put("squads", nbtList);
        return compound;
    }

    public static CompoundTag toNbt(Squad squad) {
        CompoundTag compound = new CompoundTag();
        compound.putUuid("owner", squad.getOwner());
        compound.putUuid("squadId", squad.getSquadId());
        compound.putString("name", squad.getName());
        compound.putString("capeBase", squad.getPatternId().toString());
        compound.put("capeItem", squad.getItem().toTag(new CompoundTag()));

        return compound;
    }

}
