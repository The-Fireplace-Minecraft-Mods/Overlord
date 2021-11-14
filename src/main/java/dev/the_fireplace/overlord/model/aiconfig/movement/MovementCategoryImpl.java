package dev.the_fireplace.overlord.model.aiconfig.movement;

import dev.the_fireplace.overlord.model.AIListManager;
import dev.the_fireplace.overlord.model.aiconfig.SettingsComponent;
import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

public final class MovementCategoryImpl implements SettingsComponent, MovementCategory
{

    private boolean enabled = true;

    //Movement modes
    private EnumMovementMode moveMode = EnumMovementMode.FOLLOW;
    //How close the skeleton follows the player
    private byte minimumFollowDistance = 6;
    private byte maximumFollowDistance = 8;
    private UUID patrolPosList = AIListManager.EMPTY_LIST_ID;
    //Should the skeleton go from last directly to first or turn around and navigate the path in reverse?
    private boolean patrolLoop = false;
    private PositionSetting home = new PositionSetting(0, 0, 0);
    //Should the stationed skeleton return to the home position after walking away to attack
    private boolean stationedReturnHome = true;
    //The radius away from home the skeleton can go when attacking while stationed or wandering the area
    private byte moveRadius = 16;
    //Should the skeleton actively explore when wandering?
    private boolean exploringWander = false;

    @Override
    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();

        tag.putBoolean("enabled", enabled);
        tag.putString("moveMode", moveMode.toString());
        tag.putByte("minimumFollowDistance", minimumFollowDistance);
        tag.putByte("maximumFollowDistance", maximumFollowDistance);
        tag.putUuid("patrolPosList", patrolPosList);
        tag.putBoolean("patrolLoop", patrolLoop);
        if (home != null) {
            tag.put("home", home.toTag());
        }
        tag.putBoolean("stationedReturnHome", stationedReturnHome);
        tag.putByte("moveRadius", moveRadius);
        tag.putBoolean("exploringWander", exploringWander);

        return tag;
    }

    @Override
    public void readTag(CompoundTag tag) {
        if (tag.contains("enabled")) {
            enabled = tag.getBoolean("enabled");
        }
        if (tag.contains("moveMode")) {
            moveMode = EnumMovementMode.valueOf(tag.getString("moveMode"));
        }
        if (tag.contains("minimumFollowDistance")) {
            minimumFollowDistance = tag.getByte("minimumFollowDistance");
        }
        if (tag.contains("maximumFollowDistance")) {
            maximumFollowDistance = tag.getByte("maximumFollowDistance");
        }
        if (tag.contains("patrolPosList")) {
            patrolPosList = tag.getUuid("patrolPosList");
        }
        if (tag.contains("patrolLoop")) {
            patrolLoop = tag.getBoolean("patrolLoop");
        }
        if (tag.contains("home")) {
            home.readTag(tag.getCompound("home"));
        }
        if (tag.contains("stationedReturnHome")) {
            stationedReturnHome = tag.getBoolean("stationedReturnHome");
        }
        if (tag.contains("moveRadius")) {
            moveRadius = tag.getByte("moveRadius");
        }
        if (tag.contains("exploringWander")) {
            exploringWander = tag.getBoolean("exploringWander");
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public EnumMovementMode getMoveMode() {
        return moveMode;
    }

    @Override
    public void setMoveMode(EnumMovementMode moveMode) {
        this.moveMode = moveMode;
    }

    @Override
    public byte getMinimumFollowDistance() {
        return this.minimumFollowDistance;
    }

    @Override
    public void setMinimumFollowDistance(byte followDistance) {
        this.minimumFollowDistance = followDistance;
    }

    @Override
    public byte getMaximumFollowDistance() {
        return this.maximumFollowDistance;
    }

    @Override
    public void setMaximumFollowDistance(byte followDistance) {
        this.maximumFollowDistance = followDistance;
    }

    @Override
    public UUID getPatrolPosList() {
        return patrolPosList;
    }

    @Override
    public void setPatrolPosList(UUID patrolPosList) {
        this.patrolPosList = patrolPosList;
    }

    @Override
    public boolean isPatrolLoop() {
        return patrolLoop;
    }

    @Override
    public void setPatrolLoop(boolean patrolLoop) {
        this.patrolLoop = patrolLoop;
    }

    @Override
    public PositionSetting getHome() {
        return home;
    }

    @Override
    public void setHome(PositionSetting home) {
        this.home = home;
    }

    @Override
    public boolean isStationedReturnHome() {
        return stationedReturnHome;
    }

    @Override
    public void setStationedReturnHome(boolean stationedReturnHome) {
        this.stationedReturnHome = stationedReturnHome;
    }

    @Override
    public byte getMoveRadius() {
        return moveRadius;
    }

    @Override
    public void setMoveRadius(byte moveRadius) {
        this.moveRadius = moveRadius;
    }

    @Override
    public boolean isExploringWander() {
        return exploringWander;
    }

    @Override
    public void setExploringWander(boolean exploringWander) {
        this.exploringWander = exploringWander;
    }
}
