package dev.the_fireplace.overlord.entity;

import dev.the_fireplace.annotateddi.api.DIContainer;
import dev.the_fireplace.lib.api.uuid.injectables.EmptyUUID;
import dev.the_fireplace.overlord.domain.data.Squads;
import dev.the_fireplace.overlord.domain.entity.OrderableEntity;
import dev.the_fireplace.overlord.domain.entity.Ownable;
import dev.the_fireplace.overlord.domain.entity.logic.EntityAlliances;
import dev.the_fireplace.overlord.entity.ai.GoalSelectorHelper;
import dev.the_fireplace.overlord.entity.ai.aiconfig.AISettings;
import dev.the_fireplace.overlord.entity.ai.aiconfig.combat.CombatCategory;
import dev.the_fireplace.overlord.entity.ai.aiconfig.movement.MovementCategory;
import dev.the_fireplace.overlord.entity.ai.aiconfig.movement.PositionSetting;
import dev.the_fireplace.overlord.entity.ai.aiconfig.tasks.TasksCategory;
import dev.the_fireplace.overlord.entity.ai.goal.combat.*;
import dev.the_fireplace.overlord.entity.ai.goal.equipment.FindAmmoGoal;
import dev.the_fireplace.overlord.entity.ai.goal.equipment.SwitchToMeleeWhenCloseGoal;
import dev.the_fireplace.overlord.entity.ai.goal.equipment.SwitchToRangedWhenFarGoal;
import dev.the_fireplace.overlord.entity.ai.goal.movement.FollowOwnerGoal;
import dev.the_fireplace.overlord.entity.ai.goal.movement.ReturnHomeGoal;
import dev.the_fireplace.overlord.entity.ai.goal.movement.WanderAroundHomeGoal;
import dev.the_fireplace.overlord.entity.ai.goal.target.ArmyAttackWithOwnerGoal;
import dev.the_fireplace.overlord.entity.ai.goal.target.ArmyTrackOwnerAttackerGoal;
import dev.the_fireplace.overlord.entity.ai.goal.task.GatherItemGoal;
import dev.the_fireplace.overlord.entity.ai.goal.task.GatherMilkGoal;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public abstract class ArmyEntity extends TameableEntity implements Ownable, OrderableEntity
{
    protected static final TrackedData<Optional<UUID>> SQUAD = DataTracker.registerData(OwnedSkeletonEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    protected final EntityAlliances entityAlliances;
    protected final EmptyUUID emptyUUID;
    protected final Squads squads;
    protected final AISettings aiSettings;
    protected boolean isSwappingEquipment;
    public double prevCapeX;
    public double prevCapeY;
    public double prevCapeZ;
    public double capeX;
    public double capeY;
    public double capeZ;
    public float prevStrideDistance;
    public float strideDistance;

    protected ArmyEntity(EntityType<? extends ArmyEntity> type, World world) {
        super(type, world);
        this.entityAlliances = DIContainer.get().getInstance(EntityAlliances.class);
        this.emptyUUID = DIContainer.get().getInstance(EmptyUUID.class);
        this.squads = DIContainer.get().getInstance(Squads.class);
        this.aiSettings = createBaseAISettings();
        reloadGoals();
    }

    private AISettings createBaseAISettings() {
        return new AISettings();
    }

    protected void reloadGoals() {
        if (!world.isClient()) {
            GoalSelectorHelper.clear(goalSelector);
            GoalSelectorHelper.clear(targetSelector);
            initGoals();
        }
    }

    @Override
    protected void initGoals() {
        if (aiSettings == null) {
            // Ignore initial initGoals in MobEntity constructor, we'll do that after AI settings are created/loaded
            return;
        }
        int goalWeight = 1;
        MovementCategory movement = this.aiSettings.getMovement();
        CombatCategory combat = this.aiSettings.getCombat();
        TasksCategory tasks = this.aiSettings.getTasks();

        goalWeight = addBasicSurvivalGoals(goalWeight, movement);
        goalWeight = addCombatGoals(goalWeight, combat);
        goalWeight = addTaskGoals(goalWeight, tasks);
        goalWeight = addStandardMovementGoals(goalWeight, movement);
        goalWeight = addIdleGoals(goalWeight);

        addTargetSelectors(combat);
    }

    protected int addBasicSurvivalGoals(int goalWeight, MovementCategory movement) {
        if (movement.isEnabled() && this.isUndead()) {
            this.goalSelector.add(goalWeight++, new AvoidSunlightGoal(this));
            this.goalSelector.add(goalWeight++, new EscapeSunlightGoal(this, 1.2D));
        }
        return goalWeight;
    }

    protected int addCombatGoals(int goalWeight, CombatCategory combat) {
        if (combat.isEnabled()) {
            if (combat.isRanged() && combat.isSwitchToRangedWhenFar()) {
                this.goalSelector.add(goalWeight, new SwitchToRangedWhenFarGoal(this, combat.getRangedSwitchDistance()));
            }
            if (combat.isMelee() && combat.isSwitchToMeleeWhenClose()) {
                this.goalSelector.add(goalWeight, new SwitchToMeleeWhenCloseGoal(this, combat.getMeleeSwitchDistance(), combat.isBlock()));
            }
            goalWeight++;
            if (combat.isRanged()) {
                this.goalSelector.add(goalWeight, new FindAmmoGoal(this, combat.isMelee() && combat.isSwitchToMeleeWhenNoAmmo(), combat.isBlock()));
            }

            goalWeight++;
            boolean pursueTargets = combat.isPursueCombatTargets();
            if (combat.isMelee()) {
                Goal meleeGoal = pursueTargets
                    ? new ArmyMeleeAttackGoal(this, 1.0D, true)
                    : new ArmyInPlaceMeleeAttackGoal(this);
                this.goalSelector.add(goalWeight, meleeGoal);
            }
            if (combat.isRanged()) {
                if (this instanceof CrossbowUser) {
                    int crossbowRange = 8;//TODO Figure out a good way to calc this number. Using 8 for now since that's Pillager range
                    //noinspection unchecked,rawtypes
                    Goal crossbowGoal = pursueTargets
                        ? new ArmyCrossbowAttackGoal(this, 1.0D, crossbowRange)
                        : new ArmyInPlaceCrossbowAttackGoal(this, crossbowRange * 1.5f);
                    this.goalSelector.add(goalWeight, crossbowGoal);
                }
                if (this instanceof RangedAttackMob) {
                    int bowRange = 15;//TODO Figure out a good way to calc this number. Using 15 for now since that's Skeleton range
                    int attackInterval = 20;
                    //noinspection unchecked,rawtypes
                    Goal bowGoal = pursueTargets
                        ? new ArmyBowAttackGoal(this, 1.0D, attackInterval, bowRange)
                        : new ArmyInPlaceBowAttackGoal(this, attackInterval, bowRange * 1.5f);
                    this.goalSelector.add(goalWeight, bowGoal);
                }
            }
        }
        return goalWeight;
    }

    protected int addTaskGoals(int goalWeight, TasksCategory tasks) {
        if (tasks.isEnabled()) {
            if (tasks.isGatheringMilk()) {
                this.goalSelector.add(goalWeight++, new GatherMilkGoal(this, tasks.getCowSearchDistance()));
            }
            if (tasks.isPickUpItems()) {
                this.goalSelector.add(goalWeight++, new GatherItemGoal(this, tasks.getItemSearchDistance()));
            }
        }
        return goalWeight;
    }

    protected int addStandardMovementGoals(int goalWeight, MovementCategory movement) {
        if (movement.isEnabled()) {
            PositionSetting homeSetting = movement.getHome();
            Vec3d home = new Vec3d(homeSetting.getX(), homeSetting.getY(), homeSetting.getZ());
            switch (movement.getMoveMode()) {
                case FOLLOW:
                    byte minimumFollowDistance = movement.getMinimumFollowDistance();
                    byte maximumFollowDistance = movement.getMaximumFollowDistance();
                    this.goalSelector.add(goalWeight++, new FollowOwnerGoal(this, 1.0D, minimumFollowDistance, maximumFollowDistance, true));
                    break;
                case WANDER:
                    if (movement.isExploringWander()) {
                        this.goalSelector.add(goalWeight++, new WanderAroundGoal(this, 1.0D));
                    } else {
                        this.goalSelector.add(goalWeight++, new WanderAroundHomeGoal(this, 1.0D, home, movement.getMoveRadius()));
                    }
                    break;
                case STATIONED:
                    if (movement.isStationedReturnHome()) {
                        this.goalSelector.add(goalWeight++, new ReturnHomeGoal(this, 1.0D, home));
                    }
                    break;
            }
        }
        return goalWeight;
    }

    protected int addIdleGoals(int goalWeight) {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            this.goalSelector.add(goalWeight, new LookAtEntityGoal(this, PlayerEntity.class, 16.0F));
            return ++goalWeight;
        }
        if (aiSettings.getCombat().isEnabled()) {
            this.goalSelector.add(goalWeight++, new LookAtEntityGoal(this, MobEntity.class, 12.0F));
        }
        this.goalSelector.add(goalWeight, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(goalWeight, new LookAroundGoal(this));

        return ++goalWeight;
    }

    protected void addTargetSelectors(CombatCategory combat) {
        int targetGoalWeight = 1;
        if (combat.isEnabled()) {
            this.targetSelector.add(targetGoalWeight++, new ArmyTrackOwnerAttackerGoal(this));
            if (!combat.isOnlyDefendPlayer()) {
                this.targetSelector.add(targetGoalWeight++, new ArmyAttackWithOwnerGoal(this));
                this.targetSelector.add(targetGoalWeight++, new RevengeGoal(this).setGroupRevenge());
                //TODO Looks like we'll eventually need a custom Target goal that chooses targets based on equipped weapon type
                this.targetSelector.add(targetGoalWeight, new FollowTargetGoal<>(this, MobEntity.class, 10, true, false, mob -> mob instanceof Monster));
            }
        }
    }

    @Override
    public AISettings getAISettings() {
        return aiSettings;
    }

    @Override
    public void updateAISettings(NbtCompound newSettings) {
        aiSettings.readTag(newSettings);
        reloadGoals();
    }

    public abstract Inventory getInventory();

    public abstract boolean giveItemStack(ItemStack stack);

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SQUAD, Optional.empty());
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.hasExistingSquad(null)) {
            nbt.putUuid("Squad", this.getSquad());
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.dataTracker.set(
            SQUAD,
            nbt.containsUuid("Squad")
                ? Optional.of(nbt.getUuid("Squad"))
                : Optional.empty()
        );
    }

    public byte getEquipmentSwapTicks() {
        return 0;
    }

    public boolean isSwappingEquipment() {
        return this.isSwappingEquipment;
    }

    public void setSwappingEquipment(boolean swappingEquipment) {
        this.isSwappingEquipment = swappingEquipment;
    }

    @Override
    public boolean canAttackWithOwner(LivingEntity target, LivingEntity owner) {
        return !entityAlliances.isAlliedTo(this, target);
    }

    public BlockPos getHome() {
        PositionSetting homeSetting = aiSettings.getMovement().getHome();
        return new BlockPos(homeSetting.getX(), homeSetting.getY(), homeSetting.getZ());
    }

    public float getAttackCooldownProgressPerTick() {
        return (float) (1.0D / this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_SPEED) * 20.0D);
    }

    public float getAttackCooldownProgress(float baseTime) {
        return MathHelper.clamp(((float) this.lastAttackedTicks + baseTime) / this.getAttackCooldownProgressPerTick(), 0.0F, 1.0F);
    }

    public void resetLastAttackedTicks() {
        this.lastAttackedTicks = 0;
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

    public abstract int getMainHandSlot();

    public abstract int getOffHandSlot();

    @Override
    public final int getEntityIdNumber() {
        return getEntityId();
    }

    public static DefaultAttributeContainer.Builder createArmyAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_ATTACK_DAMAGE).add(EntityAttributes.GENERIC_ATTACK_SPEED);
    }

    @Override
    public boolean cannotDespawn() {
        return true;
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        return false;
    }

    @Nullable
    @Override
    public LivingEntity getOwner() {
        if (this.world == null || !(world instanceof ServerWorld)) {
            return null;
        }

        Entity entity = ((ServerWorld) this.world).getEntity(this.getOwnerUuid());
        return entity instanceof LivingEntity ? (LivingEntity) entity : null;
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    public UUID getSquad() {
        return dataTracker.get(SQUAD).orElse(emptyUUID.get());
    }

    public void setSquad(UUID squadId) {
        dataTracker.set(SQUAD, emptyUUID.is(squadId) ? Optional.empty() : Optional.of(squadId));
    }

    public boolean hasExistingSquad(@Nullable Squads squads) {
        if (squads == null) {
            squads = this.squads;
        }
        return !emptyUUID.is(getSquad()) && squads.getSquad(getOwnerUuid(), getSquad()) != null;
    }

    @Override
    public void tick() {
        super.tick();

        this.updateCapeAngles();
    }

    protected void updateCapeAngles() {
        this.prevCapeX = this.capeX;
        this.prevCapeY = this.capeY;
        this.prevCapeZ = this.capeZ;
        double capeOffsetX = this.getX() - this.capeX;
        double capeOffsetY = this.getY() - this.capeY;
        double capeOffsetZ = this.getZ() - this.capeZ;
        double minOffsetAmount = 10.0D;
        if (capeOffsetX > minOffsetAmount) {
            this.capeX = this.getX();
            this.prevCapeX = this.capeX;
        }

        if (capeOffsetZ > minOffsetAmount) {
            this.capeZ = this.getZ();
            this.prevCapeZ = this.capeZ;
        }

        if (capeOffsetY > minOffsetAmount) {
            this.capeY = this.getY();
            this.prevCapeY = this.capeY;
        }

        if (capeOffsetX < -minOffsetAmount) {
            this.capeX = this.getX();
            this.prevCapeX = this.capeX;
        }

        if (capeOffsetZ < -minOffsetAmount) {
            this.capeZ = this.getZ();
            this.prevCapeZ = this.capeZ;
        }

        if (capeOffsetY < -minOffsetAmount) {
            this.capeY = this.getY();
            this.prevCapeY = this.capeY;
        }

        this.capeX += capeOffsetX * 0.25D;
        this.capeZ += capeOffsetZ * 0.25D;
        this.capeY += capeOffsetY * 0.25D;
    }

    @Override
    public void tickMovement() {
        this.prevStrideDistance = this.strideDistance;
        super.tickMovement();
        float g;
        if (this.onGround && !this.isDead() && !this.isSwimming()) {
            g = Math.min(0.1F, (float) this.getVelocity().horizontalLength());
        } else {
            g = 0.0F;
        }

        this.strideDistance += (g - this.strideDistance) * 0.4F;
    }

    @Nullable
    @Override
    public final UUID getOwnerUniqueId() {
        return getOwnerUuid();
    }

    @Override
    public final void setOwnerUniqueId(@Nullable UUID uuid) {
        setOwnerUuid(uuid);
    }
}
