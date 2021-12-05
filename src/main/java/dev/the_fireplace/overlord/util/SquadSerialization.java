package dev.the_fireplace.overlord.util;

import dev.the_fireplace.overlord.domain.data.objects.Squad;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class SquadSerialization
{
    private static final byte COMPOUND_TYPE = new NbtCompound().getType();

    public static NbtCompound collectionToNbt(Collection<? extends Squad> squads) {
        NbtList nbtList = new NbtList();
        for (Squad squad : squads) {
            nbtList.add(toNbt(squad));
        }

        NbtCompound compound = new NbtCompound();
        compound.put("squads", nbtList);
        return compound;
    }

    @Environment(EnvType.CLIENT)
    public static Collection<? extends Squad> collectionFromNbt(NbtCompound nbtCompound) {
        Collection<Squad> squads = new ArrayList<>();
        NbtList squadsNbt = nbtCompound.getList("squads", COMPOUND_TYPE);
        for (NbtElement squadNbt : squadsNbt) {
            squads.add(fromNbt((NbtCompound) squadNbt));
        }

        return squads;
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

    @Environment(EnvType.CLIENT)
    public static Squad fromNbt(NbtCompound compound) {
        return new ClientSquad(
            compound.getUuid("squadId"),
            compound.getUuid("owner"),
            compound.getString("capeBase"),
            ItemStack.fromNbt(compound.getCompound("capeItem")),
            compound.getString("name")
        );
    }

    @Environment(EnvType.CLIENT)
    private static class ClientSquad implements Squad
    {
        private final UUID id;
        private final UUID owner;
        private final String capeBase;
        private final ItemStack capeItem;
        private final String name;

        private ClientSquad(UUID id, UUID owner, String capeBase, ItemStack capeItem, String name) {
            this.id = id;
            this.owner = owner;
            this.capeBase = capeBase;
            this.capeItem = capeItem;
            this.name = name;
        }

        @Override
        public UUID getSquadId() {
            return id;
        }

        @Override
        public UUID getOwner() {
            return owner;
        }

        @Override
        public String getPattern() {
            return capeBase;
        }

        @Override
        public ItemStack getItem() {
            return capeItem;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void updatePattern(String capeBase, ItemStack capeItem) {
            throw new UnsupportedOperationException("Cannot save squad data on the client!");
        }

        @Override
        public void setName(String name) {
            throw new UnsupportedOperationException("Cannot save squad data on the client!");
        }
    }
}
