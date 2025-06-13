package dev.the_fireplace.overlord.impl.data;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.lib.api.io.injectables.SaveBasedStorageReader;
import dev.the_fireplace.lib.api.io.interfaces.access.StorageReadBuffer;
import dev.the_fireplace.lib.api.io.interfaces.access.StorageWriteBuffer;
import dev.the_fireplace.lib.api.lazyio.injectables.SaveDataStateManager;
import dev.the_fireplace.lib.api.lazyio.interfaces.SaveData;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.domain.data.PlayerAlliances;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Stream;

import static dev.the_fireplace.overlord.util.UUIDSerialization.storedUUIDtoUUID;

@Implementation
@Singleton
public final class IndividualPlayerAlliances implements PlayerAlliances
{
    private static final Function<UUID, ConcurrentMap<UUID, SavedAlliance>> NEW_CONCURRENT_MAP = (unused) -> new ConcurrentHashMap<>();
    private static final String DATABASE = OverlordConstants.MODID;
    private static final String TABLE = "alliances";

    private final SaveDataStateManager saveDataStateManager;
    private final SaveBasedStorageReader storageReader;
    private final ConcurrentMap<UUID, ConcurrentMap<UUID, SavedAlliance>> allianceCache;
    private boolean cacheLoaded = false;

    @Inject
    public IndividualPlayerAlliances(SaveDataStateManager saveDataStateManager, SaveBasedStorageReader storageReader) {
        this.saveDataStateManager = saveDataStateManager;
        this.storageReader = storageReader;
        this.allianceCache = new ConcurrentHashMap<>();
    }

    @Override
    public boolean hasAllianceWith(UUID playerId, UUID otherPlayerId) {
        return getAllianceStatus(playerId, otherPlayerId) == AllianceStatus.ALLIED
            && getAllianceStatus(otherPlayerId, playerId) == AllianceStatus.ALLIED;
    }

    @Nullable
    private AllianceStatus getAllianceStatus(UUID playerId, UUID otherPlayerId) {
        ConcurrentMap<UUID, SavedAlliance> playerCache = getAllianceCache().get(playerId);
        if (playerCache == null) {
            return null;
        }
        SavedAlliance savedAlliance = playerCache.get(otherPlayerId);
        if (savedAlliance == null) {
            return null;
        }
        return savedAlliance.getStatus();
    }

    @Override
    public boolean hasRequestedAllianceWith(UUID playerId, UUID otherPlayerId) {
        return getAllianceStatus(playerId, otherPlayerId) == AllianceStatus.ALLIED
            && getAllianceStatus(otherPlayerId, playerId) != AllianceStatus.ALLIED;
    }

    @Override
    public boolean hasDeclaredEnemy(UUID playerId, UUID otherPlayerId) {
        return getAllianceStatus(playerId, otherPlayerId) == AllianceStatus.ENEMY;
    }

    @Override
    public boolean enemyRelationExistsBetween(UUID playerId, UUID otherPlayerId) {
        return hasDeclaredEnemy(playerId, otherPlayerId)
            || hasDeclaredEnemy(otherPlayerId, playerId);
    }

    @Override
    public boolean requestAlliance(UUID playerId, UUID otherPlayerId) {
        ConcurrentMap<UUID, SavedAlliance> playerAlliances = getAllianceCache().computeIfAbsent(playerId, NEW_CONCURRENT_MAP);
        SavedAlliance savedAlliance = playerAlliances.get(otherPlayerId);
        if (savedAlliance != null) {
            return savedAlliance.setStatus(AllianceStatus.ALLIED);
        }
        SavedAlliance newAlliance = new SavedAlliance(playerId, otherPlayerId);
        newAlliance.init();
        newAlliance.setStatus(AllianceStatus.ALLIED);
        playerAlliances.put(otherPlayerId, newAlliance);
        return true;
    }

    @Override
    public boolean denyAlliance(UUID playerId, UUID otherPlayerId) {
        if (!hasRequestedAllianceWith(otherPlayerId, playerId)) {
            return false;
        }
        return removeAlliance(otherPlayerId, playerId);
    }

    @Override
    public boolean declareEnemy(UUID playerId, UUID otherPlayerId) {
        ConcurrentMap<UUID, SavedAlliance> playerAlliances = getAllianceCache().computeIfAbsent(playerId, NEW_CONCURRENT_MAP);
        SavedAlliance savedAlliance = playerAlliances.get(otherPlayerId);
        if (savedAlliance != null) {
            return savedAlliance.setStatus(AllianceStatus.ENEMY);
        }
        SavedAlliance newAlliance = new SavedAlliance(playerId, otherPlayerId);
        newAlliance.init();
        newAlliance.setStatus(AllianceStatus.ENEMY);
        playerAlliances.put(otherPlayerId, newAlliance);
        return true;
    }

    @Override
    public boolean removeAlliance(UUID playerId, UUID otherPlayerId) {
        ConcurrentMap<UUID, SavedAlliance> playerAlliances = getAllianceCache().computeIfAbsent(playerId, NEW_CONCURRENT_MAP);
        SavedAlliance savedAlliance = playerAlliances.get(otherPlayerId);
        if (savedAlliance == null || savedAlliance.getStatus() != AllianceStatus.ALLIED) {
            return false;
        }
        playerAlliances.remove(otherPlayerId);
        savedAlliance.delete();
        ConcurrentMap<UUID, SavedAlliance> otherPlayerAlliances = getAllianceCache().computeIfAbsent(otherPlayerId, NEW_CONCURRENT_MAP);
        SavedAlliance otherAlliance = otherPlayerAlliances.get(playerId);
        if (otherAlliance != null && otherAlliance.getStatus() == AllianceStatus.ALLIED) {
            otherPlayerAlliances.remove(playerId);
            otherAlliance.delete();
        }
        return true;
    }

