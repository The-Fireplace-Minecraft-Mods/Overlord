package dev.the_fireplace.overlord.entity;

import dev.the_fireplace.overlord.domain.entity.OrderableEntity;
import dev.the_fireplace.overlord.domain.mechanic.Ownable;
import dev.the_fireplace.overlord.entity.ai.GoalSelectorHelper;
import dev.the_fireplace.overlord.entity.ai.goal.FollowOwnerGoal;
import dev.the_fireplace.overlord.model.aiconfig.AISettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public abstract class ArmyEntity extends MobEntityWithAi implements Ownable, OrderableEntity
{
    protected final AISettings aiSettings;

    protected ArmyEntity(EntityType<? extends ArmyEntity> type, World world) {
        super(type, world);
        aiSettings = new AISettings();
        reloadGoals();
    }

    protected void reloadGoals() {
        GoalSelectorHelper.clear(goalSelector);
        GoalSelectorHelper.clear(targetSelector);
        initGoals();
    }

    @Override
    protected void initGoals() {
        if (aiSettings == null) {
            // Ignore initial initGoals in MobEntity constructor, we'll do that after AI settings are created/loaded
            return;
        }
        int goalWeight = 1;
        if (aiSettings.getMovement().isEnabled()) {
            if (this.isUndead()) {
                this.goalSelector.add(goalWeight++, new AvoidSunlightGoal(this));
                this.goalSelector.add(goalWeight++, new EscapeSunlightGoal(this, 1.0D));
            }
            switch (aiSettings.getMovement().getMoveMode()) {
                case FOLLOW:
                    byte followDistance = this.aiSettings.getMovement().getFollowDistance();
                    this.goalSelector.add(goalWeight++, new FollowOwnerGoal(this, 1.0D, followDistance - 1, followDistance + 1, true));
                    break;
                case PATROL:
                    //TODO patrol goal
                    break;
                case WANDER:
                    this.goalSelector.add(goalWeight++, new WanderAroundFarGoal(this, 1.0D));
                    break;
                case STATIONED:
                    break;
            }
        }
        this.goalSelector.add(goalWeight, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(goalWeight, new LookAtEntityGoal(this, MobEntity.class, 8.0F));
        this.goalSelector.add(goalWeight, new LookAroundGoal(this));

        int targetGoalWeight = 1;
        //TODO configure targets using AI settings
        this.targetSelector.add(targetGoalWeight++, new RevengeGoal(this));
        this.targetSelector.add(targetGoalWeight++, new FollowTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(targetGoalWeight, new FollowTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.add(targetGoalWeight, new FollowTargetGoal<>(this, TurtleEntity.class, 10, true, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
    }

    @Override
    public AISettings getAISettings() {
        return aiSettings;
    }

    @Override
    public void updateAISettings(CompoundTag newSettings) {
        aiSettings.readTag(newSettings);
        reloadGoals();
    }

    @Override
    public boolean canBeLeashedBy(PlayerEntity player) {
        return false;
    }

    @Override
    public boolean isLeashed() {
        return false;
    }

    @Nullable
    @Override
    public Entity getHoldingEntity() {
        return null;
    }

    @Override
    public void attachLeash(Entity entity, boolean bl) {

    }

    @Override
    public void detachLeash(boolean sendPacket, boolean bl) {

    }
}
