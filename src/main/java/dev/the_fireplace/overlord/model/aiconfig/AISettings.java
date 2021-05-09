package dev.the_fireplace.overlord.model.aiconfig;

import dev.the_fireplace.overlord.model.aiconfig.combat.CombatCategory;
import dev.the_fireplace.overlord.model.aiconfig.misc.MiscCategory;
import dev.the_fireplace.overlord.model.aiconfig.movement.MovementCategory;
import dev.the_fireplace.overlord.model.aiconfig.tasks.TasksCategory;
import net.minecraft.nbt.CompoundTag;

public class AISettings implements SettingsComponent {

    private final MiscCategory misc = new MiscCategory();
    private final CombatCategory combat = new CombatCategory();
    private final MovementCategory movement = new MovementCategory();
    private final TasksCategory tasks = new TasksCategory();

    @Override
    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();

        tag.put("misc", misc.toTag());
        tag.put("combat", combat.toTag());
        tag.put("movement", movement.toTag());
        tag.put("tasks", tasks.toTag());

        return tag;
    }

    @Override
    public void readTag(CompoundTag tag) {
        if (tag.contains("misc")) {
            misc.readTag(tag.getCompound("misc"));
        }
        if (tag.contains("combat")) {
            combat.readTag(tag.getCompound("combat"));
        }
        if (tag.contains("movement")) {
            movement.readTag(tag.getCompound("movement"));
        }
        if (tag.contains("tasks")) {
            tasks.readTag(tag.getCompound("tasks"));
        }
    }

    public MiscCategory getMisc() {
        return misc;
    }

    public CombatCategory getCombat() {
        return combat;
    }

    public MovementCategory getMovement() {
        return movement;
    }

    public TasksCategory getTasks() {
        return tasks;
    }
}
