package dev.the_fireplace.overlord.model;

import java.util.UUID;

public class TargetEntitySelector {
    private boolean targetPlayers = true;
    private UUID targetPlayersBlacklist = AIListManager.EMPTY_LIST_ID;
    private boolean targetAnimals = false;
    private UUID targetAnimalsList = AIListManager.ALL_ANIMALS_LIST_ID;
    private boolean targetMobs = true;
    private UUID targetMobsList = AIListManager.ALL_MOBS_LIST_ID;
}
