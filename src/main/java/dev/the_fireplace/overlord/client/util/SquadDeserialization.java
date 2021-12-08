package dev.the_fireplace.overlord.client.util;

import dev.the_fireplace.overlord.domain.data.objects.Squad;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.ArrayList;
import java.util.Collection;

@Environment(EnvType.CLIENT)
public class SquadDeserialization
{
    private static final byte COMPOUND_TYPE = new NbtCompound().getType();

    public static Collection<? extends Squad> collectionFromNbt(NbtCompound nbtCompound) {
        Collection<Squad> squads = new ArrayList<>();
        NbtList squadsNbt = nbtCompound.getList("squads", COMPOUND_TYPE);
        for (NbtElement squadNbt : squadsNbt) {
            squads.add(fromNbt((NbtCompound) squadNbt));
        }

        return squads;
    }

    public static Squad fromNbt(NbtCompound compound) {
        return new ClientSquad(
            compound.getUuid("squadId"),
            compound.getUuid("owner"),
            compound.getString("capeBase"),
            ItemStack.fromNbt(compound.getCompound("capeItem")),
            compound.getString("name")
        );
    }
}
