package dev.the_fireplace.overlord.entity.ai.aiconfig;

import net.minecraft.nbt.CompoundTag;

public interface SettingsComponent
{
    CompoundTag toTag();

    void readTag(CompoundTag tag);
}
