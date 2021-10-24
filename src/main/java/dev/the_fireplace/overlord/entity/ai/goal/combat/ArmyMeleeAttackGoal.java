package dev.the_fireplace.overlord.entity.ai.goal.combat;

import dev.the_fireplace.annotateddi.api.DIContainer;
import dev.the_fireplace.overlord.entity.ArmyEntity;
import dev.the_fireplace.overlord.entity.ai.goal.AIEquipmentHelper;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;

public class ArmyMeleeAttackGoal extends MeleeAttackGoal
{
    protected final ArmyEntity armyEntity;
    protected final AIEquipmentHelper equipmentHelper;

    public ArmyMeleeAttackGoal(ArmyEntity armyEntity, double speed, boolean pauseWhenMobIdle) {
        super(armyEntity, speed, pauseWhenMobIdle);
        this.armyEntity = armyEntity;
        this.equipmentHelper = DIContainer.get().getInstance(AIEquipmentHelper.class);
    }

    @Override
    public boolean canStart() {
        return super.canStart() && shouldAttackWithMelee();
    }

    private boolean shouldAttackWithMelee() {
        return !equipmentHelper.isUsingRanged(armyEntity) || !equipmentHelper.hasAmmoEquipped(armyEntity);
    }

    @Override
    public boolean shouldContinue() {
        return super.shouldContinue() && shouldAttackWithMelee();
    }
}
