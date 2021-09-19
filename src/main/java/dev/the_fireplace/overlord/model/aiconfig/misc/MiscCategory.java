package dev.the_fireplace.overlord.model.aiconfig.misc;

import java.util.UUID;

public interface MiscCategory {
    boolean isSaveDamagedEquipment();

    void setSaveDamagedEquipment(boolean saveDamagedEquipment);

    UUID getSaveEquipmentList();

    void setSaveEquipmentList(UUID saveEquipmentList);

    boolean isLoadChunks();

    void setLoadChunks(boolean loadChunks);
}
