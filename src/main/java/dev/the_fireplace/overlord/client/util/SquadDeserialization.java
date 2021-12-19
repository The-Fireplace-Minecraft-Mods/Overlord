package dev.the_fireplace.overlord.client.util;

import dev.the_fireplace.overlord.domain.data.objects.Squad;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;

@Environment(EnvType.CLIENT)
public class SquadDeserialization
{
    private static final byte COMPOUND_TYPE = new CompoundTag().getType();

    public static Collection<? extends Squad> collectionFromNbt(CompoundTag CompoundTag) {
        Collection<Squad> squads = new ArrayList<>();
        ListTag squadsNbt = CompoundTag.getList("squads", COMPOUND_TYPE);
        for (Tag squadNbt : squadsNbt) {
            squads.add(fromNbt((CompoundTag) squadNbt));
        }

        return squads;
    }

    public static Squad fromNbt(CompoundTag compound) {
        return new ClientSquad(
            compound.getUuid("squadId"),
            compound.getUuid("owner"),
            new Identifier(compound.getString("capeBase")),
            ItemStack.fromTag(compound.getCompound("capeItem")),
            compound.getString("name")
        );
    }
}
