package dev.the_fireplace.overlord.model.aiconfig.misc;

import dev.the_fireplace.overlord.model.AIListManager;
import dev.the_fireplace.overlord.model.aiconfig.SettingsComponent;
import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

public class MiscCategory implements SettingsComponent {

    private boolean saveDamagedEquipment = false;
    private UUID saveEquipmentList = AIListManager.ALL_EQUIPMENT_LIST_ID;
    private boolean loadChunks = false;

    @Override
    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();

        tag.putBoolean("saveDamagedEquipment", saveDamagedEquipment);
        tag.putUuid("saveEquipmentList", saveEquipmentList);
        tag.putBoolean("loadChunks", loadChunks);

        return tag;
    }

    @Override
    public void readTag(CompoundTag tag) {
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
}
