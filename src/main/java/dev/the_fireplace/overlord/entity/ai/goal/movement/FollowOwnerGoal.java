package dev.the_fireplace.overlord.entity.ai.goal.movement;

import dev.the_fireplace.overlord.entity.ArmyEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

import java.util.EnumSet;

public class FollowOwnerGoal extends Goal
{
    private final ArmyEntity armyEntity;
    private Entity owner;
    private final WorldView world;
    private final double speed;
    private final EntityNavigation navigation;
    private int updateCountdownTicks;
    private final float maxDistance;
    private final float minDistance;
    private float oldWaterPathfindingPenalty;
    private final boolean leavesAllowed;

    public FollowOwnerGoal(ArmyEntity armyEntity, double speed, float minDistance, float maxDistance, boolean leavesAllowed) {
        this.armyEntity = armyEntity;
        this.world = armyEntity.world;
        this.speed = speed;
        this.navigation = armyEntity.getNavigation();
        //TODO look more at the minDistance and maxDistance usages - it seems they aren't used like their names imply
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.leavesAllowed = leavesAllowed;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        if (!(armyEntity.getNavigation() instanceof MobNavigation) && !(armyEntity.getNavigation() instanceof BirdNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    }

    @Override
    public boolean canStart() {
        LivingEntity owner = this.armyEntity.getOwner();
        if (owner == null) {
            return false;
        } else if (owner.isSpectator()) {
            return false;
        } else if (this.armyEntity.squaredDistanceTo(owner) < (double) (this.minDistance * this.minDistance)) {
            return false;
        } else {
            this.owner = owner;
            return true;
        }
    }

    @Override
    public boolean shouldContinue() {
        if (this.navigation.isIdle()) {
            return false;
        } else {
            return !(this.armyEntity.squaredDistanceTo(this.owner) <= (double) (this.maxDistance * this.maxDistance));
        }
    }

    @Override
    public void start() {
        this.updateCountdownTicks = 0;
        this.oldWaterPathfindingPenalty = this.armyEntity.getPathfindingPenalty(PathNodeType.WATER);
        this.armyEntity.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
    }

    @Override
    public void stop() {
        this.owner = null;
        this.navigation.stop();
        this.armyEntity.setPathfindingPenalty(PathNodeType.WATER, this.oldWaterPathfindingPenalty);
    }

    @Override
    public void tick() {
        this.armyEntity.getLookControl().lookAt(this.owner, 10.0F, (float) this.armyEntity.getLookPitchSpeed());
        if (--this.updateCountdownTicks <= 0) {
            this.updateCountdownTicks = 10;
            if (!this.armyEntity.isLeashed() && !this.armyEntity.hasVehicle()) {
                double distanceToOwner = Math.sqrt(this.armyEntity.squaredDistanceTo(this.owner));
                if (distanceToOwner > maxDistance && distanceToOwner > minDistance) {
                    this.tryTeleport();
                } else {
                    this.navigation.startMovingTo(this.owner, this.speed);
                }

            }
        }
    }

    private void tryTeleport() {
        BlockPos ownerPos = new BlockPos(this.owner);

        for (int i = 0; i < 10; ++i) {
            //TODO improve this, we want to put them somewhere random just outside the minimum
            int xOffset = this.getRandomInt(-3, 3);
            int yOffset = this.getRandomInt(-1, 1);
            int zOffset = this.getRandomInt(-3, 3);
            boolean wasTeleported = this.tryTeleportTo(ownerPos.getX() + xOffset, ownerPos.getY() + yOffset, ownerPos.getZ() + zOffset);
            if (wasTeleported) {
                return;
            }
        }
    }

    private boolean tryTeleportTo(int x, int y, int z) {
        if (Math.abs((double) x - this.owner.getX()) < 2.0D && Math.abs((double) z - this.owner.getZ()) < 2.0D) {
            return false;
        } else if (!this.canTeleportTo(new BlockPos(x, y, z))) {
            return false;
        } else {
            this.armyEntity.refreshPositionAndAngles((float) x + 0.5F, y, (float) z + 0.5F, this.armyEntity.yaw, this.armyEntity.pitch);
            this.navigation.stop();
            return true;
        }
    }

    private boolean canTeleportTo(BlockPos pos) {
        PathNodeType pathNodeType = LandPathNodeMaker.getPathNodeType(this.world, pos.getX(), pos.getY(), pos.getZ());
        if (pathNodeType != PathNodeType.WALKABLE) {
            return false;
        } else {
            BlockState blockState = this.world.getBlockState(pos.down());
            if (!this.leavesAllowed && blockState.getBlock() instanceof LeavesBlock) {
                return false;
            } else {
                BlockPos blockPos = pos.subtract(new BlockPos(this.armyEntity));
                return this.world.doesNotCollide(this.armyEntity, this.armyEntity.getBoundingBox().offset(blockPos));
            }
        }
    }

    private int getRandomInt(int min, int max) {
        return this.armyEntity.getRandom().nextInt(max - min + 1) + min;
    }
}
