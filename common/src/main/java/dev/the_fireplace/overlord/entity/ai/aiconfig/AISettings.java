package dev.the_fireplace.overlord.entity.ai.aiconfig;

import dev.the_fireplace.overlord.entity.ai.aiconfig.combat.CombatCategory;
import dev.the_fireplace.overlord.entity.ai.aiconfig.combat.CombatCategoryImpl;
import dev.the_fireplace.overlord.entity.ai.aiconfig.misc.MiscCategory;
import dev.the_fireplace.overlord.entity.ai.aiconfig.misc.MiscCategoryImpl;
import dev.the_fireplace.overlord.entity.ai.aiconfig.movement.MovementCategory;
import dev.the_fireplace.overlord.entity.ai.aiconfig.movement.MovementCategoryImpl;
import dev.the_fireplace.overlord.entity.ai.aiconfig.tasks.TasksCategory;
import dev.the_fireplace.overlord.entity.ai.aiconfig.tasks.TasksCategoryImpl;
import net.minecraft.nbt.CompoundTag;

public class AISettings implements SettingsComponent {

    private final MiscCategoryImpl misc = new MiscCategoryImpl();
    private final CombatCategoryImpl combat = new CombatCategoryImpl();
    private final MovementCategoryImpl movement = new MovementCategoryImpl();
    private final TasksCategoryImpl tasks = new TasksCategoryImpl();

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
