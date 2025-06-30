package dev.the_fireplace.overlord.impl.data;

import com.google.inject.Singleton;
import dev.the_fireplace.lib.api.io.injectables.SaveBasedStorageReader;
import dev.the_fireplace.lib.api.io.interfaces.access.StorageReadBuffer;
import dev.the_fireplace.lib.api.io.interfaces.access.StorageWriteBuffer;
import dev.the_fireplace.lib.api.lazyio.injectables.SaveDataStateManager;
import dev.the_fireplace.lib.api.lazyio.interfaces.SaveData;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.command.commands.helper.AllianceNotificationSender;
import jakarta.inject.Inject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static dev.the_fireplace.overlord.util.UUIDSerialization.storedUUIDtoUUID;

@Singleton
public final class AllianceUpdateMessageQueue
{
    private static final String DATABASE = OverlordConstants.MODID;
    private static final String TABLE = "pending_alliance_updates";
    private final SaveDataStateManager saveDataStateManager;
    private final SaveBasedStorageReader storageReader;
    private final ConcurrentMap<UUID, OperationNotificationQueue> notificationQueues;
    private final AtomicBoolean isLoaded = new AtomicBoolean(false);

    @Inject
    public AllianceUpdateMessageQueue(SaveDataStateManager saveDataStateManager, SaveBasedStorageReader storageReader) {
        this.saveDataStateManager = saveDataStateManager;
        this.storageReader = storageReader;
        this.notificationQueues = new ConcurrentHashMap<>();
    }

    public void queueNotification(UUID playerId, UUID otherPlayerId, AllianceNotificationSender.Notification operation) {
        getQueues()
            .computeIfAbsent(playerId, unused -> {
                OperationNotificationQueue newQueue = new OperationNotificationQueue(playerId);
                newQueue.init();
                return newQueue;
            })
            .queueNotification(otherPlayerId, operation);
    }

    public Map<UUID, AllianceNotificationSender.Notification> popQueue(UUID playerId) {
        OperationNotificationQueue removedQueue = getQueues().remove(playerId);
        if (removedQueue == null) {
            return new HashMap<>();
        }
        return removedQueue.destruct();
    }

    private synchronized ConcurrentMap<UUID, OperationNotificationQueue> getQueues() {
        if (!isLoaded.get()) {
            isLoaded.set(true);
            OverlordConstants.getLogger().debug("Looking up alliances...");
            Iterator<String> databaseIdIterator = storageReader.getStoredIdsIterator(DATABASE, TABLE);
            while (databaseIdIterator.hasNext()) {
                String id = databaseIdIterator.next();
                UUID playerId = storedUUIDtoUUID(id);
                OperationNotificationQueue playerNotificationQueue = new OperationNotificationQueue(playerId);
                playerNotificationQueue.init();
                this.notificationQueues.put(playerId, playerNotificationQueue);
            }
        }

        return notificationQueues;
    }

    private class OperationNotificationQueue implements SaveData
    {
        private final UUID playerId;
        private final Map<UUID, AllianceNotificationSender.Notification> pendingOperationNotifications;

        private OperationNotificationQueue(UUID playerId) {
            this.playerId = playerId;
            this.pendingOperationNotifications = new ConcurrentHashMap<>();
        }

        private void init() {
            saveDataStateManager.initializeWithAutosave(this, (byte) 10);
        }

        private void queueNotification(UUID otherPlayerId, AllianceNotificationSender.Notification operation) {
            pendingOperationNotifications.put(otherPlayerId, operation);
            saveDataStateManager.markChanged(this);
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
            return playerId.toString();
        }

        @Override
        public void readFrom(StorageReadBuffer buffer) {
            Arrays.stream(buffer.readString("pendingNotifications", "").split("\\|"))
                .forEach(pendingNotification -> {
                    if (pendingNotification.isEmpty()) {
                        return;
                    }
                    String[] split = pendingNotification.split(":");
                    UUID otherPlayerId = storedUUIDtoUUID(split[0]);
                    AllianceNotificationSender.Notification operation = AllianceNotificationSender.Notification.values()[Integer.parseInt(split[1])];
                    pendingOperationNotifications.put(otherPlayerId, operation);
                });
        }

        @Override
        public void writeTo(StorageWriteBuffer buffer) {
            buffer.writeString("pendingNotifications", pendingOperationNotifications.entrySet().stream()
                .map(entry -> entry.getKey().toString() + ":" + entry.getValue().ordinal() + "|")
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString()
            );
        }

        private Map<UUID, AllianceNotificationSender.Notification> destruct() {
            saveDataStateManager.delete(this);
            return this.pendingOperationNotifications;
        }
    }
}
