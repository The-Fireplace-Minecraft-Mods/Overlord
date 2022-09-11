package dev.the_fireplace.overlord.entity.ai.goal.movement;

import dev.the_fireplace.overlord.entity.ArmyEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class ReturnHomeGoal extends Goal
{
    private final ArmyEntity armyEntity;
    private final Vec3 home;
    private final double speed;
    private final PathNavigation navigation;
    private int updateCountdownTicks;
    private float oldWaterPathfindingPenalty;

    public ReturnHomeGoal(ArmyEntity armyEntity, double speed, Vec3 home) {
        this.armyEntity = armyEntity;
        this.speed = speed;
        this.navigation = armyEntity.getNavigation();
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        this.home = home;
        if (!(armyEntity.getNavigation() instanceof GroundPathNavigation) && !(armyEntity.getNavigation() instanceof FlyingPathNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for ReturnHomeGoal");
        }
    }

    @Override
    public boolean canUse() {
        return isAwayFromStation();
    }

    private boolean isAwayFromStation() {
        return armyEntity.distanceToSqr(home) > 1;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.navigation.isDone()) {
            return false;
        } else {
            return isAwayFromStation();
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
        this.navigation.stop();
        this.armyEntity.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterPathfindingPenalty);
    }

    @Override
    public void tick() {
        if (--this.updateCountdownTicks <= 0) {
            this.updateCountdownTicks = 10;
            if (!this.armyEntity.isLeashed() && !this.armyEntity.isPassenger()) {
                this.navigation.moveTo(home.x(), home.y(), home.z(), this.speed);
            }
        }
    }
}
