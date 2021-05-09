package dev.the_fireplace.overlord.model.aiconfig.combat;

import dev.the_fireplace.overlord.model.AIListManager;
import dev.the_fireplace.overlord.model.aiconfig.SettingsComponent;
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

    private final Access access = new Access();
    public Access getData() {
        return access;
    }

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

    public final class Access {
        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            CombatCategory.this.enabled = enabled;
        }

        public boolean isMelee() {
            return melee;
        }

        public void setMelee(boolean melee) {
            CombatCategory.this.melee = melee;
        }

        public boolean isRanged() {
            return ranged;
        }

        public void setRanged(boolean ranged) {
            CombatCategory.this.ranged = ranged;
        }

        public boolean isBlock() {
            return block;
        }

        public void setBlock(boolean block) {
            CombatCategory.this.block = block;
        }

        public boolean isThrowItem() {
            return throwItem;
        }

        public void setThrowItem(boolean throwItem) {
            CombatCategory.this.throwItem = throwItem;
        }

        public boolean isOnlyDefendPlayer() {
            return onlyDefendPlayer;
        }

        public void setOnlyDefendPlayer(boolean onlyDefendPlayer) {
            CombatCategory.this.onlyDefendPlayer = onlyDefendPlayer;
        }

        public boolean isSwitchToRangedWhenFar() {
            return switchToRangedWhenFar;
        }

        public void setSwitchToRangedWhenFar(boolean switchToRangedWhenFar) {
            CombatCategory.this.switchToRangedWhenFar = switchToRangedWhenFar;
        }

        public byte getRangedSwitchDistance() {
            return rangedSwitchDistance;
        }

        public void setRangedSwitchDistance(byte rangedSwitchDistance) {
            CombatCategory.this.rangedSwitchDistance = rangedSwitchDistance;
        }

        public TargetEntitySelector.Access getMeleeTargets() {
            return meleeTargets.getData();
        }

        public boolean isSwitchToMeleeWhenNoAmmo() {
            return switchToMeleeWhenNoAmmo;
        }

        public void setSwitchToMeleeWhenNoAmmo(boolean switchToMeleeWhenNoAmmo) {
            CombatCategory.this.switchToMeleeWhenNoAmmo = switchToMeleeWhenNoAmmo;
        }

        public boolean isSwitchToMeleeWhenClose() {
            return switchToMeleeWhenClose;
        }

        public void setSwitchToMeleeWhenClose(boolean switchToMeleeWhenClose) {
            CombatCategory.this.switchToMeleeWhenClose = switchToMeleeWhenClose;
        }

        public byte getMeleeSwitchDistance() {
            return meleeSwitchDistance;
        }

        public void setMeleeSwitchDistance(byte meleeSwitchDistance) {
            CombatCategory.this.meleeSwitchDistance = meleeSwitchDistance;
        }

        public TargetEntitySelector.Access getRangedTargets() {
            return rangedTargets.getData();
        }

        public UUID getThrowItemList() {
            return throwItemList;
        }

        public void setThrowItemList(UUID throwItemList) {
            CombatCategory.this.throwItemList = throwItemList;
        }

        public TargetEntitySelector.Access getThrowTargets() {
            return throwTargets.getData();
        }
    }
}
