package dev.the_fireplace.overlord.model.aiconfig.combat;

public interface CombatCategory {
    boolean isEnabled();

    void setEnabled(boolean enabled);

    boolean isMelee();

    void setMelee(boolean melee);

    boolean isRanged();

    void setRanged(boolean ranged);

    boolean isBlock();

    void setBlock(boolean block);

    boolean isOnlyDefendPlayer();

    void setOnlyDefendPlayer(boolean onlyDefendPlayer);

    boolean isSwitchToRangedWhenFar();

    void setSwitchToRangedWhenFar(boolean switchToRangedWhenFar);

    byte getRangedSwitchDistance();

    void setRangedSwitchDistance(byte rangedSwitchDistance);

    TargetEntitySelector.Access getMeleeTargets();

    boolean isSwitchToMeleeWhenNoAmmo();

    void setSwitchToMeleeWhenNoAmmo(boolean switchToMeleeWhenNoAmmo);

    boolean isSwitchToMeleeWhenClose();

    void setSwitchToMeleeWhenClose(boolean switchToMeleeWhenClose);

    byte getMeleeSwitchDistance();

    void setMeleeSwitchDistance(byte meleeSwitchDistance);

    TargetEntitySelector.Access getRangedTargets();
}
