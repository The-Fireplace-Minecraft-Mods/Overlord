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
}
