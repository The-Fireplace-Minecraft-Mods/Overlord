package dev.the_fireplace.overlord.entity;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import dev.the_fireplace.annotateddi.impl.domain.loader.LoaderHelper;
import dev.the_fireplace.lib.api.uuid.injectables.EmptyUUID;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.advancement.OverlordCriterions;
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
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public abstract class ArmyEntity extends TamableAnimal implements Ownable, OrderableEntity
{
    protected static final EntityDataAccessor<Optional<UUID>> SQUAD = SynchedEntityData.defineId(ArmyEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    protected final EntityAlliances entityAlliances;
    protected final EmptyUUID emptyUUID;
    protected final Injector injector;
    protected final LoaderHelper loaderHelper;
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

    protected boolean hasTriggeredObtainedCriterion = false;

    protected ArmyEntity(EntityType<? extends ArmyEntity> type, Level world) {
        super(type, world);
        this.injector = OverlordConstants.getInjector();
        this.entityAlliances = injector.getInstance(EntityAlliances.class);
        this.emptyUUID = injector.getInstance(EmptyUUID.class);
        this.loaderHelper = injector.getInstance(LoaderHelper.class);
        if (!world.isClientSide()) {
            this.squads = injector.getInstance(Squads.class);
        } else {
            this.squads = injector.getInstance(Key.get(Squads.class, Names.named("client")));
        }
        this.aiSettings = createBaseAISettings();
        reloadGoals();
    }

    private AISettings createBaseAISettings() {
        return new AISettings();
    }

    protected void reloadGoals() {
        if (!level.isClientSide()) {
            GoalSelectorHelper.clear(goalSelector);
            GoalSelectorHelper.clear(targetSelector);
            registerGoals();
        }
    }

    @Override
    protected void registerGoals() {
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
        if (movement.isEnabled() && this.isInvertedHealAndHarm()) {
            this.goalSelector.addGoal(goalWeight++, new RestrictSunGoal(this));
            this.goalSelector.addGoal(goalWeight++, new FleeSunGoal(this, 1.2D));
        }
        return goalWeight;
    }

    protected int addCombatGoals(int goalWeight, CombatCategory combat) {
        if (combat.isEnabled()) {
            if (combat.isRanged() && combat.isSwitchToRangedWhenFar()) {
                this.goalSelector.addGoal(goalWeight, new SwitchToRangedWhenFarGoal(this, combat.getRangedSwitchDistance()));
            }
            if (combat.isMelee() && combat.isSwitchToMeleeWhenClose()) {
                this.goalSelector.addGoal(goalWeight, new SwitchToMeleeWhenCloseGoal(this, combat.getMeleeSwitchDistance(), combat.isBlock()));
            }
            goalWeight++;
            if (combat.isRanged()) {
                this.goalSelector.addGoal(goalWeight, new FindAmmoGoal(this, combat.isMelee() && combat.isSwitchToMeleeWhenNoAmmo(), combat.isBlock()));
            }

            goalWeight++;
            boolean pursueTargets = combat.isPursueCombatTargets();
            if (combat.isMelee()) {
                Goal meleeGoal = pursueTargets
                    ? new ArmyMeleeAttackGoal(this, 1.0D, true)
                    : new ArmyInPlaceMeleeAttackGoal(this);
                this.goalSelector.addGoal(goalWeight, meleeGoal);
            }
            if (combat.isRanged()) {
                if (this instanceof CrossbowAttackMob) {
                    int crossbowRange = 8;//TODO Figure out a good way to calc this number. Using 8 for now since that's Pillager range
                    //noinspection unchecked,rawtypes
                    Goal crossbowGoal = pursueTargets
                        ? new ArmyCrossbowAttackGoal(this, 1.0D, crossbowRange)
                        : new ArmyInPlaceCrossbowAttackGoal(this, crossbowRange * 1.5f);
                    this.goalSelector.addGoal(goalWeight, crossbowGoal);
                }
                if (this instanceof RangedAttackMob) {
                    int bowRange = 15;//TODO Figure out a good way to calc this number. Using 15 for now since that's Skeleton range
                    int attackInterval = 20;
                    //noinspection unchecked,rawtypes
                    Goal bowGoal = pursueTargets
                        ? new ArmyBowAttackGoal(this, 1.0D, attackInterval, bowRange)
                        : new ArmyInPlaceBowAttackGoal(this, attackInterval, bowRange * 1.5f);
                    this.goalSelector.addGoal(goalWeight, bowGoal);
                }
            }
        }
        return goalWeight;
    }

    protected int addTaskGoals(int goalWeight, TasksCategory tasks) {
        if (tasks.isEnabled()) {
            if (tasks.isGatheringMilk()) {
                this.goalSelector.addGoal(goalWeight++, new GatherMilkGoal(this, tasks.getCowSearchDistance()));
            }
            if (tasks.isPickUpItems()) {
                this.goalSelector.addGoal(goalWeight++, new GatherItemGoal(this, tasks.getItemSearchDistance()));
            }
        }
        return goalWeight;
    }

    protected int addStandardMovementGoals(int goalWeight, MovementCategory movement) {
        if (movement.isEnabled()) {
            PositionSetting homeSetting = movement.getHome();
            Vec3 home = new Vec3(homeSetting.getX(), homeSetting.getY(), homeSetting.getZ());
            switch (movement.getMoveMode()) {
                case FOLLOW:
                    byte minimumFollowDistance = movement.getMinimumFollowDistance();
                    byte maximumFollowDistance = movement.getMaximumFollowDistance();
                    this.goalSelector.addGoal(goalWeight++, new FollowOwnerGoal(this, 1.0D, minimumFollowDistance, maximumFollowDistance, true));
                    break;
                case WANDER:
                    if (movement.isExploringWander()) {
                        this.goalSelector.addGoal(goalWeight++, new RandomStrollGoal(this, 1.0D));
                    } else {
                        this.goalSelector.addGoal(goalWeight++, new WanderAroundHomeGoal(this, 1.0D, home, movement.getMoveRadius()));
                    }
                    break;
                case STATIONED:
                    if (movement.isStationedReturnHome()) {
                        this.goalSelector.addGoal(goalWeight++, new ReturnHomeGoal(this, 1.0D, home));
                    }
                    break;
            }
        }
        return goalWeight;
    }

    protected int addIdleGoals(int goalWeight) {
        if (this.loaderHelper.isDevelopmentEnvironment()) {
            this.goalSelector.addGoal(goalWeight, new LookAtPlayerGoal(this, Player.class, 16.0F));
            return ++goalWeight;
        }
        if (aiSettings.getCombat().isEnabled()) {
            this.goalSelector.addGoal(goalWeight++, new LookAtPlayerGoal(this, Mob.class, 12.0F));
        }
        this.goalSelector.addGoal(goalWeight, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(goalWeight, new RandomLookAroundGoal(this));

        return ++goalWeight;
    }

    protected void addTargetSelectors(CombatCategory combat) {
        int targetGoalWeight = 1;
        if (combat.isEnabled()) {
            this.targetSelector.addGoal(targetGoalWeight++, new ArmyTrackOwnerAttackerGoal(this));
            if (!combat.isOnlyDefendPlayer()) {
                this.targetSelector.addGoal(targetGoalWeight++, new ArmyAttackWithOwnerGoal(this));
                this.targetSelector.addGoal(targetGoalWeight++, new HurtByTargetGoal(this).setAlertOthers());
                //TODO Looks like we'll eventually need a custom Target goal that chooses targets based on equipped weapon type
                this.targetSelector.addGoal(targetGoalWeight, new NearestAttackableTargetGoal<>(this, Mob.class, 10, true, false, mob -> mob instanceof Enemy));
            }
        }
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

    public abstract Container getInventory();

    public abstract boolean giveItemStack(ItemStack stack);

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SQUAD, Optional.empty());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        if (this.hasExistingSquad(squads)) {
            nbt.putUUID("Squad", this.getSquad());
        }
        nbt.putBoolean("HasTriggeredObtainedCriterion", this.hasTriggeredObtainedCriterion);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.entityData.set(
            SQUAD,
            nbt.hasUUID("Squad")
                ? Optional.of(nbt.getUUID("Squad"))
                : Optional.empty()
        );
        if (nbt.contains("HasTriggeredObtainedCriterion")) {
            this.hasTriggeredObtainedCriterion = nbt.getBoolean("HasTriggeredObtainedCriterion");
        }
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
    public boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
        return !entityAlliances.isAlliedTo(this, target);
    }

    public BlockPos getHome() {
        PositionSetting homeSetting = aiSettings.getMovement().getHome();
        return new BlockPos(homeSetting.getX(), homeSetting.getY(), homeSetting.getZ());
    }

    public float getAttackCooldownProgressPerTick() {
        return (float) (1.0D / this.getAttributeValue(Attributes.ATTACK_SPEED) * 20.0D);
    }

    public float getAttackCooldownProgress(float baseTime) {
        return Mth.clamp(((float) this.attackStrengthTicker + baseTime) / this.getAttackCooldownProgressPerTick(), 0.0F, 1.0F);
    }

    public void resetLastAttackedTicks() {
        this.attackStrengthTicker = 0;
    }

    @Override
    public boolean canBeLeashed(Player player) {
        return false;
    }

    @Override
    public boolean isLeashed() {
        return false;
    }

    @Nullable
    @Override
    public Entity getLeashHolder() {
        return null;
    }

    @Override
    public void setLeashedTo(Entity entity, boolean bl) {

    }

    @Override
    public void dropLeash(boolean sendPacket, boolean bl) {

    }

    public abstract int getMainHandSlot();

    public abstract int getOffHandSlot();

    @Override
    public final int getEntityIdNumber() {
        return getId();
    }

    public static AttributeSupplier.Builder createArmyAttributes() {
        return Mob.createMobAttributes().add(Attributes.ATTACK_DAMAGE).add(Attributes.ATTACK_SPEED);
    }

    @Override
    public boolean requiresCustomPersistence() {
        return true;
    }

    @Override
    public boolean removeWhenFarAway(double distanceSquared) {
        return false;
    }

    @Nullable
    @Override
    public LivingEntity getOwner() {
        if (this.level == null || !(level instanceof ServerLevel)) {
            return null;
        }

        Entity entity = ((ServerLevel) this.level).getEntity(this.getOwnerUUID());
        return entity instanceof LivingEntity ? (LivingEntity) entity : null;
    }

    @Override
    public AgableMob getBreedOffspring(ServerLevel serverLevel, AgableMob agableMob) {
        return null;
    }

    public UUID getSquad() {
        return entityData.get(SQUAD).orElse(emptyUUID.get());
    }

    public void setSquad(UUID squadId) {
        entityData.set(SQUAD, emptyUUID.is(squadId) ? Optional.empty() : Optional.of(squadId));
    }

    public boolean hasExistingSquad(Squads squads) {
        return !emptyUUID.is(getSquad()) && squads.getSquad(getOwnerUUID(), getSquad()) != null;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.hasTriggeredObtainedCriterion && getOwner() instanceof ServerPlayer) {
            OverlordCriterions.OBTAINED_ARMY_MEMBER.trigger((ServerPlayer) getOwner(), this.getType());
            this.hasTriggeredObtainedCriterion = true;
        }

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
    public void aiStep() {
        this.prevStrideDistance = this.strideDistance;
        super.aiStep();
        float g;
        if (this.onGround && !this.isDeadOrDying() && !this.isSwimming()) {
            g = Math.min(0.1F, Mth.sqrt(getHorizontalDistanceSqr(this.getDeltaMovement())));
        } else {
            g = 0.0F;
        }

        this.strideDistance += (g - this.strideDistance) * 0.4F;
    }

    @Nullable
    public final UUID getOwnerUUID() {
        return super.getOwnerUUID();
    }

    public final void setOwnerUUID(@Nullable UUID uuid) {
        super.setOwnerUUID(uuid);
    }
}
