package dev.the_fireplace.overlord.entity;

import dev.the_fireplace.overlord.domain.entity.OrderableEntity;
import dev.the_fireplace.overlord.domain.mechanic.Ownable;
import dev.the_fireplace.overlord.entity.ai.GoalSelectorHelper;
import dev.the_fireplace.overlord.entity.ai.goal.FollowOwnerGoal;
import dev.the_fireplace.overlord.entity.ai.goal.ReturnHomeGoal;
import dev.the_fireplace.overlord.entity.ai.goal.WanderAroundHomeGoal;
import dev.the_fireplace.overlord.model.aiconfig.AISettings;
import dev.the_fireplace.overlord.model.aiconfig.movement.MovementCategory;
import dev.the_fireplace.overlord.model.aiconfig.movement.PositionSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
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
            MovementCategory movement = this.aiSettings.getMovement();
            if (this.isUndead()) {
                this.goalSelector.add(goalWeight++, new AvoidSunlightGoal(this));
                this.goalSelector.add(goalWeight++, new EscapeSunlightGoal(this, 1.0D));
            }
            PositionSetting homeSetting = movement.getHome();
            Vec3d home = new Vec3d(homeSetting.getX(), homeSetting.getY(), homeSetting.getZ());
            switch (movement.getMoveMode()) {
                case FOLLOW:
                    byte minimumFollowDistance = movement.getMinimumFollowDistance();
                    byte maximumFollowDistance = movement.getMaximumFollowDistance();
                    this.goalSelector.add(goalWeight++, new FollowOwnerGoal(this, 1.0D, minimumFollowDistance, maximumFollowDistance, true));
                    break;
                case PATROL:
                    //TODO patrol goal
                    break;
                case WANDER:
                    if (movement.isExploringWander()) {
                        this.goalSelector.add(goalWeight++, new WanderAroundGoal(this, 1.0D));
                    } else {
                        this.goalSelector.add(goalWeight++, new WanderAroundHomeGoal(this, 1.0D, home, movement.getMoveRadius()));
                    }
                    break;
                case STATIONED:
                    //TODO how will they wander away to attack?
                    if (movement.isStationedReturnHome()) {
                        this.goalSelector.add(goalWeight++, new ReturnHomeGoal(this, 1.0D, home));
                    }
                    break;
            }
        }
        this.goalSelector.add(goalWeight, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        if (aiSettings.getCombat().isEnabled()) {
            this.goalSelector.add(goalWeight, new LookAtEntityGoal(this, MobEntity.class, 8.0F));
        }
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

    public BlockPos getHome() {
        PositionSetting homeSetting = aiSettings.getMovement().getHome();
        return new BlockPos(homeSetting.getX(), homeSetting.getY(), homeSetting.getZ());
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
