package dev.the_fireplace.overlord.model.aiconfig.movement;

import java.util.UUID;

public interface MovementCategory {
    boolean isEnabled();

    void setEnabled(boolean enabled);

    EnumMovementMode getMoveMode();

    void setMoveMode(EnumMovementMode moveMode);

    byte getFollowDistance();//TODO min/max follow distance?

    void setFollowDistance(byte followDistance);

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
