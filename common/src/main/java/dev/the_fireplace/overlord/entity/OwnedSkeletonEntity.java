package dev.the_fireplace.overlord.entity;

import com.google.common.collect.Lists;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import dev.the_fireplace.lib.api.uuid.injectables.EmptyUUID;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.advancement.OverlordCriterions;
import dev.the_fireplace.overlord.augment.Augments;
import dev.the_fireplace.overlord.domain.config.ConfigValues;
import dev.the_fireplace.overlord.domain.entity.AnimatedMilkDrinker;
import dev.the_fireplace.overlord.domain.entity.AugmentBearer;
import dev.the_fireplace.overlord.domain.inventory.InventorySearcher;
import dev.the_fireplace.overlord.domain.registry.HeadBlockAugmentRegistry;
import dev.the_fireplace.overlord.domain.world.DaylightDetector;
import dev.the_fireplace.overlord.domain.world.MeleeAttackExecutor;
import dev.the_fireplace.overlord.domain.world.UndeadDaylightDamager;
import dev.the_fireplace.overlord.entity.ai.aiconfig.movement.MovementCategory;
import dev.the_fireplace.overlord.entity.ai.aiconfig.tasks.TasksCategory;
import dev.the_fireplace.overlord.entity.ai.goal.AIEquipmentHelper;
import dev.the_fireplace.overlord.entity.ai.goal.equipment.skeleton.DrinkMilkForHealthGoal;
import dev.the_fireplace.overlord.entity.ai.goal.equipment.skeleton.DrinkMilkGoal;
import dev.the_fireplace.overlord.loader.MenuLoaderHelper;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class OwnedSkeletonEntity extends ArmyEntity implements RangedAttackMob, CrossbowAttackMob, AnimatedMilkDrinker, AugmentBearer
{
    private static final EntityDataAccessor<Boolean> CHARGING = SynchedEntityData.defineId(OwnedSkeletonEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DRINKING_MILK = SynchedEntityData.defineId(OwnedSkeletonEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HAS_TARGET = SynchedEntityData.defineId(OwnedSkeletonEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> GROWTH_PHASE = SynchedEntityData.defineId(OwnedSkeletonEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> HAS_SKIN = SynchedEntityData.defineId(OwnedSkeletonEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HAS_MUSCLES = SynchedEntityData.defineId(OwnedSkeletonEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<UUID>> SKINSUIT = SynchedEntityData.defineId(OwnedSkeletonEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<ItemStack> AUGMENT_BLOCK = SynchedEntityData.defineId(OwnedSkeletonEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final UUID MUSCLE_ATTACK_BONUS_ID = Mth.createInsecureUUID(new SingleThreadedRandomSource("Muscle Attack Bonus".hashCode()));
    private static final UUID MUSCLE_TOUGHNESS_BONUS_ID = Mth.createInsecureUUID(new SingleThreadedRandomSource("Muscle Toughness Bonus".hashCode()));
    private static final UUID MUSCLE_SPEED_BONUS_ID = Mth.createInsecureUUID(new SingleThreadedRandomSource("Muscle Speed Bonus".hashCode()));

    private static final UUID MAX_HEALTH_MODIFIER_ID = Mth.createInsecureUUID(new SingleThreadedRandomSource("Max Health Modifier".hashCode()));
    private static final AttributeModifier MUSCLE_ATTACK_BONUS = new AttributeModifier(MUSCLE_ATTACK_BONUS_ID, "Muscle Attack Bonus", 2.0D, AttributeModifier.Operation.ADDITION);
    private static final AttributeModifier MUSCLE_TOUGHNESS_BONUS = new AttributeModifier(MUSCLE_TOUGHNESS_BONUS_ID, "Muscle Toughness Bonus", 0.25D, AttributeModifier.Operation.ADDITION);
    private static final AttributeModifier MUSCLE_SPEED_BONUS = new AttributeModifier(MUSCLE_SPEED_BONUS_ID, "Muscle Speed Bonus", 0.05D, AttributeModifier.Operation.ADDITION);

    private static final EntityDataAccessor<Float> MAX_HEALTH = SynchedEntityData.defineId(OwnedSkeletonEntity.class, EntityDataSerializers.FLOAT);

    private int milkBucketsDrank = 0;

    private final SkeletonInventory inventory = new SkeletonInventory(this);
    private final ItemCooldowns itemCooldownManager = new ItemCooldowns();

    private final DaylightDetector daylightDetector;
    private final UndeadDaylightDamager undeadDaylightDamager;
    private final MeleeAttackExecutor meleeAttackExecutor;
    private final InventorySearcher inventorySearcher;
    private final AIEquipmentHelper equipmentHelper;
    private final HeadBlockAugmentRegistry headBlockAugmentRegistry;
    private final ConfigValues configValues;

    @Nullable
    private SkeletonGrowthPhase lastGrantedAdvancementGrowthPhase = null;

    /**
     * @deprecated Only public because Minecraft requires it to be.
     * Use {@link OwnedSkeletonEntity#create(Level, UUID)} when possible.
     */
    @Deprecated
    public OwnedSkeletonEntity(EntityType<? extends OwnedSkeletonEntity> type, Level world) {
        super(type, world);
        this.setLeftHanded(this.random.nextFloat() < 0.05F);
        daylightDetector = injector.getInstance(DaylightDetector.class);
        undeadDaylightDamager = injector.getInstance(UndeadDaylightDamager.class);
        meleeAttackExecutor = injector.getInstance(MeleeAttackExecutor.class);
        inventorySearcher = injector.getInstance(InventorySearcher.class);
        equipmentHelper = injector.getInstance(AIEquipmentHelper.class);
        headBlockAugmentRegistry = injector.getInstance(HeadBlockAugmentRegistry.class);
        configValues = injector.getInstance(ConfigValues.class);
        refreshDimensions();
    }

    public static OwnedSkeletonEntity create(Level world, @Nullable UUID owner) {
        OverlordEntities overlordEntities = OverlordConstants.getInjector().getInstance(OverlordEntities.class);
        OwnedSkeletonEntity e = new OwnedSkeletonEntity(overlordEntities.getOwnedSkeletonType(), world);
        e.setOwnerUUID(Objects.requireNonNullElseGet(owner, () -> new UUID(801295133947085751L, -7395604847578632613L)));
        return e;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CHARGING, false);
        this.entityData.define(HAS_TARGET, false);
        this.entityData.define(DRINKING_MILK, false);
        this.entityData.define(GROWTH_PHASE, SkeletonGrowthPhase.BABY.ordinal());
        this.entityData.define(HAS_SKIN, false);
        this.entityData.define(HAS_MUSCLES, false);
        this.entityData.define(SKINSUIT, Optional.empty());
        this.entityData.define(AUGMENT_BLOCK, ItemStack.EMPTY);
        this.entityData.define(MAX_HEALTH, 20f);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
        // Scale the hitbox on the client
        if (level.isClientSide() && GROWTH_PHASE.equals(data)) {
            refreshDimensions();
        }
    }

    public ItemStack getAugmentBlockStack() {
        return entityData.get(AUGMENT_BLOCK);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        tickRegeneration();
        this.getInventory().tickItems();
        tickDaylight();
        tickMilkDrinkSound();
        updateGrowthPhaseAdvancement();
    }

    private void updateGrowthPhaseAdvancement() {
        boolean hasGrownSinceLastAdvancementGranted = this.lastGrantedAdvancementGrowthPhase == null
            || !this.lastGrantedAdvancementGrowthPhase.isAtLeast(this.getGrowthPhase());
        if (hasGrownSinceLastAdvancementGranted && this.getOwner() instanceof ServerPlayer player) {
            OverlordCriterions.SKELETON_ACHIEVED_GROWTH_PHASE.trigger(
                player,
                this.getGrowthPhase(),
                this.hasSkin(),
                this.hasMuscles(),
                this.hasSkinsuit(),
                this.getAugment()
            );
            this.lastGrantedAdvancementGrowthPhase = this.getGrowthPhase();
        }
    }

    private void tickMilkDrinkSound() {
        if (isDrinkingMilk()) {
            this.playSound(this.getDrinkingSound(this.getOffhandItem()), 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
        }
    }

    private void tickRegeneration() {
        if (this.level.getDifficulty() == Difficulty.PEACEFUL && this.level.getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION)) {
            if (this.getHealth() < this.getMaxHealth() && this.tickCount % 2000 == 0) {
                this.heal(1.0F);
            }
        }
    }

    private void tickDaylight() {
        if (!this.hasSkin() && daylightDetector.isInDaylight(this)) {
            undeadDaylightDamager.applyDamage(this);
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!player.isShiftKeyDown()) {
            if (!player.level.isClientSide() && player.getUUID().equals(getOwnerUUID())) {
                MenuLoaderHelper menuLoaderHelper = OverlordConstants.getInjector().getInstance(MenuLoaderHelper.class);
                menuLoaderHelper.openMenu(player, menuLoaderHelper.getSkeletonMenuProvider(this));
            }
            return InteractionResult.SUCCESS;
        }
        if (player.isCreative()
            && player.getUUID().equals(getOwnerUUID())
            && player.getMainHandItem().getItem() == Items.MILK_BUCKET
            && canGrow()
        ) {
            switch (getGrowthPhase()) {
                case BABY:
                    setGrowthPhase(SkeletonGrowthPhase.QUARTER);
                    milkBucketsDrank = configValues.getQuarterGrownMilkCount();
                    break;
                case QUARTER:
                    setGrowthPhase(SkeletonGrowthPhase.HALF);
                    milkBucketsDrank = configValues.getHalfGrownMilkCount();
                    break;
                case HALF:
                    setGrowthPhase(SkeletonGrowthPhase.THREE_QUARTERS);
                    milkBucketsDrank = configValues.getThreeQuartersGrownMilkCount();
                    break;
                case THREE_QUARTERS:
                    setGrowthPhase(SkeletonGrowthPhase.ADULT);
                    milkBucketsDrank = configValues.getFullyGrownMilkCount();
                    break;
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        this.reapplyPosition();

        if (source != null) {
            this.setDeltaMovement(
                -Mth.cos((this.hurtDir + this.getYRot()) * (float) Math.PI / 180) * 0.1f,
                0.1f,
                -Mth.sin((this.hurtDir + this.getYRot()) * (float) Math.PI / 180) * 0.1f
            );
        } else {
            this.setDeltaMovement(0.0D, 0.1D, 0.0D);
        }

        this.clearFire();
        this.setSharedFlag(0, false);
    }

    @Override
    public int getMainHandSlot() {
        return SkeletonInventory.MAIN_HAND_SLOT;
    }

    @Override
    public int getOffHandSlot() {
        return SkeletonInventory.OFF_HAND_SLOT;
    }

    public boolean isCharging() {
        return this.entityData.get(CHARGING);
    }

    @Override
    public void setChargingCrossbow(boolean charging) {
        this.entityData.set(CHARGING, charging);
    }

    public boolean hasTarget() {
        return this.entityData.get(HAS_TARGET);
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        super.setTarget(target);
        this.entityData.set(HAS_TARGET, target != null);
    }

    public AnimationState getAnimationState() {
        if (this.isCharging()) {
            return AnimationState.CROSSBOW_CHARGE;
        } else if (this.getMainHandItem().getItem() instanceof CrossbowItem) {
            return hasTarget() ? AnimationState.CROSSBOW_AIM : AnimationState.NEUTRAL;
        } else if (this.getMainHandItem().getItem() instanceof BowItem) {
            return this.isAggressive() ? AnimationState.BOW_AND_ARROW : AnimationState.NEUTRAL;
        } else if (this.isAggressive()) {
            return AnimationState.MELEE_ATTACK;
        } else if (this.isDrinkingMilk()) {
            return AnimationState.DRINK;
        }

        return AnimationState.NEUTRAL;
    }

    @Override
    public boolean isAlliedTo(Entity other) {
        return super.isAlliedTo(other)
            || entityAlliances.isAlliedTo(this, other);
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(getStepSound(), 0.15F, 1.0F);
    }

    private SoundEvent getStepSound() {
        return hasPaddedFeet() ? SoundEvents.ZOMBIE_STEP : SoundEvents.SKELETON_STEP;
    }

    private boolean hasPaddedFeet() {
        return (getGrowthPhase() == SkeletonGrowthPhase.ADULT && hasMuscles()) || hasSkin();
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        this.vanishCursedItems();
        this.inventory.dropAll();
    }

    private void vanishCursedItems() {
        List<Integer> slots = inventorySearcher.getSlotsMatching(inventory, EnchantmentHelper::hasVanishingCurse);
        for (int slot : slots) {
            this.inventory.removeItemNoUpdate(slot);
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        if (!hasPlayerlikeBody()) {
            return SoundEvents.SKELETON_HURT;
        }
        if (source == DamageSource.ON_FIRE) {
            return SoundEvents.PLAYER_HURT_ON_FIRE;
        } else if (source == DamageSource.DROWN) {
            return SoundEvents.PLAYER_HURT_DROWN;
        } else if (source == DamageSource.SWEET_BERRY_BUSH) {
            return SoundEvents.PLAYER_HURT_SWEET_BERRY_BUSH;
        } else {
            return SoundEvents.PLAYER_HURT;
        }
    }

    @Override
    protected SoundEvent getDeathSound() {
        return hasPlayerlikeBody() ? SoundEvents.PLAYER_DEATH : SoundEvents.SKELETON_DEATH;
    }

    private boolean hasPlayerlikeBody() {
        return hasMuscles() && getGrowthPhase().ordinal() >= SkeletonGrowthPhase.THREE_QUARTERS.ordinal();
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        ListTag inventoryTag = tag.getList("Inventory", 10);
        this.inventory.deserialize(inventoryTag);
        this.entityData.set(HAS_SKIN, tag.getBoolean("Skin"));
        this.entityData.set(
            SKINSUIT,
            tag.hasUUID("Skinsuit")
                ? Optional.of(tag.getUUID("Skinsuit"))
                : Optional.empty()
        );
        this.setHasMuscles(tag.getBoolean("Muscles"));
        this.setGrowthPhase(SkeletonGrowthPhase.values()[tag.getInt("GrowthPhase")]);
        this.updateAISettings(tag.getCompound("aiSettings"));
        this.milkBucketsDrank = tag.getInt("milkBucketsDrank");
        this.setAugmentBlock(ItemStack.of(tag.getCompound("augment")));
        if (tag.contains("MaxHealth")) {
            this.setMaxHealth(tag.getFloat("MaxHealth"));
        } else {
            this.setMaxHealth(calculateDefaultMaxHealth());
        }

        if (tag.contains("lastGrantedAdvancementGrowthPhase")) {
            this.lastGrantedAdvancementGrowthPhase = SkeletonGrowthPhase.values()[tag.getInt("lastGrantedAdvancementGrowthPhase")];
        }
    }

    private float calculateDefaultMaxHealth() {
        int defaultMaxHealth = 20;
        if (hasMuscles()) {
            defaultMaxHealth += 4;
        }
        if (hasSkin()) {
            defaultMaxHealth += 2;
        }
        return defaultMaxHealth;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
        tag.put("Inventory", this.inventory.serialize(new ListTag()));
        tag.putBoolean("Muscles", hasMuscles());
        tag.putBoolean("Skin", hasSkin());
        if (this.hasSkinsuit()) {
            tag.putUUID("Skinsuit", this.getSkinsuit());
        }
        tag.put("aiSettings", aiSettings.toTag());
        tag.putInt("GrowthPhase", entityData.get(GROWTH_PHASE));
        tag.putInt("milkBucketsDrank", milkBucketsDrank);
        tag.put("augment", entityData.get(AUGMENT_BLOCK).save(new CompoundTag()));
        if (lastGrantedAdvancementGrowthPhase != null) {
            tag.putInt("lastGrantedAdvancementGrowthPhase", lastGrantedAdvancementGrowthPhase.ordinal());
        }
        tag.putFloat("MaxHealth", entityData.get(MAX_HEALTH));
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        if (super.isInvulnerableTo(damageSource)) {
            return true;
        } else if (damageSource == DamageSource.DROWN) {
            return !this.level.getGameRules().getBoolean(GameRules.RULE_DROWNING_DAMAGE);
        } else if (damageSource == DamageSource.FALL) {
            return !this.level.getGameRules().getBoolean(GameRules.RULE_FALL_DAMAGE);
        } else if (damageSource.isFire()) {
            return !this.level.getGameRules().getBoolean(GameRules.RULE_FIRE_DAMAGE);
        } else {
            return false;
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source) || this.getHealth() <= 0) {
            return false;
        }
        amount = applyDifficultyScalingModifiers(source, amount);
        amount = applyAugmentDamageModifiers(source, amount);

        return amount != 0.0F && super.hurt(source, amount);
    }

    private float applyDifficultyScalingModifiers(DamageSource source, float amount) {
        if (source.scalesWithDifficulty()) {
            if (this.level.getDifficulty() == Difficulty.PEACEFUL) {
                amount = Math.min(1.0F, amount);
            } else if (this.level.getDifficulty() == Difficulty.EASY) {
                amount = Math.min(amount / 2.0F + 1.0F, amount);
            } else if (this.level.getDifficulty() == Difficulty.HARD) {
                amount *= 3.0F / 2.0F;
            }
        }

        return amount;
    }

    private float applyAugmentDamageModifiers(DamageSource source, float amount) {
        if (source.isBypassMagic()) {
            return amount;
        }
        if (hasAugment(Augments.FRAGILE)) {
            if (source.isMagic()) {
                amount /= 4.0F;
            }
            if (source.isExplosion() || source.isProjectile()) {
                amount *= 3.0F / 2.0F;
            }
        } else if (hasAugment(Augments.IMPOSTER)) {
            Entity attacker = source.getEntity();
            if (attacker instanceof LivingEntity && ((LivingEntity) attacker).isInvertedHealAndHarm()) {
                amount *= 0.99F;
            }
        } else if (hasAugment(Augments.SLOW_BURN)) {
            if (source.isFire()) {
                amount *= 0.05F;
            }
        } else if (hasAugment(Augments.STURDY)) {
            if (!source.isFire() && !source.isMagic()) {
                amount *= 0.75F;
            } else if (source.isMagic()) {
                amount *= 1.5F;
            }
        } else if (hasAugment(Augments.FIREPROOF)) {
            if (source.isFire()) {
                amount = 0;
            } else {
                amount *= 1.05F;
            }
        }
        return amount;
    }

    @Override
    protected void blockUsingShield(LivingEntity attacker) {
        super.blockUsingShield(attacker);
        if (attacker.getMainHandItem().getItem() instanceof AxeItem) {
            this.disableShield(true);
        }
    }

    @Override
    public void hurtArmor(DamageSource source, float amount) {
        this.inventory.damageArmor(amount);
    }

    @Override
    public void hurtCurrentlyUsedShield(float amount) {
        if (amount < 3 || this.useItem.getItem() != Items.SHIELD) {
            return;
        }
        int i = 1 + Mth.floor(amount);
        InteractionHand hand = this.getUsedItemHand();
        this.useItem.hurtAndBreak(i, this, (playerEntity) -> {
            //TODO make sure this works
            this.broadcastBreakEvent(hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        });
        if (this.useItem.isEmpty()) {
            if (hand == InteractionHand.MAIN_HAND) {
                this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            } else {
                this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
            }

            this.useItem = ItemStack.EMPTY;
            this.playSound(SoundEvents.SHIELD_BREAK, 0.8F, 0.8F + this.level.random.nextFloat() * 0.4F);
        }
    }

    @Override
    protected void actuallyHurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return;
        }
        amount = getDamageAfterArmorAbsorb(source, amount);
        amount = getDamageAfterMagicAbsorb(source, amount);
        amount = applyAbsorptionToDamage(amount);

        if (amount != 0.0F) {
            float health = this.getHealth();
            this.setHealth(this.getHealth() - amount);
            this.getCombatTracker().recordDamage(source, health, amount);
        }
    }

    private float applyAbsorptionToDamage(float amount) {
        float preAbsorbAmount = amount;
        amount = Math.max(amount - this.getAbsorptionAmount(), 0.0F);
        this.setAbsorptionAmount(this.getAbsorptionAmount() - (preAbsorbAmount - amount));
        return amount;
    }


    @Override
    protected void doAutoAttackOnTouch(LivingEntity target) {
        meleeAttackExecutor.attack(this, target, getAttackCooldownProgress(0.5F));
        resetLastAttackedTicks();
    }

    public void disableShield(boolean sprinting) {
        float f = 0.25F + (float) EnchantmentHelper.getBlockEfficiency(this) * 0.05F;
        if (sprinting) {
            f += 0.75F;
        }

        if (this.random.nextFloat() < f) {
            this.getItemCooldownManager().addCooldown(Items.SHIELD, 100);
            this.stopUsingItem();
            this.level.broadcastEntityEvent(this, (byte) 30);
        }
    }

    @Override
    public void travel(Vec3 movementInput) {
        if (this.isSwimming() && !this.isPassenger()) {
            double rotationY = this.getLookAngle().y;
            double verticalResistance = rotationY < -0.2D ? 0.085D : 0.06D;
            if (rotationY <= 0.0D
                || this.jumping
                || !this.level.getBlockState(new BlockPos(this.getX(), this.getY() + 0.9, this.getZ())).getFluidState().isEmpty()
            ) {
                Vec3 vec3d = this.getDeltaMovement();
                this.setDeltaMovement(vec3d.add(0.0D, (rotationY - vec3d.y) * verticalResistance, 0.0D));
            }
        }

        super.travel(movementInput);
    }

    protected boolean doesNotSuffocate(BlockPos pos) {
        return !this.level.getBlockState(pos).isSuffocating(this.level, pos);
    }

    @Override
    public float getSpeed() {
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

    @Override
    public LivingEntity.Fallsounds getFallSounds() {
        if (hasPaddedFeet()) {
            return new LivingEntity.Fallsounds(SoundEvents.PLAYER_SMALL_FALL, SoundEvents.PLAYER_BIG_FALL);
        }
        return new LivingEntity.Fallsounds(SoundEvents.GENERIC_SMALL_FALL, SoundEvents.GENERIC_BIG_FALL);
    }

    @Override
    public boolean shouldShowName() {
        return hasCustomName();
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            return this.inventory.getMainHandStack();
        } else if (slot == EquipmentSlot.OFFHAND) {
            return this.inventory.offHand.get(0);
        } else if (slot.getType() == EquipmentSlot.Type.ARMOR) {
            return this.inventory.armor.get(slot.getIndex());
        }

        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
        if (slot == EquipmentSlot.MAINHAND) {
            this.onEquipItem(slot, this.inventory.mainHand.set(0, stack), stack);
        } else if (slot == EquipmentSlot.OFFHAND) {
            this.onEquipItem(slot, this.inventory.offHand.set(0, stack), stack);
        } else if (slot.getType() == EquipmentSlot.Type.ARMOR) {
            this.onEquipItem(slot, this.inventory.armor.set(slot.getIndex(), stack), stack);
        }
    }

    @Override
    public boolean giveItemStack(ItemStack stack) {
        return this.inventory.insertStack(stack);
    }

    @Override
    public byte getEquipmentSwapTicks() {
        switch (this.getGrowthPhase()) {
            case BABY:
                return 40;
            case QUARTER:
                return 30;
            case HALF:
                return 20;
            case THREE_QUARTERS:
                return 10;
            case ADULT:
                return 5;
        }

        throw new IllegalStateException("Growth phase not found!");
    }

    @Override
    public Iterable<ItemStack> getHandSlots() {
        return Lists.newArrayList(this.getMainHandItem(), this.getOffhandItem());
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return this.inventory.armor;
    }

    public ItemCooldowns getItemCooldownManager() {
        return this.itemCooldownManager;
    }

    @Override
    public boolean canHoldItem(ItemStack stack) {
        EquipmentSlot equipmentSlot = LivingEntity.getEquipmentSlotForItem(stack);
        return this.getItemBySlot(equipmentSlot).isEmpty();
    }

    @Override
    public ItemStack getProjectile(ItemStack rangedWeaponStack) {
        if (equipmentHelper.hasAmmoEquipped(this)) {
            return getOffhandItem();
        }

        return ItemStack.EMPTY;
    }

    public void setGrowthPhase(SkeletonGrowthPhase newPhase) {
        entityData.set(GROWTH_PHASE, newPhase.ordinal());
        refreshDimensions();
    }

    @Override
    public float getScale() {
        return 1 - (0.1f * (4 - entityData.get(GROWTH_PHASE)));
    }

    public SkeletonGrowthPhase getGrowthPhase() {
        return SkeletonGrowthPhase.values()[this.entityData.get(GROWTH_PHASE)];
    }

    public void setSkinsuit(UUID playerId) {
        this.entityData.set(SKINSUIT, Optional.of(playerId));
        if (!level.isClientSide()) {
            //TODO find a way to set left-handedness based on skinsuit
        }
    }

    public boolean hasSkinsuit() {
        return entityData.get(SKINSUIT).isPresent();
    }

    public UUID getSkinsuit() {
        Optional<UUID> skinsuit = entityData.get(SKINSUIT);
        return skinsuit.orElseGet(() -> OverlordConstants.getInjector().getInstance(EmptyUUID.class).get());
    }

    public boolean hasSkin() {
        return this.entityData.get(HAS_SKIN);
    }

    public void setHasSkin(boolean hasSkin) {
        this.entityData.set(HAS_SKIN, hasSkin);
    }

    public boolean hasMuscles() {
        return entityData.get(HAS_MUSCLES);
    }

    @SuppressWarnings("ConstantConditions")
    public void setHasMuscles(boolean hasMuscles) {
        this.entityData.set(HAS_MUSCLES, hasMuscles);

        this.getAttribute(Attributes.ATTACK_DAMAGE).removeModifier(MUSCLE_ATTACK_BONUS);
        this.getAttribute(Attributes.ARMOR_TOUGHNESS).removeModifier(MUSCLE_TOUGHNESS_BONUS);
        this.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(MUSCLE_SPEED_BONUS);
        if (hasMuscles) {
            this.getAttribute(Attributes.ATTACK_DAMAGE).addPermanentModifier(MUSCLE_ATTACK_BONUS);
            this.getAttribute(Attributes.ARMOR_TOUGHNESS).addPermanentModifier(MUSCLE_TOUGHNESS_BONUS);
            this.getAttribute(Attributes.MOVEMENT_SPEED).addPermanentModifier(MUSCLE_SPEED_BONUS);
        }
    }

    public void setMaxHealth(float maxHealth) {
        this.entityData.set(MAX_HEALTH, maxHealth);

        //noinspection ConstantConditions
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHealth);
    }

    public OwnedSkeletonContainer getContainer(Inventory playerInv, int syncId) {
        return new OwnedSkeletonContainer(playerInv, !level.isClientSide, this, syncId);
    }

    @Override
    public boolean isUsingItem() {
        return super.isUsingItem();
    }

    @Override
    public SkeletonInventory getInventory() {
        return inventory;
    }

    @Override
    public void shootCrossbowProjectile(LivingEntity target, ItemStack crossbow, Projectile projectile, float multiShotSpray) {
        shootCrossbow(target, crossbow, projectile, multiShotSpray);
    }

    @Override
    public void onCrossbowAttackPerformed() {

    }

    private void shootCrossbow(LivingEntity target, ItemStack crossbow, Projectile projectile, float multiShotSpray) {
        if (projectile == null) {
            OverlordConstants.getLogger().warn("Projectile is not an entity! {}", projectile.getClass());
            return;
        }
        boolean isMultishotProjectile = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MULTISHOT, crossbow) > 0;
        if (projectile instanceof AbstractArrow && !isMultishotProjectile) {
            ((AbstractArrow) projectile).pickup = AbstractArrow.Pickup.ALLOWED;
        }
        double d = target.getX() - this.getX();
        double e = target.getZ() - this.getZ();
        double f = Mth.sqrt((float) (d * d + e * e));
        double g = target.getY(1.0 / 3.0) - projectile.getY() + f * 0.2;
        Vector3f vector3f = this.getProjectileVelocity(new Vec3(d, g, e), multiShotSpray);
        projectile.shoot(vector3f.x(), vector3f.y(), vector3f.z(), 1.6F, (float) (14 - this.level.getDifficulty().getId() * 4));
        this.playSound(SoundEvents.CROSSBOW_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
    }

    private Vector3f getProjectileVelocity(Vec3 vec3d, float multiShotSpray) {
        Vec3 vec3d2 = vec3d.normalize();
        Vec3 vec3d3 = vec3d2.cross(new Vec3(0.0D, 1.0D, 0.0D));
        if (vec3d3.lengthSqr() <= 1.0E-7D) {
            vec3d3 = vec3d2.cross(this.getUpVector(1.0F));
        }

        Quaternion quaternion = new Quaternion(new Vector3f(vec3d3), 90.0F, true);
        Vector3f vector3f = new Vector3f(vec3d2);
        vector3f.transform(quaternion);
        Quaternion quaternion2 = new Quaternion(vector3f, multiShotSpray, true);
        Vector3f vector3f2 = new Vector3f(vec3d2);
        vector3f2.transform(quaternion2);
        return vector3f2;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float f) {
        ItemStack mainHandStack = this.getMainHandItem();
        if (mainHandStack.getItem() instanceof CrossbowItem) {//TODO adjust numbers as needed
            CrossbowItem.performShooting(this.level, this, InteractionHand.MAIN_HAND, mainHandStack, 1.6F, (float) (14 - this.level.getDifficulty().getId() * 4));
        } else if (mainHandStack.getItem() instanceof BowItem) {
            shootBow(target, f);
        }
    }

    public void shootBow(LivingEntity target, float f) {
        ItemStack arrowStack = this.getProjectile(this.getMainHandItem());
        AbstractArrow projectileEntity = this.createArrowProjectile(arrowStack, f);
        boolean hasInfinity = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, getMainHandItem()) > 0;
        boolean usingInfinity = hasInfinity && arrowStack.is(Items.ARROW);
        if (!usingInfinity) {
            projectileEntity.pickup = AbstractArrow.Pickup.ALLOWED;
        }
        double d = target.getX() - this.getX();
        double e = target.getY(1.0 / 3.0) - projectileEntity.getY();
        double g = target.getZ() - this.getZ();
        double h = Mth.sqrt((float) (d * d + g * g));
        projectileEntity.shoot(d, e + h * 0.2, g, 1.6F, (float) (14 - this.level.getDifficulty().getId() * 4));
        this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level.addFreshEntity(projectileEntity);
        if (!usingInfinity) {
            if (arrowStack.getCount() == 1) {
                setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
            } else {
                getOffhandItem().setCount(arrowStack.getCount() - 1);
            }
        }
        this.getMainHandItem().hurtAndBreak(1, this, (entity) -> entity.broadcastBreakEvent(InteractionHand.MAIN_HAND));
    }

    protected AbstractArrow createArrowProjectile(ItemStack arrow, float f) {
        return ProjectileUtil.getMobArrow(this, arrow, f);
    }

    @Override
    public void startDrinkingMilkAnimation() {
        this.entityData.set(DRINKING_MILK, true);
    }

    @Override
    public void completeDrinkingMilk() {
        this.entityData.set(DRINKING_MILK, false);
        if (canUseMilkToFullHealingPotential() || !canGrow()) {
            heal(Math.min(getMilkRestoreAmount(), getMaxHealth() - getHealth()));
            //TODO crop growth type particles maybe?
            return;
        }
        milkBucketsDrank++;
        checkForGrowth();
    }

    protected boolean canUseMilkToFullHealingPotential() {
        return getMaxHealth() - getHealth() >= getMilkRestoreAmount();
    }

    protected float getMilkRestoreAmount() {
        return 6;
    }

    protected void checkForGrowth() {
        //TODO particles?
        switch (this.getGrowthPhase()) {
            case BABY:
                if (milkBucketsDrank >= configValues.getQuarterGrownMilkCount()) {
                    setGrowthPhase(SkeletonGrowthPhase.QUARTER);
                }
                break;
            case QUARTER:
                if (milkBucketsDrank >= configValues.getHalfGrownMilkCount()) {
                    setGrowthPhase(SkeletonGrowthPhase.HALF);
                }
                break;
            case HALF:
                if (milkBucketsDrank >= configValues.getThreeQuartersGrownMilkCount()) {
                    setGrowthPhase(SkeletonGrowthPhase.THREE_QUARTERS);
                }
                break;
            case THREE_QUARTERS:
                if (milkBucketsDrank >= configValues.getFullyGrownMilkCount()) {
                    setGrowthPhase(SkeletonGrowthPhase.ADULT);
                }
                break;
        }
    }

    protected boolean canGrow() {
        return getGrowthPhase() != SkeletonGrowthPhase.ADULT;
    }

    @Override
    public boolean isDrinkingMilk() {
        return this.entityData.get(DRINKING_MILK);
    }

    @Override
    public boolean canDrinkMilk() {
        return canGrow() || getHealth() < getMaxHealth();
    }

    @Override
    protected int addBasicSurvivalGoals(int goalWeight, MovementCategory movement) {
        goalWeight = super.addBasicSurvivalGoals(goalWeight, movement);
        this.goalSelector.addGoal(goalWeight++, new DrinkMilkForHealthGoal<>(this));
        return goalWeight;
    }

    @Override
    protected int addTaskGoals(int goalWeight, TasksCategory tasks) {
        goalWeight = super.addTaskGoals(goalWeight, tasks);
        this.goalSelector.addGoal(goalWeight++, new DrinkMilkGoal<>(this));
        return goalWeight;
    }

    @Override
    public boolean hasAugment(ResourceLocation augment) {
        Item augmentBlockItem = getAugmentBlockStack().getItem();
        return augmentBlockItem instanceof BlockItem && augment.equals(headBlockAugmentRegistry.get(((BlockItem) augmentBlockItem).getBlock()));
    }

    public void setAugmentBlock(ItemStack augmentBlock) {
        this.entityData.set(AUGMENT_BLOCK, augmentBlock);
    }

    @Nullable
    public ResourceLocation getAugment() {
        Item augmentBlockItem = getAugmentBlockStack().getItem();
        if (augmentBlockItem instanceof BlockItem) {
            return headBlockAugmentRegistry.get(((BlockItem) augmentBlockItem).getBlock());
        }

        return null;
    }

    public enum AnimationState
    {
        MELEE_ATTACK,
        BOW_AND_ARROW,
        CROSSBOW_AIM,
        CROSSBOW_CHARGE,
        DRINK,
        NEUTRAL
    }

    public static AttributeSupplier.Builder createOwnedSkeletonAttributes() {
        return ArmyEntity.createArmyAttributes().add(Attributes.MOVEMENT_SPEED, 0.25D);
    }
}
