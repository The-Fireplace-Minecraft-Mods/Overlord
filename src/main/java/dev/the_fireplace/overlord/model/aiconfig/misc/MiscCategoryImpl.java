package dev.the_fireplace.overlord.model.aiconfig.misc;

import dev.the_fireplace.overlord.model.AIListManager;
import dev.the_fireplace.overlord.model.aiconfig.SettingsComponent;
import net.minecraft.nbt.NbtCompound;

import java.util.UUID;

public final class MiscCategoryImpl implements SettingsComponent, MiscCategory {

    private boolean saveDamagedEquipment = false;
    private UUID saveEquipmentList = AIListManager.ALL_EQUIPMENT_LIST_ID;
    private boolean loadChunks = false;

    @Override
    public NbtCompound toTag() {
        NbtCompound tag = new NbtCompound();

        tag.putBoolean("saveDamagedEquipment", saveDamagedEquipment);
        tag.putUuid("saveEquipmentList", saveEquipmentList);
        tag.putBoolean("loadChunks", loadChunks);

        return tag;
    }

    @Override
    public void readTag(NbtCompound tag) {
        if (tag.contains("saveDamagedEquipment")) {
            saveDamagedEquipment = tag.getBoolean("saveDamagedEquipment");
        }
        if (tag.contains("saveEquipmentList")) {
            saveEquipmentList = tag.getUuid("saveEquipmentList");
        }
        if (tag.contains("loadChunks")) {
            loadChunks = tag.getBoolean("loadChunks");
        }
    }

    @Override
    public boolean isSaveDamagedEquipment() {
        return saveDamagedEquipment;
    }

    @Override
    public void setSaveDamagedEquipment(boolean saveDamagedEquipment) {
        this.saveDamagedEquipment = saveDamagedEquipment;
    }

    @Override
    public UUID getSaveEquipmentList() {
        return saveEquipmentList;
    }

    @Override
    public void setSaveEquipmentList(UUID saveEquipmentList) {
        this.saveEquipmentList = saveEquipmentList;
    }

    @Override
    public boolean isLoadChunks() {
        return loadChunks;
    }

    @Override
    public void setLoadChunks(boolean loadChunks) {
        this.loadChunks = loadChunks;
    }
}
