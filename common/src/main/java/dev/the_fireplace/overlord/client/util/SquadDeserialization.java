package dev.the_fireplace.overlord.client.util;

import dev.the_fireplace.overlord.domain.data.objects.Squad;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;

public class SquadDeserialization
{
    private static final byte COMPOUND_TYPE = new CompoundTag().getId();

    public static Collection<? extends Squad> collectionFromNbt(CompoundTag nbtCompound) {
        Collection<Squad> squads = new ArrayList<>();
        ListTag squadsNbt = nbtCompound.getList("squads", COMPOUND_TYPE);
        for (Tag squadNbt : squadsNbt) {
            squads.add(fromNbt((CompoundTag) squadNbt));
        }

        return squads;
    }

    public static Squad fromNbt(CompoundTag compound) {
        return new ClientSquad(
            compound.getUUID("squadId"),
            compound.getUUID("owner"),
            new ResourceLocation(compound.getString("capeBase")),
            ItemStack.of(compound.getCompound("capeItem")),
            compound.getString("name")
        );
    }
}
