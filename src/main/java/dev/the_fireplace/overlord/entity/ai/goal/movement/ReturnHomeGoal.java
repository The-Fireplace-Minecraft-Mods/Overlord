package dev.the_fireplace.overlord.entity.ai.goal.movement;

import dev.the_fireplace.overlord.entity.ArmyEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class ReturnHomeGoal extends Goal
{
    private final ArmyEntity armyEntity;
    private final Vec3d home;
    private final double speed;
    private final EntityNavigation navigation;
    private int updateCountdownTicks;
    private float oldWaterPathfindingPenalty;

    public ReturnHomeGoal(ArmyEntity armyEntity, double speed, Vec3d home) {
        this.armyEntity = armyEntity;
        this.speed = speed;
        this.navigation = armyEntity.getNavigation();
        this.setControls(EnumSet.of(Goal.Control.MOVE));
        this.home = home;
        if (!(armyEntity.getNavigation() instanceof MobNavigation) && !(armyEntity.getNavigation() instanceof BirdNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for ReturnHomeGoal");
        }
    }

    @Override
    public boolean canStart() {
        return isAwayFromStation();
    }

    private boolean isAwayFromStation() {
        return armyEntity.squaredDistanceTo(home) > 1;
    }

    @Override
    public boolean shouldContinue() {
        if (this.navigation.isIdle()) {
            return false;
        } else {
            return isAwayFromStation();
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
        this.navigation.stop();
        this.armyEntity.setPathfindingPenalty(PathNodeType.WATER, this.oldWaterPathfindingPenalty);
    }

    @Override
    public void tick() {
        if (--this.updateCountdownTicks <= 0) {
            this.updateCountdownTicks = 10;
            if (!this.armyEntity.isLeashed() && !this.armyEntity.hasVehicle()) {
                this.navigation.startMovingTo(home.getX(), home.getY(), home.getZ(), this.speed);
            }
        }
    }
}
