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
import net.minecraft.util.Identifier;
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
    private boolean cacheLoaded = false;

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
    public Squad createNewSquad(UUID owner, Identifier patternId, ItemStack stack, String name) {
        ensureCachePopulated();
        UUID newSquadId;
        do {
            newSquadId = UUID.randomUUID();
        } while (squadCache.computeIfAbsent(owner, NEW_CONCURRENT_MAP).containsKey(newSquadId));

        SavedSquad squad = new SavedSquad(newSquadId, owner);
        squad.init();
        squad.updatePattern(patternId, stack);
        squad.setName(name);
        squadCache.computeIfAbsent(owner, NEW_CONCURRENT_MAP).put(newSquadId, squad);
        return squad;
    }

    @Override
    public boolean removeSquad(UUID owner, UUID squadId) {
        ensureCachePopulated();
        SavedSquad squad = squadCache.computeIfAbsent(owner, NEW_CONCURRENT_MAP).remove(squadId);
        if (squad != null) {
            squad.delete();
            return true;
        }
        return false;
    }

    @Override
    public Collection<? extends Squad> getSquadsWithOwner(UUID owner) {
        ensureCachePopulated();
        return ImmutableSet.copyOf(squadCache.get(owner).values());
    }

    @Override
    public Collection<? extends Squad> getSquads() {
        ensureCachePopulated();
        Collection<Squad> squads = new ArrayList<>();
        squadCache.values().forEach(entry -> squads.addAll(entry.values()));
        return squads;
    }

    private void ensureCachePopulated() {
        if (cacheLoaded) {
            return;
        }
        populateCache();
    }

    private void populateCache() {
        cacheLoaded = true;
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
        private Identifier patternId;
        private ItemStack item;
        private String name;

        private SavedSquad(UUID id, UUID owner) {
            this.id = id;
            this.owner = owner;
            this.patternId = new Identifier("");
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
        public Identifier getPatternId() {
            return patternId;
        }

        @Override
        public ItemStack getItem() {
            return item;
        }

        @Override
        public void updatePattern(Identifier patternId, ItemStack capeItem) {
            if (this.patternId.equals(patternId) && this.item.equals(capeItem)) {
                return;
            }
            this.patternId = patternId;
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
            String legacyPattern = buffer.readString("pattern", "");
            if (!legacyPattern.isBlank()) {
                //TODO 4.0.0 eliminate legacy checking
                this.patternId = new Identifier(Overlord.MODID, legacyPattern);
            } else {
                this.patternId = new Identifier(buffer.readString("patternId", ""));
            }
            try {
                this.item = ItemStack.fromNbt(StringNbtReader.parse(buffer.readString("item", "")));
            } catch (CommandSyntaxException ignored) {
            }
            this.name = buffer.readString("name", this.name);
        }

        @Override
        public void writeTo(StorageWriteBuffer buffer) {
            buffer.writeString("patternId", patternId.toString());
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
