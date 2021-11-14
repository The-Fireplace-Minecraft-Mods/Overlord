package dev.the_fireplace.overlord.domain.entity;

import dev.the_fireplace.overlord.entity.ai.aiconfig.AISettings;
import net.minecraft.nbt.NbtCompound;

public interface OrderableEntity {
    AISettings getAISettings();

    void updateAISettings(NbtCompound newSettings);

    int getEntityIdNumber();
}
