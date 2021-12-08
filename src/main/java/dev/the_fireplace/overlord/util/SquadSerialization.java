package dev.the_fireplace.overlord.util;

import dev.the_fireplace.overlord.domain.data.objects.Squad;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import java.util.Collection;

public class SquadSerialization
{
    public static NbtCompound collectionToNbt(Collection<? extends Squad> squads) {
        NbtList nbtList = new NbtList();
        for (Squad squad : squads) {
            nbtList.add(toNbt(squad));
        }

        NbtCompound compound = new NbtCompound();
        compound.put("squads", nbtList);
        return compound;
    }

    public static NbtCompound toNbt(Squad squad) {
        NbtCompound compound = new NbtCompound();
        compound.putUuid("owner", squad.getOwner());
        compound.putUuid("squadId", squad.getSquadId());
        compound.putString("name", squad.getName());
        compound.putString("capeBase", squad.getPattern());
        compound.put("capeItem", squad.getItem().writeNbt(new NbtCompound()));

        return compound;
    }

}
