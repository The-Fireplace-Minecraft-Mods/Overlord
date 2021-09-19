package dev.the_fireplace.overlord.model.aiconfig.tasks;

import java.util.UUID;

public interface TasksCategory {
    boolean isEnabled();

    void setEnabled(boolean enabled);

    boolean isWoodcutting();

    void setWoodcutting(boolean woodcutting);

    boolean isMining();

    void setMining(boolean mining);

    boolean isDigging();

    void setDigging(boolean digging);

    boolean isBeekeeping();

    void setBeekeeping(boolean beekeeping);

    boolean isFarming();

    void setFarming(boolean farming);

    boolean isBuilding();

    void setBuilding(boolean building);

    boolean isBreeding();

    void setBreeding(boolean breeding);

    boolean isDepositItems();

    void setDepositItems(boolean depositItems);

    boolean isWithdrawItems();

    void setWithdrawItems(boolean withdrawItems);

    UUID getWoodcuttingBlockList();

    void setWoodcuttingBlockList(UUID woodcuttingBlockList);

    boolean isWoodcuttingWithoutTools();

    void setWoodcuttingWithoutTools(boolean woodcuttingWithoutTools);

    UUID getMiningBlockList();

    void setMiningBlockList(UUID miningBlockList);

    boolean isMiningWithoutTools();

    void setMiningWithoutTools(boolean miningWithoutTools);

    UUID getDiggingBlockList();

    void setDiggingBlockList(UUID diggingBlockList);

    boolean isDiggingWithoutTools();

    void setDiggingWithoutTools(boolean diggingWithoutTools);

    boolean isHarvestAngryHives();

    void setHarvestAngryHives(boolean harvestAngryHives);

    UUID getBreedingEntityList();

    void setBreedingEntityList(UUID breedingEntityList);
}
