package dev.the_fireplace.overlord.entity.ai.goal.equipment;

import dev.the_fireplace.annotateddi.api.DIContainer;
import dev.the_fireplace.overlord.entity.ArmyEntity;
import dev.the_fireplace.overlord.entity.ai.goal.AIEquipmentHelper;
import net.minecraft.entity.ai.goal.Goal;

public class FindAmmoGoal extends Goal
{
    protected final ArmyEntity armyEntity;
    protected final boolean switchToMeleeWhenOut;
    protected final boolean equipShieldWhenSwitchingToMelee;
    protected final AIEquipmentHelper equipmentHelper;

    protected byte postSwapCooldownTicks;

    public FindAmmoGoal(ArmyEntity armyEntity, boolean switchToMeleeWhenOut, boolean equipShieldWhenSwitchingToMelee) {
        this.armyEntity = armyEntity;
        this.switchToMeleeWhenOut = switchToMeleeWhenOut;
        this.equipShieldWhenSwitchingToMelee = equipShieldWhenSwitchingToMelee;
        this.equipmentHelper = DIContainer.get().getInstance(AIEquipmentHelper.class);
    }

    @Override
    public boolean canStart() {
        return equipmentHelper.isUsingRanged(armyEntity)
            && !equipmentHelper.hasAmmoEquipped(armyEntity)
            && (equipmentHelper.hasUsableRangedWeapon(armyEntity) || switchToMeleeWhenOut);
    }

    @Override
    public void start() {
        super.start();
        this.postSwapCooldownTicks = 0;
    }

    @Override
    public boolean shouldContinue() {
        return this.postSwapCooldownTicks > 0 || canStart() || (this.equipShieldWhenSwitchingToMelee && equipmentHelper.shouldEquipShield(armyEntity));
    }

    @Override
    public void tick() {
        if (this.postSwapCooldownTicks > 0) {
            this.postSwapCooldownTicks--;
            return;
        }

        if (equipmentHelper.isUsingRanged(armyEntity) && !equipmentHelper.hasAmmoForWeapon(this.armyEntity.getInventory(), this.armyEntity.getMainHandStack())) {
            if (equipmentHelper.hasUsableRangedWeapon(armyEntity)) {
                equipmentHelper.equipUsableRangedWeapon(armyEntity);
                this.postSwapCooldownTicks = this.armyEntity.getEquipmentSwapTicks();
                return;
            } else if (switchToMeleeWhenOut) {
                equipmentHelper.equipMeleeWeapon(armyEntity);
                this.postSwapCooldownTicks = this.armyEntity.getEquipmentSwapTicks();
                return;
            }
        }
        if (equipmentHelper.isUsingRanged(armyEntity) && !equipmentHelper.hasAmmoEquipped(armyEntity)) {
            equipmentHelper.equipUsableAmmo(armyEntity);
            this.postSwapCooldownTicks = this.armyEntity.getEquipmentSwapTicks();
            return;
        }
        if (this.equipShieldWhenSwitchingToMelee && equipmentHelper.shouldEquipShield(armyEntity)) {
            equipmentHelper.equipUsableShield(armyEntity);
            this.postSwapCooldownTicks = this.armyEntity.getEquipmentSwapTicks();
        }
    }
}
