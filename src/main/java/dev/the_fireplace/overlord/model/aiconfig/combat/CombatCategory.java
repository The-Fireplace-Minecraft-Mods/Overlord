package dev.the_fireplace.overlord.model.aiconfig.combat;

import dev.the_fireplace.overlord.model.AIListManager;
import dev.the_fireplace.overlord.model.aiconfig.SettingsComponent;
import dev.the_fireplace.overlord.model.aiconfig.TargetEntitySelector;
import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

public class CombatCategory implements SettingsComponent {

    private boolean enabled = true;
    //Combat strategies
    private boolean melee = true;
    private boolean ranged = true;
    private boolean block = true;
    private boolean throwItem = true;
    private boolean onlyDefendPlayer = false;
    //Melee settings
    private boolean switchToRangedWhenFar = true;
    private byte rangedSwitchDistance = 4;
    private final TargetEntitySelector meleeTargets = new TargetEntitySelector();
    //Ranged settings
    private boolean switchToMeleeWhenNoAmmo = true;
    private boolean switchToMeleeWhenClose = true;
    private byte meleeSwitchDistance = 3;
    private final TargetEntitySelector rangedTargets = new TargetEntitySelector();
    //Throw Item settings
    private UUID throwItemList = AIListManager.ALL_THROWABLES_LIST_ID;
    private final TargetEntitySelector throwTargets = new TargetEntitySelector();

    @Override
    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();

        tag.putBoolean("enabled", enabled);

        //Combat strategies
        tag.putBoolean("melee", melee);
        tag.putBoolean("ranged", ranged);
        tag.putBoolean("block", block);
        tag.putBoolean("throwItem", throwItem);
        tag.putBoolean("onlyDefendPlayer", onlyDefendPlayer);
        //Melee settings
        tag.putBoolean("switchToRangedWhenFar", switchToRangedWhenFar);
        tag.putByte("rangedSwitchDistance", rangedSwitchDistance);
        tag.put("meleeTargets", meleeTargets.toTag());
        //Ranged settings
        tag.putBoolean("switchToMeleeWhenNoAmmo", switchToMeleeWhenNoAmmo);
        tag.putBoolean("switchToMeleeWhenClose", switchToMeleeWhenClose);
        tag.putByte("meleeSwitchDistance", meleeSwitchDistance);
        tag.put("rangedTargets", rangedTargets.toTag());
        //Throw Item settings
        tag.putUuid("throwItemList", throwItemList);
        tag.put("throwTargets", throwTargets.toTag());

        return tag;
    }

    @Override
    public void readTag(CompoundTag tag) {
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
        if (tag.contains("throwItem")) {
            throwItem = tag.getBoolean("throwItem");
        }
        if (tag.contains("onlyDefendPlayer")) {
            onlyDefendPlayer = tag.getBoolean("onlyDefendPlayer");
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
        //Thrown Item settings
        if (tag.contains("throwItemList")) {
            throwItemList = tag.getUuid("throwItemList");
        }
        if (tag.contains("throwTargets")) {
            throwTargets.readTag(tag.getCompound("throwTargets"));
        }
    }
}
