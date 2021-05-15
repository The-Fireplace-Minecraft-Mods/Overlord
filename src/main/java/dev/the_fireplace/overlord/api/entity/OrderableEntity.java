package dev.the_fireplace.overlord.api.entity;

import dev.the_fireplace.overlord.model.aiconfig.AISettings;

public interface OrderableEntity {
    AISettings getAISettings();
    void updateAISettings(AISettings newSettings);
    int getEntityId();
}
