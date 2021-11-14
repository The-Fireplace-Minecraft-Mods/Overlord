package dev.the_fireplace.overlord.entity.ai.aiconfig.tasks;

import dev.the_fireplace.overlord.entity.ai.AIListManager;
import dev.the_fireplace.overlord.entity.ai.aiconfig.SettingsComponent;
import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

public final class TasksCategoryImpl implements SettingsComponent, TasksCategory {

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

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isWoodcutting() {
        return woodcutting;
    }

    @Override
    public void setWoodcutting(boolean woodcutting) {
        this.woodcutting = woodcutting;
    }

    @Override
    public boolean isMining() {
        return mining;
    }

    @Override
    public void setMining(boolean mining) {
        this.mining = mining;
    }

    @Override
    public boolean isDigging() {
        return digging;
    }

    @Override
    public void setDigging(boolean digging) {
        this.digging = digging;
    }

    @Override
    public boolean isBeekeeping() {
        return beekeeping;
    }

    @Override
    public void setBeekeeping(boolean beekeeping) {
        this.beekeeping = beekeeping;
    }

    @Override
    public boolean isFarming() {
        return farming;
    }

    @Override
    public void setFarming(boolean farming) {
        this.farming = farming;
    }

    @Override
    public boolean isBuilding() {
        return building;
    }

    @Override
    public void setBuilding(boolean building) {
        this.building = building;
    }

    @Override
    public boolean isBreeding() {
        return breeding;
    }

    @Override
    public void setBreeding(boolean breeding) {
        this.breeding = breeding;
    }

    @Override
    public boolean isDepositItems() {
        return depositItems;
    }

    @Override
    public void setDepositItems(boolean depositItems) {
        this.depositItems = depositItems;
    }

    @Override
    public boolean isWithdrawItems() {
        return withdrawItems;
    }

    @Override
    public void setWithdrawItems(boolean withdrawItems) {
        this.withdrawItems = withdrawItems;
    }

    @Override
    public UUID getWoodcuttingBlockList() {
        return woodcuttingBlockList;
    }

    @Override
    public void setWoodcuttingBlockList(UUID woodcuttingBlockList) {
        this.woodcuttingBlockList = woodcuttingBlockList;
    }

    @Override
    public boolean isWoodcuttingWithoutTools() {
        return woodcuttingWithoutTools;
    }

    @Override
    public void setWoodcuttingWithoutTools(boolean woodcuttingWithoutTools) {
        this.woodcuttingWithoutTools = woodcuttingWithoutTools;
    }

    @Override
    public UUID getMiningBlockList() {
        return miningBlockList;
    }

    @Override
    public void setMiningBlockList(UUID miningBlockList) {
        this.miningBlockList = miningBlockList;
    }

    @Override
    public boolean isMiningWithoutTools() {
        return miningWithoutTools;
    }

    @Override
    public void setMiningWithoutTools(boolean miningWithoutTools) {
        this.miningWithoutTools = miningWithoutTools;
    }

    @Override
    public UUID getDiggingBlockList() {
        return diggingBlockList;
    }

    @Override
    public void setDiggingBlockList(UUID diggingBlockList) {
        this.diggingBlockList = diggingBlockList;
    }

    @Override
    public boolean isDiggingWithoutTools() {
        return diggingWithoutTools;
    }

    @Override
    public void setDiggingWithoutTools(boolean diggingWithoutTools) {
        this.diggingWithoutTools = diggingWithoutTools;
    }

    @Override
    public boolean isHarvestAngryHives() {
        return harvestAngryHives;
    }

    @Override
    public void setHarvestAngryHives(boolean harvestAngryHives) {
        this.harvestAngryHives = harvestAngryHives;
    }

    @Override
    public UUID getBreedingEntityList() {
        return breedingEntityList;
    }

    @Override
    public void setBreedingEntityList(UUID breedingEntityList) {
        this.breedingEntityList = breedingEntityList;
    }
}
