package dev.the_fireplace.overlord.impl.data;

import com.google.common.collect.ImmutableSet;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.lib.api.io.injectables.SaveBasedStorageReader;
import dev.the_fireplace.lib.api.io.interfaces.access.StorageReadBuffer;
import dev.the_fireplace.lib.api.io.interfaces.access.StorageWriteBuffer;
import dev.the_fireplace.lib.api.lazyio.injectables.SaveDataStateManager;
import dev.the_fireplace.lib.api.lazyio.interfaces.SaveData;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.domain.data.Squads;
import dev.the_fireplace.overlord.domain.data.objects.Squad;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.nbt.visitor.StringNbtWriter;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

@Implementation
@Singleton
public final class SquadsImpl implements Squads
{
    private static final Function<UUID, ConcurrentMap<UUID, SquadImpl>> NEW_CONCURRENT_MAP = (unused) -> new ConcurrentHashMap<>();
    private static final String DATABASE = Overlord.MODID;
    private static final String TABLE = "squads";

    private final SaveDataStateManager saveDataStateManager;
    private final SaveBasedStorageReader storageReader;
    private final ConcurrentMap<UUID, ConcurrentMap<UUID, SquadImpl>> squadCache;

    @Inject
    public SquadsImpl(SaveDataStateManager saveDataStateManager, SaveBasedStorageReader storageReader) {
        this.saveDataStateManager = saveDataStateManager;
        this.storageReader = storageReader;
        this.squadCache = new ConcurrentHashMap<>();
    }

    @Nullable
    @Override
    public Squad getSquad(UUID owner, UUID squadId) {
        SquadImpl squad = squadCache.computeIfAbsent(owner, NEW_CONCURRENT_MAP).get(squadId);
        if (squad == null) {
            squad = new SquadImpl(squadId, owner);
            if (storageReader.isStored(squad)) {
                squad.init();
                squadCache.computeIfAbsent(owner, NEW_CONCURRENT_MAP).put(squadId, squad);
            } else {
                return null;
            }
        }

        return squad;
    }

    @Nullable
    @Override
    public Squad createNewSquad(UUID owner, String capeBase, ItemStack stack, String name) {
        if (!isCapeUnused(capeBase, stack)) {
            return null;
        }
        UUID newSquadId;
        do {
            newSquadId = UUID.randomUUID();
        } while (squadCache.computeIfAbsent(owner, NEW_CONCURRENT_MAP).containsKey(newSquadId));

        SquadImpl squad = new SquadImpl(newSquadId, owner);
        squad.updateCape(capeBase, stack);
        squad.setName(name);
        squad.init();
        return squad;
    }

    @Override
    public void removeSquad(UUID owner, UUID squadId) {
        //TODO handle if squad is not yet cached for some reason, if needed
        SquadImpl squad = squadCache.computeIfAbsent(owner, NEW_CONCURRENT_MAP).remove(squadId);
        if (squad != null) {
            squad.delete();
        }
    }

    @Override
    public Collection<? extends Squad> getSquadsWithOwner(UUID owner) {
        loadSquadCacheForOwner(owner);
        return ImmutableSet.copyOf(squadCache.get(owner).values());
    }

    private void loadSquadCacheForOwner(UUID owner) {
        ConcurrentMap<UUID, SquadImpl> cachedOwnerSquads = squadCache.computeIfAbsent(owner, NEW_CONCURRENT_MAP);
        Iterator<String> databaseIdIterator = storageReader.getStoredIdsIterator(DATABASE, TABLE);
        while (databaseIdIterator.hasNext()) {
            String id = databaseIdIterator.next();
            if (id.startsWith(owner.toString())) {
                UUID squadId = UUID.fromString(id.substring(owner.toString().length()));
                if (!cachedOwnerSquads.containsKey(squadId)) {
                    SquadImpl squad = new SquadImpl(squadId, owner);
                    squad.init();
                    cachedOwnerSquads.put(squadId, squad);
                }
            }
        }
    }

    @Override
    public boolean isCapeUnused(String capeBase, ItemStack stack) {
        for (ConcurrentMap<UUID, SquadImpl> squadEntries : squadCache.values()) {
            for (SquadImpl squad : squadEntries.values()) {
                if (squad.capeItem.equals(stack) && squad.capeBase.equals(capeBase)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public boolean canUseCapeBase(UUID player, String capeBase) {
        //TODO adjust when more capes are added
        return true;
    }

    private class SquadImpl implements Squad, SaveData
    {
        private final UUID id;
        private final UUID owner;
        private String capeBase;
        private ItemStack capeItem;
        private String name;

        private SquadImpl(UUID id, UUID owner) {
            this.id = id;
            this.owner = owner;
            this.capeBase = "missing_texture";
            this.capeItem = new ItemStack(Blocks.BARRIER);
            this.name = "Missingno";
        }

        private void init() {
            saveDataStateManager.initializeWithAutosave(this, (byte) 10);
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
        public String getCapeBase() {
            return capeBase;
        }

        @Override
        public ItemStack getCapeItem() {
            return capeItem;
        }

        @Override
        public void updateCape(String capeBase, ItemStack capeItem) {
            if (this.capeBase.equals(capeBase) && this.capeItem.equals(capeItem)) {
                return;
            }
            if (!isCapeUnused(capeBase, capeItem)) {
                return;
            }
            this.capeBase = capeBase;
            this.capeItem = capeItem;
            saveDataStateManager.markChanged(this);
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setName(String name) {
            this.name = name;
            saveDataStateManager.markChanged(this);
        }

        @Override
        public void readFrom(StorageReadBuffer buffer) {
            this.capeBase = buffer.readString("capeBase", this.capeBase);
            try {
                this.capeItem = ItemStack.fromNbt(StringNbtReader.parse(buffer.readString("capeItem", "")));
            } catch (CommandSyntaxException ignored) {
            }
            this.name = buffer.readString("name", this.name);
        }

        @Override
        public void writeTo(StorageWriteBuffer buffer) {
            buffer.writeString("capeBase", capeBase);
            buffer.writeString("capeItem", new StringNbtWriter().apply(capeItem.writeNbt(new NbtCompound())));
            buffer.writeString("name", name);
        }

        @Override
        public String getDatabase() {
            return DATABASE;
        }

        @Override
        public String getTable() {
            return TABLE;
        }

        @Override
        public String getId() {
            return getOwner().toString() + getSquadId().toString();
        }

        private void delete() {
            saveDataStateManager.delete(this);
        }
    }
}
