package dev.the_fireplace.overlord.entity.ai.goal.combat;

import dev.the_fireplace.overlord.entity.ArmyEntity;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.ai.RangedAttackMob;

import java.util.EnumSet;

public class ArmyInPlaceCrossbowAttackGoal<T extends ArmyEntity & RangedAttackMob & CrossbowUser> extends AbstractArmyCrossbowAttackGoal<T>
{
	public ArmyInPlaceCrossbowAttackGoal(T armyEntity, float range) {
		super(armyEntity, range);
		this.setControls(EnumSet.of(Control.LOOK));
	}
}
