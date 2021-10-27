package dev.the_fireplace.overlord.entity.ai.goal.equipment;

import dev.the_fireplace.annotateddi.api.DIContainer;
import dev.the_fireplace.overlord.entity.ArmyEntity;
import dev.the_fireplace.overlord.entity.ai.goal.AIEquipmentHelper;

public class FindAmmoGoal extends SwapEquipmentGoal
{
    protected final boolean switchToMeleeWhenOut;
    protected final boolean equipShieldWhenSwitchingToMelee;
    protected final AIEquipmentHelper equipmentHelper;

    protected byte postSwapCooldownTicks;

    public FindAmmoGoal(ArmyEntity armyEntity, boolean switchToMeleeWhenOut, boolean equipShieldWhenSwitchingToMelee) {
        super(armyEntity);
        this.switchToMeleeWhenOut = switchToMeleeWhenOut;
        this.equipShieldWhenSwitchingToMelee = equipShieldWhenSwitchingToMelee;
        this.equipmentHelper = DIContainer.get().getInstance(AIEquipmentHelper.class);
    }

    @Override
    public boolean canStart() {
        return super.canStart()
            && missingRangedAmmo()
            && canSwitchAmmoOrWeapon();
    }

    private boolean canSwitchAmmoOrWeapon() {
        return equipmentHelper.hasUsableRangedWeapon(armyEntity) || switchToMeleeWhenOut;
    }

    private boolean missingRangedAmmo() {
        return equipmentHelper.isUsingRanged(armyEntity)
            && !equipmentHelper.hasAmmoEquipped(armyEntity);
    }

    @Override
    public void start() {
        super.start();
        this.postSwapCooldownTicks = 0;
    }

    @Override
    public boolean shouldContinue() {
        return this.postSwapCooldownTicks > 0
            || (missingRangedAmmo() && canSwitchAmmoOrWeapon())
            || (this.equipShieldWhenSwitchingToMelee && equipmentHelper.shouldEquipShield(armyEntity));
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
