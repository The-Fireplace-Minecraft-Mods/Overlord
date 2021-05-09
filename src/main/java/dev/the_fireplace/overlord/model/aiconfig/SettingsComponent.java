package dev.the_fireplace.overlord.model.aiconfig;

import net.minecraft.nbt.CompoundTag;

public interface SettingsComponent {
    CompoundTag toTag();
    void readTag(CompoundTag tag);
}
