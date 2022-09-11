package dev.the_fireplace.overlord.entity.ai.aiconfig.movement;

import java.util.UUID;

public interface MovementCategory
{
    boolean isEnabled();

    void setEnabled(boolean enabled);

    EnumMovementMode getMoveMode();

    void setMoveMode(EnumMovementMode moveMode);

    byte getMinimumFollowDistance();

    void setMinimumFollowDistance(byte followDistance);

    byte getMaximumFollowDistance();

    void setMaximumFollowDistance(byte followDistance);

    UUID getPatrolPosList();

    void setPatrolPosList(UUID patrolPosList);

    boolean isPatrolLoop();

    void setPatrolLoop(boolean patrolLoop);

    PositionSetting getHome();

    void setHome(PositionSetting home);

    boolean isStationedReturnHome();

    void setStationedReturnHome(boolean stationedReturnHome);

    byte getMoveRadius();

    void setMoveRadius(byte moveRadius);

    boolean isExploringWander();

    void setExploringWander(boolean exploringWander);
}
