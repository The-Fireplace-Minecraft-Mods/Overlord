package dev.the_fireplace.overlord.entity.ai.aiconfig;

import net.minecraft.nbt.NbtCompound;

public interface SettingsComponent
{
    NbtCompound toTag();

    void readTag(NbtCompound tag);
}
