package dev.the_fireplace.overlord.entity.ai.aiconfig.combat;

import dev.the_fireplace.overlord.entity.ai.AIListManager;
import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

public class TargetEntitySelector {
    private boolean targetPlayers = true;
    private UUID targetPlayersBlacklist = AIListManager.EMPTY_LIST_ID;
    private boolean targetAnimals = false;
    private UUID targetAnimalsList = AIListManager.ALL_ANIMALS_LIST_ID;
    private boolean targetMobs = true;
    private UUID targetMobsList = AIListManager.ALL_MOBS_LIST_ID;

    private final Access access = new Access();

    public Access getData() {
        return access;
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();

        tag.putBoolean("targetPlayers", targetPlayers);
        tag.putUuid("targetPlayersBlacklist", targetPlayersBlacklist);
        tag.putBoolean("targetAnimals", targetAnimals);
        tag.putUuid("targetAnimalsList", targetAnimalsList);
        tag.putBoolean("targetMobs", targetMobs);
        tag.putUuid("targetMobsList", targetMobsList);

        return tag;
    }

    public void readTag(CompoundTag tag) {
        if (tag.contains("targetPlayers")) {
            targetPlayers = tag.getBoolean("targetPlayers");
        }
        if (tag.contains("targetPlayersBlacklist")) {
            targetPlayersBlacklist = tag.getUuid("targetPlayersBlacklist");
        }
        if (tag.contains("targetAnimals")) {
            targetAnimals = tag.getBoolean("targetAnimals");
        }
        if (tag.contains("targetAnimalsList")) {
            targetAnimalsList = tag.getUuid("targetAnimalsList");
        }
        if (tag.contains("targetMobs")) {
            targetMobs = tag.getBoolean("targetMobs");
        }
        if (tag.contains("targetMobsList")) {
            targetMobsList = tag.getUuid("targetMobsList");
        }
    }
    
    public final class Access {
        public boolean isTargetPlayers() {
            return targetPlayers;
        }

        public void setTargetPlayers(boolean targetPlayers) {
            TargetEntitySelector.this.targetPlayers = targetPlayers;
        }

        public UUID getTargetPlayersBlacklist() {
            return targetPlayersBlacklist;
        }

        public void setTargetPlayersBlacklist(UUID targetPlayersBlacklist) {
            TargetEntitySelector.this.targetPlayersBlacklist = targetPlayersBlacklist;
        }

        public boolean isTargetAnimals() {
            return targetAnimals;
        }

        public void setTargetAnimals(boolean targetAnimals) {
            TargetEntitySelector.this.targetAnimals = targetAnimals;
        }

        public UUID getTargetAnimalsList() {
            return targetAnimalsList;
        }

        public void setTargetAnimalsList(UUID targetAnimalsList) {
            TargetEntitySelector.this.targetAnimalsList = targetAnimalsList;
        }

        public boolean isTargetMobs() {
            return targetMobs;
        }

        public void setTargetMobs(boolean targetMobs) {
            TargetEntitySelector.this.targetMobs = targetMobs;
        }

        public UUID getTargetMobsList() {
            return targetMobsList;
        }

        public void setTargetMobsList(UUID targetMobsList) {
            TargetEntitySelector.this.targetMobsList = targetMobsList;
        }
    }
}
