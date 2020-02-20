package the_fireplace.overlord.model;

import java.util.UUID;

public class AISettings {
    //Behaviors - don't depend on any category
    private boolean saveDamagedEquipment = false;
    private UUID saveEquipmentList = AIListManager.ALL_EQUIPMENT_LIST;
    private boolean loadChunks = false;
    //Top level categories
    private boolean combat = true;
    private boolean movement = true;
    private boolean tasks = true;

    //Combat strategies
    private boolean melee = true;
    private boolean ranged = true;
    private boolean block = true;
    private boolean throwItem = true;
    //Melee settings
    private boolean switchToRangedWhenFar = true;
    private byte rangedSwitchDistance = 4;
    private TargetEntitySelector meleeTargets;
    //Ranged settings
    private boolean switchToMeleeWhenNoArrows = true;
    private boolean switchToMeleeWhenClose = true;
    private byte meleeSwitchDistance = 3;
    private TargetEntitySelector rangedTargets;
    //Throw Item settings
    private UUID throwItemList = AIListManager.ALL_THROWABLES_LIST_ID;
    private TargetEntitySelector throwTargets;

    //Movement modes
    private EnumMovementMode moveMode = EnumMovementMode.STATIONED;
    //How close the skeleton follows the player
    private byte followDistance = 8;
    private UUID patrolPosList = AIListManager.EMPTY_LIST_ID;
    //Should the skeleton go from last directly to first or turn around and navigate the path in reverse?
    private boolean patrolLoop = false;
    private Pos home = null;
    //Should the stationed skeleton return to the home position after walking away to attack
    private boolean stationedReturnHome = true;
    //The radius away from home the skeleton can go when attacking while stationed or wandering the area
    private byte moveRadius = 16;
    //Should the skeleton actively explore when wandering?
    private boolean exploringWander = false;

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
}
