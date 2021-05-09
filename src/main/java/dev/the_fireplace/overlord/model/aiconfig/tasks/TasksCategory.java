package dev.the_fireplace.overlord.model.aiconfig.tasks;

import dev.the_fireplace.overlord.model.AIListManager;
import dev.the_fireplace.overlord.model.aiconfig.SettingsComponent;
import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

public class TasksCategory implements SettingsComponent {

    private boolean enabled = true;

    //Tasks
    private boolean woodcutting = false;
    private boolean mining = false;
    private boolean digging = false;
    private boolean beekeeping = false;
    private boolean farming = false;
    private boolean building = false;
    private boolean breeding = false;
    private boolean depositItems = false;
    private boolean withdrawItems = false;
    //Woodcutting settings
    private UUID woodcuttingBlockList = AIListManager.EMPTY_LIST_ID;
    private boolean woodcuttingWithoutTools = true;
    //Mining settings
    private UUID miningBlockList = AIListManager.EMPTY_LIST_ID;
    private boolean miningWithoutTools = false;
    //Digging settings
    private UUID diggingBlockList = AIListManager.EMPTY_LIST_ID;
    private boolean diggingWithoutTools = true;
    //Beekeeping settings
    //Should the skeleton collect honey from hives that don't have smoke to calm them?
    private boolean harvestAngryHives = false;
    //Breeding settings
    private UUID breedingEntityList = AIListManager.ALL_ANIMALS_LIST_ID;

    private final Access access = new Access();

    public Access getData() {
        return access;
    }

    @Override
    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();

        tag.putBoolean("enabled", enabled);
        tag.putBoolean("woodcutting", woodcutting);
        tag.putBoolean("mining", mining);
        tag.putBoolean("digging", digging);
        tag.putBoolean("beekeeping", beekeeping);
        tag.putBoolean("farming", farming);
        tag.putBoolean("building", building);
        tag.putBoolean("breeding", breeding);
        tag.putBoolean("depositItems", depositItems);
        tag.putBoolean("withdrawItems", withdrawItems);
        tag.putUuid("woodcuttingBlockList", woodcuttingBlockList);
        tag.putBoolean("woodcuttingWithoutTools", woodcuttingWithoutTools);
        tag.putUuid("miningBlockList", miningBlockList);
        tag.putBoolean("miningWithoutTools", miningWithoutTools);
        tag.putUuid("diggingBlockList", diggingBlockList);
        tag.putBoolean("diggingWithoutTools", diggingWithoutTools);
        tag.putBoolean("harvestAngryHives", harvestAngryHives);
        tag.putUuid("breedingEntityList", breedingEntityList);

