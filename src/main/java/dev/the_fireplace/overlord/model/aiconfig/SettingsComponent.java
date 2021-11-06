package dev.the_fireplace.overlord.model.aiconfig;

import net.minecraft.nbt.NbtCompound;

public interface SettingsComponent
{
    NbtCompound toTag();

    void readTag(NbtCompound tag);
}
