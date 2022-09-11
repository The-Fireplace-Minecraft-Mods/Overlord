package dev.the_fireplace.overlord.entity.ai.goal.movement;

import dev.the_fireplace.overlord.entity.ArmyEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

import java.util.EnumSet;

public class FollowOwnerGoal extends Goal
{
    private final ArmyEntity armyEntity;
    private Entity owner;
    private final LevelReader world;
    private final double speed;
    private final PathNavigation navigation;
    private int updateCountdownTicks;
    private final float maxDistance;
    private final float minDistance;
    private float oldWaterPathfindingPenalty;
    private final boolean leavesAllowed;

    public FollowOwnerGoal(ArmyEntity armyEntity, double speed, float minDistance, float maxDistance, boolean leavesAllowed) {
        this.armyEntity = armyEntity;
        this.world = armyEntity.level;
        this.speed = speed;
        this.navigation = armyEntity.getNavigation();
        //TODO look more at the minDistance and maxDistance usages - it seems they aren't used like their names imply
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.leavesAllowed = leavesAllowed;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        if (!(armyEntity.getNavigation() instanceof GroundPathNavigation) && !(armyEntity.getNavigation() instanceof FlyingPathNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    }

    @Override
    public boolean canUse() {
        LivingEntity owner = this.armyEntity.getOwner();
        if (owner == null) {
            return false;
        } else if (owner.isSpectator()) {
            return false;
        } else if (this.armyEntity.distanceToSqr(owner) < (double) (this.minDistance * this.minDistance)) {
            return false;
        } else {
            this.owner = owner;
            return true;
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (this.navigation.isDone()) {
            return false;
        } else {
            return !(this.armyEntity.distanceToSqr(this.owner) <= (double) (this.maxDistance * this.maxDistance));
        }
    }

    @Override
    public void start() {
        this.updateCountdownTicks = 0;
        this.oldWaterPathfindingPenalty = this.armyEntity.getPathfindingMalus(BlockPathTypes.WATER);
        this.armyEntity.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    }

    @Override
    public void stop() {
        this.owner = null;
        this.navigation.stop();
        this.armyEntity.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterPathfindingPenalty);
    }

    @Override
    public void tick() {
        this.armyEntity.getLookControl().setLookAt(this.owner, 10.0F, (float) this.armyEntity.getMaxHeadXRot());
        if (--this.updateCountdownTicks <= 0) {
            this.updateCountdownTicks = 10;
            if (!this.armyEntity.isLeashed() && !this.armyEntity.isPassenger()) {
                double distanceToOwner = Math.sqrt(this.armyEntity.distanceToSqr(this.owner));
                if (distanceToOwner > maxDistance && distanceToOwner > minDistance) {
                    this.tryTeleport();
                } else {
                    this.navigation.moveTo(this.owner, this.speed);
                }

            }
        }
    }

    private void tryTeleport() {
        BlockPos ownerPos = new BlockPos(this.owner.position());

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
            this.armyEntity.moveTo((float) x + 0.5F, y, (float) z + 0.5F, this.armyEntity.yRot, this.armyEntity.xRot);
            this.navigation.stop();
            return true;
        }
    }

    private boolean canTeleportTo(BlockPos pos) {
        BlockPathTypes pathNodeType = WalkNodeEvaluator.getBlockPathTypeStatic(this.world, new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ()));
        if (pathNodeType != BlockPathTypes.WALKABLE) {
            return false;
        } else {
            BlockState blockState = this.world.getBlockState(pos.below());
            if (!this.leavesAllowed && blockState.getBlock() instanceof LeavesBlock) {
                return false;
            } else {
                BlockPos blockPos = pos.subtract(new BlockPos(this.armyEntity.position()));
                return this.world.noCollision(this.armyEntity, this.armyEntity.getBoundingBox().move(blockPos));
            }
        }
    }

    private int getRandomInt(int min, int max) {
        return this.armyEntity.getRandom().nextInt(max - min + 1) + min;
    }
}
