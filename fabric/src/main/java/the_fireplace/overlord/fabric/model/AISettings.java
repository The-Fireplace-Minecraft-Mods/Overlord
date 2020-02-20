package the_fireplace.overlord.fabric.model;

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
    //Bow settings
    private boolean switchToMeleeWhenNoArrows = true;
    private boolean switchToMeleeWhenClose = true;
    private byte meleeSwitchDistance = 3;
    //Throw Item settings
    private UUID throwItemList = AIListManager.ALL_THROWABLES_LIST_ID;
}
