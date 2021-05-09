package dev.the_fireplace.overlord.model.aiconfig.movement;

import dev.the_fireplace.overlord.model.AIListManager;
import dev.the_fireplace.overlord.model.aiconfig.SettingsComponent;
import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

public class MovementCategory implements SettingsComponent {

    private boolean enabled = true;

    //Movement modes
    private EnumMovementMode moveMode = EnumMovementMode.STATIONED;
    //How close the skeleton follows the player
    private byte followDistance = 8;
    private UUID patrolPosList = AIListManager.EMPTY_LIST_ID;
    //Should the skeleton go from last directly to first or turn around and navigate the path in reverse?
    private boolean patrolLoop = false;
    private PositionSetting home = null;
    //Should the stationed skeleton return to the home position after walking away to attack
    private boolean stationedReturnHome = true;
    //The radius away from home the skeleton can go when attacking while stationed or wandering the area
    private byte moveRadius = 16;
    //Should the skeleton actively explore when wandering?
    private boolean exploringWander = false;

    private final Access access = new Access();

    public Access getData() {
        return access;
    }

    @Override
    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();

        tag.putBoolean("enabled", enabled);
        tag.putString("moveMode", moveMode.toString());
        tag.putByte("followDistance", followDistance);
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
        if (tag.contains("followDistance")) {
            followDistance = tag.getByte("followDistance");
        }
        if (tag.contains("patrolPosList")) {
            patrolPosList = tag.getUuid("patrolPosList");
        }
        if (tag.contains("patrolLoop")) {
            patrolLoop = tag.getBoolean("patrolLoop");
        }
        if (tag.contains("home")) {
            if (home == null) {
                home = new PositionSetting(0, 0, 0);
            }
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

    public final class Access {
        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            MovementCategory.this.enabled = enabled;
        }

        public EnumMovementMode getMoveMode() {
            return moveMode;
        }

        public void setMoveMode(EnumMovementMode moveMode) {
            MovementCategory.this.moveMode = moveMode;
        }

        public byte getFollowDistance() {
            return followDistance;
        }

        public void setFollowDistance(byte followDistance) {
            MovementCategory.this.followDistance = followDistance;
        }

        public UUID getPatrolPosList() {
            return patrolPosList;
        }

        public void setPatrolPosList(UUID patrolPosList) {
            MovementCategory.this.patrolPosList = patrolPosList;
        }

        public boolean isPatrolLoop() {
            return patrolLoop;
        }

        public void setPatrolLoop(boolean patrolLoop) {
            MovementCategory.this.patrolLoop = patrolLoop;
        }

        public PositionSetting getHome() {
            return home;
        }

        public void setHome(PositionSetting home) {
            MovementCategory.this.home = home;
        }

        public boolean isStationedReturnHome() {
            return stationedReturnHome;
        }

        public void setStationedReturnHome(boolean stationedReturnHome) {
            MovementCategory.this.stationedReturnHome = stationedReturnHome;
        }

        public byte getMoveRadius() {
            return moveRadius;
        }

        public void setMoveRadius(byte moveRadius) {
            MovementCategory.this.moveRadius = moveRadius;
        }

        public boolean isExploringWander() {
            return exploringWander;
        }

        public void setExploringWander(boolean exploringWander) {
            MovementCategory.this.exploringWander = exploringWander;
        }
    }
}
