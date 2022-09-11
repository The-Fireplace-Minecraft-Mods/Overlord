package dev.the_fireplace.overlord.entity.ai.goal.movement;

import dev.the_fireplace.overlord.entity.ArmyEntity;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

public class WanderAroundHomeGoal extends RandomStrollGoal
{
    protected ArmyEntity armyEntity;
    protected Vec3 home;
    protected byte radius;

    public WanderAroundHomeGoal(ArmyEntity mob, double speed, Vec3 home, byte radius) {
        this(mob, speed, home, radius, 120);
    }

    public WanderAroundHomeGoal(ArmyEntity mob, double speed, Vec3 home, byte radius, int chance) {
        super(mob, speed, chance);
        this.armyEntity = mob;
        this.home = home;
        this.radius = radius;
    }

    @Override
    protected Vec3 getPosition() {
        if (armyEntity.position().distanceTo(home) > radius) {
            return LandRandomPos.getPosTowards(this.armyEntity, 10, 7, home);
        }

        return DefaultRandomPos.getPos(this.armyEntity, Math.min(10, radius), 7);
    }
}
