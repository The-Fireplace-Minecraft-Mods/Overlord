package dev.the_fireplace.overlord.domain.entity;

import dev.the_fireplace.overlord.entity.ai.aiconfig.AISettings;
import net.minecraft.nbt.CompoundTag;

public interface OrderableEntity {
    AISettings getAISettings();

    void updateAISettings(CompoundTag newSettings);

    int getEntityIdNumber();
}