        return tag;
    }

    @Override
    public void readTag(CompoundTag tag) {
        if (tag.contains("enabled")) {
            enabled = tag.getBoolean("enabled");
        }
        if (tag.contains("woodcutting")) {
            woodcutting = tag.getBoolean("woodcutting");
        }
        if (tag.contains("mining")) {
            mining = tag.getBoolean("mining");
        }
        if (tag.contains("digging")) {
            digging = tag.getBoolean("digging");
        }
        if (tag.contains("beekeeping")) {
            beekeeping = tag.getBoolean("beekeeping");
        }
        if (tag.contains("farming")) {
            farming = tag.getBoolean("farming");
        }
        if (tag.contains("building")) {
            building = tag.getBoolean("building");
        }
        if (tag.contains("breeding")) {
            breeding = tag.getBoolean("breeding");
        }
        if (tag.contains("depositItems")) {
            depositItems = tag.getBoolean("depositItems");
        }
        if (tag.contains("withdrawItems")) {
            withdrawItems = tag.getBoolean("withdrawItems");
        }
        if (tag.contains("woodcuttingBlockList")) {
            woodcuttingBlockList = tag.getUuid("woodcuttingBlockList");
        }
        if (tag.contains("woodcuttingWithoutTools")) {
            woodcuttingWithoutTools = tag.getBoolean("woodcuttingWithoutTools");
        }
        if (tag.contains("miningBlockList")) {
            miningBlockList = tag.getUuid("miningBlockList");
        }
        if (tag.contains("miningWithoutTools")) {
            miningWithoutTools = tag.getBoolean("miningWithoutTools");
        }
        if (tag.contains("diggingBlockList")) {
            diggingBlockList = tag.getUuid("diggingBlockList");
        }
        if (tag.contains("diggingWithoutTools")) {
            diggingWithoutTools = tag.getBoolean("diggingWithoutTools");
        }
        if (tag.contains("harvestAngryHives")) {
            harvestAngryHives = tag.getBoolean("harvestAngryHives");
        }
        if (tag.contains("breedingEntityList")) {
            breedingEntityList = tag.getUuid("breedingEntityList");
        }
    }

    public final class Access {
        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            TasksCategory.this.enabled = enabled;
        }

        public boolean isWoodcutting() {
            return woodcutting;
        }

        public void setWoodcutting(boolean woodcutting) {
            TasksCategory.this.woodcutting = woodcutting;
        }

        public boolean isMining() {
            return mining;
        }

        public void setMining(boolean mining) {
            TasksCategory.this.mining = mining;
        }

        public boolean isDigging() {
            return digging;
        }

        public void setDigging(boolean digging) {
            TasksCategory.this.digging = digging;
        }

        public boolean isBeekeeping() {
            return beekeeping;
        }

        public void setBeekeeping(boolean beekeeping) {
            TasksCategory.this.beekeeping = beekeeping;
        }

        public boolean isFarming() {
            return farming;
        }

        public void setFarming(boolean farming) {
            TasksCategory.this.farming = farming;
        }

        public boolean isBuilding() {
            return building;
        }

        public void setBuilding(boolean building) {
            TasksCategory.this.building = building;
        }

        public boolean isBreeding() {
            return breeding;
        }

        public void setBreeding(boolean breeding) {
            TasksCategory.this.breeding = breeding;
        }

        public boolean isDepositItems() {
            return depositItems;
        }

        public void setDepositItems(boolean depositItems) {
            TasksCategory.this.depositItems = depositItems;
        }

        public boolean isWithdrawItems() {
            return withdrawItems;
        }

        public void setWithdrawItems(boolean withdrawItems) {
            TasksCategory.this.withdrawItems = withdrawItems;
        }

        public UUID getWoodcuttingBlockList() {
            return woodcuttingBlockList;
        }

        public void setWoodcuttingBlockList(UUID woodcuttingBlockList) {
            TasksCategory.this.woodcuttingBlockList = woodcuttingBlockList;
        }

        public boolean isWoodcuttingWithoutTools() {
            return woodcuttingWithoutTools;
        }

        public void setWoodcuttingWithoutTools(boolean woodcuttingWithoutTools) {
            TasksCategory.this.woodcuttingWithoutTools = woodcuttingWithoutTools;
        }

        public UUID getMiningBlockList() {
            return miningBlockList;
        }

        public void setMiningBlockList(UUID miningBlockList) {
            TasksCategory.this.miningBlockList = miningBlockList;
        }

        public boolean isMiningWithoutTools() {
            return miningWithoutTools;
        }

        public void setMiningWithoutTools(boolean miningWithoutTools) {
            TasksCategory.this.miningWithoutTools = miningWithoutTools;
        }

        public UUID getDiggingBlockList() {
            return diggingBlockList;
        }

        public void setDiggingBlockList(UUID diggingBlockList) {
            TasksCategory.this.diggingBlockList = diggingBlockList;
        }

        public boolean isDiggingWithoutTools() {
            return diggingWithoutTools;
        }

        public void setDiggingWithoutTools(boolean diggingWithoutTools) {
            TasksCategory.this.diggingWithoutTools = diggingWithoutTools;
        }

        public boolean isHarvestAngryHives() {
            return harvestAngryHives;
        }

        public void setHarvestAngryHives(boolean harvestAngryHives) {
            TasksCategory.this.harvestAngryHives = harvestAngryHives;
        }

        public UUID getBreedingEntityList() {
            return breedingEntityList;
        }

        public void setBreedingEntityList(UUID breedingEntityList) {
            TasksCategory.this.breedingEntityList = breedingEntityList;
        }
    }
}