    @Override
    public boolean removeEnemy(UUID playerId, UUID otherPlayerId) {
        ConcurrentMap<UUID, SavedAlliance> playerAlliances = getAllianceCache().computeIfAbsent(playerId, NEW_CONCURRENT_MAP);
        SavedAlliance savedAlliance = playerAlliances.get(otherPlayerId);
        if (savedAlliance == null || savedAlliance.getStatus() != AllianceStatus.ENEMY) {
            return false;
        }
        playerAlliances.remove(otherPlayerId);
        savedAlliance.delete();
        return true;
    }

    @Override
    public Stream<UUID> getEnemiesDeclaredBy(UUID playerId) {
        return getAllianceCache().computeIfAbsent(playerId, NEW_CONCURRENT_MAP).entrySet().stream()
            .filter(entry -> entry.getValue().getStatus() == AllianceStatus.ENEMY)
            .map(Map.Entry::getKey);
    }

    @Override
    public Stream<UUID> getAlliesRequestedBy(UUID playerId) {
        return getAllianceCache().computeIfAbsent(playerId, NEW_CONCURRENT_MAP).entrySet().stream()
            .filter(entry -> entry.getValue().getStatus() == AllianceStatus.ALLIED)
            .filter(entry -> {
                    SavedAlliance otherAllianceToPlayer = getAllianceCache()
                        .computeIfAbsent(entry.getKey(), NEW_CONCURRENT_MAP)
                        .get(playerId);
                    return otherAllianceToPlayer != null && otherAllianceToPlayer
                        .getStatus() != AllianceStatus.ALLIED;
                }
            )
            .map(Map.Entry::getKey);
    }

    @Override
    public Stream<UUID> getConfirmedAlliancesWith(UUID playerId) {
        return getAllianceCache().computeIfAbsent(playerId, NEW_CONCURRENT_MAP).entrySet().stream()
            .filter(entry -> entry.getValue().getStatus() == AllianceStatus.ALLIED)
            .filter(entry -> {
                    SavedAlliance otherAllianceToPlayer = getAllianceCache()
                        .computeIfAbsent(entry.getKey(), NEW_CONCURRENT_MAP)
                        .get(playerId);
                    return otherAllianceToPlayer != null && otherAllianceToPlayer
                        .getStatus() == AllianceStatus.ALLIED;
                }
            )
            .map(Map.Entry::getKey);
    }

    private enum AllianceStatus {
        ALLIED,
        ENEMY,
    }

    private ConcurrentMap<UUID, ConcurrentMap<UUID, SavedAlliance>> getAllianceCache() {
        if (!cacheLoaded) {
            cacheLoaded = true;
            OverlordConstants.getLogger().debug("Looking up alliances...");
            int uuidStringLength = UUID.randomUUID().toString().replace("-", "").length();
            Iterator<String> databaseIdIterator = storageReader.getStoredIdsIterator(DATABASE, TABLE);
            while (databaseIdIterator.hasNext()) {
                String id = databaseIdIterator.next();
                UUID playerId = storedUUIDtoUUID(id.substring(0, uuidStringLength));
                UUID otherPlayerId = storedUUIDtoUUID(id.substring(uuidStringLength));
                ConcurrentMap<UUID, SavedAlliance> cachedPlayerAlliances = allianceCache.computeIfAbsent(playerId, NEW_CONCURRENT_MAP);
                if (!cachedPlayerAlliances.containsKey(otherPlayerId)) {
                    SavedAlliance alliance = new SavedAlliance(playerId, otherPlayerId);
                    alliance.init();
                    cachedPlayerAlliances.put(otherPlayerId, alliance);
                }
            }
        }

        return allianceCache;
    }

    private class SavedAlliance implements SaveData
    {
        private final UUID playerId;
        private final UUID otherPlayerId;
        private AllianceStatus status;

        private SavedAlliance(UUID playerId, UUID otherPlayerId) {
            this.playerId = playerId;
            this.otherPlayerId = otherPlayerId;
            this.status = AllianceStatus.ENEMY;
        }

        private void init() {
            saveDataStateManager.initializeWithAutosave(this, (byte) 10);
        }

        private boolean setStatus(AllianceStatus status) {
            if (this.status == status) {
                return false;
            }
            this.status = status;
            saveDataStateManager.markChanged(this);
            return true;
        }

        private AllianceStatus getStatus() {
            return status;
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
            return playerId.toString() + otherPlayerId.toString();
        }

        @Override
        public void readFrom(StorageReadBuffer buffer) {
            int statusOrdinal = buffer.readInt("status", AllianceStatus.ENEMY.ordinal());
            this.status = AllianceStatus.values()[statusOrdinal];
        }

        @Override
        public void writeTo(StorageWriteBuffer buffer) {
            buffer.writeString("playerId", playerId.toString());
            buffer.writeString("otherPlayerId", otherPlayerId.toString());
            buffer.writeInt("status", status.ordinal());
        }

        private void delete() {
            saveDataStateManager.delete(this);
        }
    }
}
