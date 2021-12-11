package dev.the_fireplace.overlord.entity.ai.aiconfig.combat;

import dev.the_fireplace.overlord.entity.ai.aiconfig.SettingsComponent;
import net.minecraft.nbt.NbtCompound;

public final class CombatCategoryImpl implements SettingsComponent, CombatCategory
{
    private boolean enabled = true;
    //Combat strategies
    private boolean melee = true;
    private boolean ranged = true;
    private boolean block = true;
    private boolean onlyDefendPlayer = false;
    private boolean pursueCombatTargets = true;
    //Melee settings
    private boolean switchToRangedWhenFar = true;
    private byte rangedSwitchDistance = 4;
    private final TargetEntitySelector meleeTargets = new TargetEntitySelector();
    //Ranged settings
    private boolean switchToMeleeWhenNoAmmo = true;
    private boolean switchToMeleeWhenClose = true;
    private byte meleeSwitchDistance = 3;
    private final TargetEntitySelector rangedTargets = new TargetEntitySelector();

    @Override
    public NbtCompound toTag() {
        NbtCompound tag = new NbtCompound();

        tag.putBoolean("enabled", enabled);

        //Combat strategies
        tag.putBoolean("melee", melee);
        tag.putBoolean("ranged", ranged);
        tag.putBoolean("block", block);
        tag.putBoolean("onlyDefendPlayer", onlyDefendPlayer);
        tag.putBoolean("pursueCombatTargets", pursueCombatTargets);
        //Melee settings
        tag.putBoolean("switchToRangedWhenFar", switchToRangedWhenFar);
        tag.putByte("rangedSwitchDistance", rangedSwitchDistance);
        tag.put("meleeTargets", meleeTargets.toTag());
        //Ranged settings
        tag.putBoolean("switchToMeleeWhenNoAmmo", switchToMeleeWhenNoAmmo);
        tag.putBoolean("switchToMeleeWhenClose", switchToMeleeWhenClose);
        tag.putByte("meleeSwitchDistance", meleeSwitchDistance);
        tag.put("rangedTargets", rangedTargets.toTag());

        return tag;
    }

    @Override
    public void readTag(NbtCompound tag) {
        if (tag.contains("enabled")) {
            enabled = tag.getBoolean("enabled");
        }
        //Combat strategies
        if (tag.contains("melee")) {
            melee = tag.getBoolean("melee");
        }
        if (tag.contains("ranged")) {
            ranged = tag.getBoolean("ranged");
        }
        if (tag.contains("block")) {
            block = tag.getBoolean("block");
        }
        if (tag.contains("onlyDefendPlayer")) {
            onlyDefendPlayer = tag.getBoolean("onlyDefendPlayer");
        }
        if (tag.contains("pursueCombatTargets")) {
            pursueCombatTargets = tag.getBoolean("pursueCombatTargets");
        }
        //Melee settings
        if (tag.contains("switchToRangedWhenFar")) {
            switchToRangedWhenFar = tag.getBoolean("switchToRangedWhenFar");
        }
        if (tag.contains("rangedSwitchDistance")) {
            rangedSwitchDistance = tag.getByte("rangedSwitchDistance");
        }
        if (tag.contains("meleeTargets")) {
            meleeTargets.readTag(tag.getCompound("meleeTargets"));
        }
        //Ranged settings
        if (tag.contains("switchToMeleeWhenNoAmmo")) {
            switchToMeleeWhenNoAmmo = tag.getBoolean("switchToMeleeWhenNoAmmo");
        }
        if (tag.contains("switchToMeleeWhenClose")) {
            switchToMeleeWhenClose = tag.getBoolean("switchToMeleeWhenClose");
        }
        if (tag.contains("meleeSwitchDistance")) {
            meleeSwitchDistance = tag.getByte("meleeSwitchDistance");
        }
        if (tag.contains("rangedTargets")) {
            rangedTargets.readTag(tag.getCompound("rangedTargets"));
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isMelee() {
        return melee;
    }

    @Override
    public void setMelee(boolean melee) {
        this.melee = melee;
    }

    @Override
    public boolean isRanged() {
        return ranged;
    }

    @Override
    public void setRanged(boolean ranged) {
        this.ranged = ranged;
    }

    @Override
    public boolean isBlock() {
        return block;
    }

    @Override
    public void setBlock(boolean block) {
        this.block = block;
    }

    @Override
    public boolean isOnlyDefendPlayer() {
        return onlyDefendPlayer;
    }

    @Override
    public void setOnlyDefendPlayer(boolean onlyDefendPlayer) {
        this.onlyDefendPlayer = onlyDefendPlayer;
    }

    @Override
    public boolean isSwitchToRangedWhenFar() {
        return switchToRangedWhenFar;
    }

    @Override
    public void setSwitchToRangedWhenFar(boolean switchToRangedWhenFar) {
        this.switchToRangedWhenFar = switchToRangedWhenFar;
    }

    @Override
    public byte getRangedSwitchDistance() {
        return rangedSwitchDistance;
    }

    @Override
    public void setRangedSwitchDistance(byte rangedSwitchDistance) {
        this.rangedSwitchDistance = rangedSwitchDistance;
    }

    @Override
    public TargetEntitySelector.Access getMeleeTargets() {
        return meleeTargets.getData();
    }

    @Override
    public boolean isSwitchToMeleeWhenNoAmmo() {
        return switchToMeleeWhenNoAmmo;
    }

    @Override
    public void setSwitchToMeleeWhenNoAmmo(boolean switchToMeleeWhenNoAmmo) {
        this.switchToMeleeWhenNoAmmo = switchToMeleeWhenNoAmmo;
    }

    @Override
    public boolean isSwitchToMeleeWhenClose() {
        return switchToMeleeWhenClose;
    }

    @Override
    public void setSwitchToMeleeWhenClose(boolean switchToMeleeWhenClose) {
        this.switchToMeleeWhenClose = switchToMeleeWhenClose;
    }

    @Override
    public byte getMeleeSwitchDistance() {
        return meleeSwitchDistance;
    }

    @Override
    public void setMeleeSwitchDistance(byte meleeSwitchDistance) {
        this.meleeSwitchDistance = meleeSwitchDistance;
    }

    @Override
    public TargetEntitySelector.Access getRangedTargets() {
        return rangedTargets.getData();
    }

    @Override
    public boolean isPursueCombatTargets() {
        return pursueCombatTargets;
    }

    @Override
    public void setPursueCombatTargets(boolean pursueCombatTargets) {
        this.pursueCombatTargets = pursueCombatTargets;
    }
}
