package dev.the_fireplace.overlord.entity.ai.goal.combat;

import dev.the_fireplace.overlord.entity.ArmyEntity;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.RangedAttackMob;

import java.util.EnumSet;

public class ArmyInPlaceCrossbowAttackGoal<T extends ArmyEntity & RangedAttackMob & CrossbowAttackMob> extends AbstractArmyCrossbowAttackGoal<T>
{
    public ArmyInPlaceCrossbowAttackGoal(T armyEntity, float range) {
        super(armyEntity, range);
        this.setFlags(EnumSet.of(Flag.LOOK));
    }
}
