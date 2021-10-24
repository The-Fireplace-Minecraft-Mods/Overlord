package dev.the_fireplace.overlord.entity.ai.goal.movement;

import dev.the_fireplace.overlord.entity.ArmyEntity;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class WanderAroundHomeGoal extends WanderAroundGoal
{
    protected ArmyEntity armyEntity;
    protected Vec3d home;
    protected byte radius;

    public WanderAroundHomeGoal(ArmyEntity mob, double speed, Vec3d home, byte radius) {
        this(mob, speed, home, radius, 120);
    }

    public WanderAroundHomeGoal(ArmyEntity mob, double speed, Vec3d home, byte radius, int chance) {
        super(mob, speed, chance);
        this.armyEntity = mob;
        this.home = home;
        this.radius = radius;
    }

    @Nullable
    @Override
    protected Vec3d getWanderTarget() {
        if (armyEntity.getPos().distanceTo(home) > radius) {
            return TargetFinder.findTargetTowards(this.armyEntity, 10, 7, home);
        }

        return TargetFinder.findTarget(this.armyEntity, 10, 7);
    }
}
