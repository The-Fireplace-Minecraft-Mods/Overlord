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
import java.util.ArrayList;
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
    private static final Function<UUID, ConcurrentMap<UUID, SavedSquad>> NEW_CONCURRENT_MAP = (unused) -> new ConcurrentHashMap<>();
    private static final String DATABASE = Overlord.MODID;
    private static final String TABLE = "squads";

    private final SaveDataStateManager saveDataStateManager;
    private final SaveBasedStorageReader storageReader;
    private final ConcurrentMap<UUID, ConcurrentMap<UUID, SavedSquad>> squadCache;

    @Inject
    public SquadsImpl(SaveDataStateManager saveDataStateManager, SaveBasedStorageReader storageReader) {
        this.saveDataStateManager = saveDataStateManager;
        this.storageReader = storageReader;
        this.squadCache = new ConcurrentHashMap<>();
    }

    @Nullable
    @Override
    public Squad getSquad(UUID owner, UUID squadId) {
        SavedSquad squad = squadCache.computeIfAbsent(owner, NEW_CONCURRENT_MAP).get(squadId);
        if (squad == null) {
            squad = new SavedSquad(squadId, owner);
            if (storageReader.isStored(squad)) {
                squad.init();
                squadCache.computeIfAbsent(owner, NEW_CONCURRENT_MAP).put(squadId, squad);
            } else {
                return null;
            }
        }

        return squad;
    }

    @Override
    public Squad createNewSquad(UUID owner, String pattern, ItemStack stack, String name) {
        UUID newSquadId;
        do {
            newSquadId = UUID.randomUUID();
        } while (squadCache.computeIfAbsent(owner, NEW_CONCURRENT_MAP).containsKey(newSquadId));

        SavedSquad squad = new SavedSquad(newSquadId, owner);
        squad.init();
        squad.updatePattern(pattern, stack);
        squad.setName(name);
        squadCache.computeIfAbsent(owner, NEW_CONCURRENT_MAP).put(newSquadId, squad);
        return squad;
    }

    @Override
    public void removeSquad(UUID owner, UUID squadId) {
        loadFullSquadCache();
        SavedSquad squad = squadCache.computeIfAbsent(owner, NEW_CONCURRENT_MAP).remove(squadId);
        if (squad != null) {
            squad.delete();
        }
    }

    @Override
    public Collection<? extends Squad> getSquadsWithOwner(UUID owner) {
        loadSquadCacheForOwner(owner);
        return ImmutableSet.copyOf(squadCache.get(owner).values());
    }

    @Override
    public Collection<? extends Squad> getSquads() {
        loadFullSquadCache();
        Collection<Squad> squads = new ArrayList<>();
        squadCache.values().forEach(entry -> squads.addAll(entry.values()));
        return squads;
    }

    private void loadSquadCacheForOwner(UUID owner) {
        ConcurrentMap<UUID, SavedSquad> cachedOwnerSquads = squadCache.computeIfAbsent(owner, NEW_CONCURRENT_MAP);
        Iterator<String> databaseIdIterator = storageReader.getStoredIdsIterator(DATABASE, TABLE);
        while (databaseIdIterator.hasNext()) {
            String id = databaseIdIterator.next();
            if (id.startsWith(owner.toString())) {
                UUID squadId = UUID.fromString(id.substring(owner.toString().length()));
                if (!cachedOwnerSquads.containsKey(squadId)) {
                    SavedSquad squad = new SavedSquad(squadId, owner);
                    squad.init();
                    cachedOwnerSquads.put(squadId, squad);
                }
            }
        }
    }

    private void loadFullSquadCache() {
        Overlord.getLogger().debug("Looking up Squads...");
        int uuidStringLength = UUID.randomUUID().toString().replace("-", "").length();
        Iterator<String> databaseIdIterator = storageReader.getStoredIdsIterator(DATABASE, TABLE);
        while (databaseIdIterator.hasNext()) {
            String id = databaseIdIterator.next();
            UUID owner = storedUUIDtoUUID(id.substring(0, uuidStringLength));
            UUID squadId = storedUUIDtoUUID(id.substring(uuidStringLength));
            ConcurrentMap<UUID, SavedSquad> cachedOwnerSquads = squadCache.computeIfAbsent(owner, NEW_CONCURRENT_MAP);
            if (!cachedOwnerSquads.containsKey(squadId)) {
                SavedSquad squad = new SavedSquad(squadId, owner);
                squad.init();
                cachedOwnerSquads.put(squadId, squad);
            }
        }
    }

    private UUID storedUUIDtoUUID(String storedUUID) {
        return UUID.fromString(
            storedUUID
                .replaceFirst(
                    "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"
                )
        );
    }

    private class SavedSquad implements Squad, SaveData
    {
        private final UUID id;
        private final UUID owner;
        private String pattern;
        private ItemStack item;
        private String name;

        private SavedSquad(UUID id, UUID owner) {
            this.id = id;
            this.owner = owner;
            this.pattern = "missing_texture";
            this.item = new ItemStack(Blocks.BARRIER);
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
        public String getPattern() {
            return pattern;
        }

        @Override
        public ItemStack getItem() {
            return item;
        }

        @Override
        public void updatePattern(String capeBase, ItemStack capeItem) {
            if (this.pattern.equals(capeBase) && this.item.equals(capeItem)) {
                return;
            }
            this.pattern = capeBase;
            this.item = capeItem;
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
            this.pattern = buffer.readString("pattern", this.pattern);
            try {
                this.item = ItemStack.fromNbt(StringNbtReader.parse(buffer.readString("item", "")));
            } catch (CommandSyntaxException ignored) {
            }
            this.name = buffer.readString("name", this.name);
        }

        @Override
        public void writeTo(StorageWriteBuffer buffer) {
            buffer.writeString("pattern", pattern);
            buffer.writeString("item", new StringNbtWriter().apply(item.writeNbt(new NbtCompound())));
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
