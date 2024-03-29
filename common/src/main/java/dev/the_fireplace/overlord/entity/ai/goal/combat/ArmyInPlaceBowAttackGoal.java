package dev.the_fireplace.overlord.entity.ai.goal.combat;

import dev.the_fireplace.overlord.entity.ArmyEntity;
import net.minecraft.world.entity.monster.RangedAttackMob;

import java.util.EnumSet;

public class ArmyInPlaceBowAttackGoal<T extends ArmyEntity & RangedAttackMob> extends AbstractArmyBowAttackGoal<T>
{
    public ArmyInPlaceBowAttackGoal(T armyEntity, int attackInterval, float range) {
        super(armyEntity, attackInterval, range);
        this.setFlags(EnumSet.of(Flag.LOOK));
    }
}
