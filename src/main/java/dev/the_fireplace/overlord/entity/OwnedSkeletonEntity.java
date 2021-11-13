package dev.the_fireplace.overlord.entity;

import com.google.common.collect.Lists;
import com.google.inject.Injector;
import dev.the_fireplace.annotateddi.api.DIContainer;
import dev.the_fireplace.lib.api.uuid.injectables.EmptyUUID;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.domain.entity.AnimatedMilkDrinker;
import dev.the_fireplace.overlord.domain.entity.AugmentBearer;
import dev.the_fireplace.overlord.domain.entity.Ownable;
import dev.the_fireplace.overlord.domain.inventory.InventorySearcher;
import dev.the_fireplace.overlord.domain.registry.HeadBlockAugmentRegistry;
import dev.the_fireplace.overlord.domain.world.DaylightDetector;
import dev.the_fireplace.overlord.domain.world.MeleeAttackExecutor;
import dev.the_fireplace.overlord.domain.world.UndeadDaylightDamager;
import dev.the_fireplace.overlord.entity.ai.goal.AIEquipmentHelper;
import dev.the_fireplace.overlord.entity.ai.goal.equipment.skeleton.DrinkMilkForHealthGoal;
import dev.the_fireplace.overlord.entity.ai.goal.equipment.skeleton.DrinkMilkGoal;
import dev.the_fireplace.overlord.init.Augments;
import dev.the_fireplace.overlord.init.OverlordEntities;
import dev.the_fireplace.overlord.model.aiconfig.movement.MovementCategory;
import dev.the_fireplace.overlord.model.aiconfig.tasks.TasksCategory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.SharedConstants;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

public class OwnedSkeletonEntity extends ArmyEntity implements RangedAttackMob, CrossbowUser, AnimatedMilkDrinker, AugmentBearer
{
    public static final int CHILD_REQUIRED_MILK = 4;
    public static final int PRETEEN_REQUIRED_MILK = 16;
    public static final int TEEN_REQUIRED_MILK = 64;
    public static final int ADULT_REQUIRED_MILK = 256;

    private static final TrackedData<Boolean> CHARGING = DataTracker.registerData(OwnedSkeletonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> DRINKING_MILK = DataTracker.registerData(OwnedSkeletonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> HAS_TARGET = DataTracker.registerData(OwnedSkeletonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> GROWTH_PHASE = DataTracker.registerData(OwnedSkeletonEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> HAS_SKIN = DataTracker.registerData(OwnedSkeletonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> HAS_MUSCLES = DataTracker.registerData(OwnedSkeletonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Optional<UUID>> SKINSUIT = DataTracker.registerData(OwnedSkeletonEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    private static final TrackedData<ItemStack> AUGMENT_BLOCK = DataTracker.registerData(OwnedSkeletonEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private static final UUID MUSCLE_ATTACK_BONUS_ID = MathHelper.randomUuid(new Random("Muscle Attack Bonus".hashCode()));
    private static final UUID MUSCLE_TOUGHNESS_BONUS_ID = MathHelper.randomUuid(new Random("Muscle Toughness Bonus".hashCode()));
    private static final UUID MUSCLE_SPEED_BONUS_ID = MathHelper.randomUuid(new Random("Muscle Speed Bonus".hashCode()));
    private static final EntityAttributeModifier MUSCLE_ATTACK_BONUS = new EntityAttributeModifier(MUSCLE_ATTACK_BONUS_ID, "Muscle Attack Bonus", 2.0D, EntityAttributeModifier.Operation.ADDITION);
    private static final EntityAttributeModifier MUSCLE_TOUGHNESS_BONUS = new EntityAttributeModifier(MUSCLE_TOUGHNESS_BONUS_ID, "Muscle Toughness Bonus", 0.25D, EntityAttributeModifier.Operation.ADDITION);
    private static final EntityAttributeModifier MUSCLE_SPEED_BONUS = new EntityAttributeModifier(MUSCLE_SPEED_BONUS_ID, "Muscle Speed Bonus", 0.05D, EntityAttributeModifier.Operation.ADDITION);

    private UUID owner = new UUID(801295133947085751L, -7395604847578632613L);
    private int milkBucketsDrank = 0;

    private final SkeletonInventory inventory = new SkeletonInventory(this);
    private final ItemCooldownManager itemCooldownManager = new ItemCooldownManager();

    private final DaylightDetector daylightDetector;
    private final UndeadDaylightDamager undeadDaylightDamager;
    private final MeleeAttackExecutor meleeAttackExecutor;
    private final InventorySearcher inventorySearcher;
    private final AIEquipmentHelper equipmentHelper;
    private final HeadBlockAugmentRegistry headBlockAugmentRegistry;

    /**
     * @deprecated Only public because Minecraft requires it to be. Use the factory.
     * Intended for internal use, but it technically works. Use {@link OwnedSkeletonEntity#create(World, UUID)} when possible.
     */
    @Deprecated
    public OwnedSkeletonEntity(EntityType<? extends OwnedSkeletonEntity> type, World world) {
        super(type, world);
        this.setLeftHanded(this.random.nextFloat() < 0.05F);
        Injector injector = DIContainer.get();
        daylightDetector = injector.getInstance(DaylightDetector.class);
        undeadDaylightDamager = injector.getInstance(UndeadDaylightDamager.class);
        meleeAttackExecutor = injector.getInstance(MeleeAttackExecutor.class);
        inventorySearcher = injector.getInstance(InventorySearcher.class);
        equipmentHelper = injector.getInstance(AIEquipmentHelper.class);
        headBlockAugmentRegistry = injector.getInstance(HeadBlockAugmentRegistry.class);
        calculateDimensions();
    }

    public static OwnedSkeletonEntity create(World world, @Nullable UUID owner) {
        OwnedSkeletonEntity e = new OwnedSkeletonEntity(OverlordEntities.OWNED_SKELETON_TYPE, world);
        if (owner != null) {
            e.setOwner(owner);
        }
        return e;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(CHARGING, false);
        this.dataTracker.startTracking(HAS_TARGET, false);
        this.dataTracker.startTracking(DRINKING_MILK, false);
        this.dataTracker.startTracking(GROWTH_PHASE, SkeletonGrowthPhase.BABY.ordinal());
        this.dataTracker.startTracking(HAS_SKIN, false);
        this.dataTracker.startTracking(HAS_MUSCLES, false);
        this.dataTracker.startTracking(SKINSUIT, Optional.empty());
        this.dataTracker.startTracking(AUGMENT_BLOCK, ItemStack.EMPTY);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        // Scale the hitbox on the client
        if (world.isClient() && GROWTH_PHASE.equals(data)) {
            calculateDimensions();
        }
    }

    public ItemStack getAugmentBlockStack() {
        return dataTracker.get(AUGMENT_BLOCK);
    }

    @Override
    protected void mobTick() {
        super.mobTick();
        tickRegeneration();
        this.getInventory().tickItems();
        tickDaylight();
        tickMilkDrinkSound();
    }

    private void tickMilkDrinkSound() {
        if (isDrinkingMilk()) {
            this.playSound(this.getDrinkSound(this.getOffHandStack()), 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
        }
    }

    private void tickRegeneration() {
        if (this.world.getDifficulty() == Difficulty.PEACEFUL && this.world.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION)) {
            if (this.getHealth() < this.getMaxHealth() && this.age % 2000 == 0) {
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
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (!player.world.isClient() && !player.isSneaking()) {
            player.openHandledScreen(new ExtendedScreenHandlerFactory()
            {
                @Override
                public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
                    buf.writeUuid(OwnedSkeletonEntity.this.getUuid());
                }

                @Override
                public Text getDisplayName() {
                    return OwnedSkeletonEntity.this.getDisplayName();
                }

                @Override
                public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                    return OwnedSkeletonEntity.this.getContainer(inv, syncId);
                }
            });
        }
        if (player.isSneaking()
            && player.isCreative()
            && player.getUuid().equals(getOwnerId())
            && player.getMainHandStack().getItem() == Items.MILK_BUCKET
            && canGrow()
        ) {
            switch (getGrowthPhase()) {
                case BABY:
                    setGrowthPhase(SkeletonGrowthPhase.CHILD);
                    milkBucketsDrank = CHILD_REQUIRED_MILK;
                    break;
                case CHILD:
                    setGrowthPhase(SkeletonGrowthPhase.PRETEEN);
                    milkBucketsDrank = PRETEEN_REQUIRED_MILK;
                    break;
                case PRETEEN:
                    setGrowthPhase(SkeletonGrowthPhase.TEEN);
                    milkBucketsDrank = TEEN_REQUIRED_MILK;
                    break;
                case TEEN:
                    setGrowthPhase(SkeletonGrowthPhase.ADULT);
                    milkBucketsDrank = ADULT_REQUIRED_MILK;
                    break;
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        this.refreshPosition();

        if (source != null) {
            this.setVelocity(
                -MathHelper.cos((this.knockbackVelocity + this.yaw) * (float) Math.PI / 180) * 0.1f,
                0.1f,
                -MathHelper.sin((this.knockbackVelocity + this.yaw) * (float) Math.PI / 180) * 0.1f
            );
        } else {
            this.setVelocity(0.0D, 0.1D, 0.0D);
        }

        this.extinguish();
        this.setFlag(0, false);
    }

    @Override
    public int getMainHandSlot() {
        return SkeletonInventory.MAIN_HAND_SLOT;
    }

    @Override
    public int getOffHandSlot() {
        return SkeletonInventory.OFF_HAND_SLOT;
    }

    @Environment(EnvType.CLIENT)
    public boolean isCharging() {
        return this.dataTracker.get(CHARGING);
    }

    @Override
    public void setCharging(boolean charging) {
        this.dataTracker.set(CHARGING, charging);
    }

    @Environment(EnvType.CLIENT)
    public boolean hasTarget() {
        return this.dataTracker.get(HAS_TARGET);
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        super.setTarget(target);
        this.dataTracker.set(HAS_TARGET, target != null);
    }

    @Environment(EnvType.CLIENT)
    public AnimationState getAnimationState() {
        if (this.isCharging()) {
            return AnimationState.CROSSBOW_CHARGE;
        } else if (this.getMainHandStack().getItem() instanceof CrossbowItem) {
            return hasTarget() ? AnimationState.CROSSBOW_AIM : AnimationState.NEUTRAL;
        } else if (this.getMainHandStack().getItem() instanceof BowItem) {
            return this.isAttacking() ? AnimationState.BOW_AND_ARROW : AnimationState.NEUTRAL;
        } else if (this.isAttacking()) {
            return AnimationState.MELEE_ATTACK;
        } else if (this.isDrinkingMilk()) {
            return AnimationState.DRINK;
        }

        return AnimationState.NEUTRAL;
    }

    @Override
    public boolean isTeammate(Entity other) {
        return super.isTeammate(other)
            || other.getUuid().equals(getOwnerId())
            || (other instanceof Ownable && ((Ownable) other).getOwnerId().equals(getOwnerId()));
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(getStepSound(), 0.15F, 1.0F);
    }

    private SoundEvent getStepSound() {
        return hasPaddedFeet() ? SoundEvents.ENTITY_ZOMBIE_STEP : SoundEvents.ENTITY_SKELETON_STEP;
    }

    private boolean hasPaddedFeet() {
        return (getGrowthPhase() == SkeletonGrowthPhase.ADULT && hasMuscles()) || hasSkin();
    }

    @Override
    public EntityGroup getGroup() {
        return EntityGroup.UNDEAD;
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
        this.vanishCursedItems();
        this.inventory.dropAll();
    }

    private void vanishCursedItems() {
        List<Integer> slots = inventorySearcher.getSlotsMatching(inventory, EnchantmentHelper::hasVanishingCurse);
        for (int slot : slots) {
            this.inventory.removeStack(slot);
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        if (!hasPlayerlikeBody()) {
            return SoundEvents.ENTITY_SKELETON_HURT;
        }
        if (source == DamageSource.ON_FIRE) {
            return SoundEvents.ENTITY_PLAYER_HURT_ON_FIRE;
        } else if (source == DamageSource.DROWN) {
            return SoundEvents.ENTITY_PLAYER_HURT_DROWN;
        } else if (source == DamageSource.SWEET_BERRY_BUSH) {
            return SoundEvents.ENTITY_PLAYER_HURT_SWEET_BERRY_BUSH;
        } else {
            return SoundEvents.ENTITY_PLAYER_HURT;
        }
    }

    @Override
    protected SoundEvent getDeathSound() {
        return hasPlayerlikeBody() ? SoundEvents.ENTITY_PLAYER_DEATH : SoundEvents.ENTITY_SKELETON_DEATH;
    }

    private boolean hasPlayerlikeBody() {
        return hasMuscles() && getGrowthPhase().ordinal() >= SkeletonGrowthPhase.TEEN.ordinal();
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound tag) {
        super.readCustomDataFromNbt(tag);
        NbtList inventoryTag = tag.getList("Inventory", 10);
        this.inventory.deserialize(inventoryTag);
        this.owner = tag.getUuid("Owner");
        this.dataTracker.set(HAS_SKIN, tag.getBoolean("Skin"));
        this.dataTracker.set(
            SKINSUIT,
            tag.containsUuid("Skinsuit")
                ? Optional.of(tag.getUuid("Skinsuit"))
                : Optional.empty()
        );
        this.setHasMuscles(tag.getBoolean("Muscles"));
        this.setGrowthPhase(SkeletonGrowthPhase.values()[tag.getInt("GrowthPhase")]);
        this.updateAISettings(tag.getCompound("aiSettings"));
        this.milkBucketsDrank = tag.getInt("milkBucketsDrank");
        this.setAugmentBlock(ItemStack.fromNbt(tag.getCompound("augment")));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound tag) {
        super.writeCustomDataToNbt(tag);
        tag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        tag.put("Inventory", this.inventory.serialize(new NbtList()));
        tag.putUuid("Owner", this.owner);
        tag.putBoolean("Muscles", hasMuscles());
        tag.putBoolean("Skin", hasSkin());
        if (this.hasSkinsuit()) {
            tag.putUuid("Skinsuit", this.getSkinsuit());
        }
        tag.put("aiSettings", aiSettings.toTag());
        tag.putInt("GrowthPhase", dataTracker.get(GROWTH_PHASE));
        tag.putInt("milkBucketsDrank", milkBucketsDrank);
        tag.put("augment", dataTracker.get(AUGMENT_BLOCK).writeNbt(new NbtCompound()));
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        if (super.isInvulnerableTo(damageSource)) {
            return true;
        } else if (damageSource == DamageSource.DROWN) {
            return !this.world.getGameRules().getBoolean(GameRules.DROWNING_DAMAGE);
        } else if (damageSource == DamageSource.FALL) {
            return !this.world.getGameRules().getBoolean(GameRules.FALL_DAMAGE);
        } else if (damageSource.isFire()) {
            return !this.world.getGameRules().getBoolean(GameRules.FIRE_DAMAGE);
        } else {
            return false;
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source) || this.getHealth() <= 0) {
            return false;
        }
        amount = applyDifficultyScalingModifiers(source, amount);
        amount = applyAugmentDamageModifiers(source, amount);

        return amount != 0.0F && super.damage(source, amount);
    }

    private float applyDifficultyScalingModifiers(DamageSource source, float amount) {
        if (source.isScaledWithDifficulty()) {
            if (this.world.getDifficulty() == Difficulty.PEACEFUL) {
                amount = Math.min(1.0F, amount);
            } else if (this.world.getDifficulty() == Difficulty.EASY) {
                amount = Math.min(amount / 2.0F + 1.0F, amount);
            } else if (this.world.getDifficulty() == Difficulty.HARD) {
                amount *= 3.0F / 2.0F;
            }
        }

        return amount;
    }

    private float applyAugmentDamageModifiers(DamageSource source, float amount) {
        if (hasAugment(Augments.FRAGILE)) {
            if (source.isMagic()) {
                amount /= 4.0F;
            }
            if (source.isExplosive() || source.isProjectile()) {
                amount *= 3.0F / 2.0F;
            }
        } else if (hasAugment(Augments.IMPOSTER)) {
            Entity attacker = source.getAttacker();
            if (attacker instanceof LivingEntity && ((LivingEntity) attacker).isUndead()) {
                amount *= 0.99F;
            }
        } else if (hasAugment(Augments.SLOW_BURN)) {
            if (source.isFire()) {
                amount *= 0.05F;
            }
        }
        return amount;
    }

    @Override
    protected void takeShieldHit(LivingEntity attacker) {
        super.takeShieldHit(attacker);
        if (attacker.getMainHandStack().getItem() instanceof AxeItem) {
            this.disableShield(true);
        }
    }

    @Override
    protected void damageArmor(DamageSource source, float amount) {
        this.inventory.damageArmor(amount);
    }

    @Override
    protected void damageShield(float amount) {
        if (amount < 3 || this.activeItemStack.getItem() != Items.SHIELD) {
            return;
        }
        int i = 1 + MathHelper.floor(amount);
        Hand hand = this.getActiveHand();
        this.activeItemStack.damage(i, this, (playerEntity) -> {
            //TODO make sure this works
            this.sendEquipmentBreakStatus(hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        });
        if (this.activeItemStack.isEmpty()) {
            if (hand == Hand.MAIN_HAND) {
                this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            } else {
                this.equipStack(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
            }

            this.activeItemStack = ItemStack.EMPTY;
            this.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8F, 0.8F + this.world.random.nextFloat() * 0.4F);
        }
    }

    @Override
    protected void applyDamage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return;
        }
        amount = applyArmorToDamage(source, amount);
        amount = applyEnchantmentsToDamage(source, amount);
        amount = applyAbsorptionToDamage(amount);

        if (amount != 0.0F) {
            float health = this.getHealth();
            this.setHealth(this.getHealth() - amount);
            this.getDamageTracker().onDamage(source, health, amount);
        }
    }

    private float applyAbsorptionToDamage(float amount) {
        float preAbsorbAmount = amount;
        amount = Math.max(amount - this.getAbsorptionAmount(), 0.0F);
        this.setAbsorptionAmount(this.getAbsorptionAmount() - (preAbsorbAmount - amount));
        return amount;
    }


    @Override
    protected void attackLivingEntity(LivingEntity target) {
        meleeAttackExecutor.attack(this, target, getAttackCooldownProgress(0.5F));
        resetLastAttackedTicks();
    }

    public void disableShield(boolean sprinting) {
        float f = 0.25F + (float) EnchantmentHelper.getEfficiency(this) * 0.05F;
        if (sprinting) {
            f += 0.75F;
        }

        if (this.random.nextFloat() < f) {
            this.getItemCooldownManager().set(Items.SHIELD, 100);
            this.clearActiveItem();
            this.world.sendEntityStatus(this, (byte) 30);
        }
    }

    @Override
    public void travel(Vec3d movementInput) {
        if (this.isSwimming() && !this.hasVehicle()) {
            double rotationY = this.getRotationVector().y;
            double verticalResistance = rotationY < -0.2D ? 0.085D : 0.06D;
            if (rotationY <= 0.0D
                || this.jumping
                || !this.world.getBlockState(new BlockPos(this.getX(), this.getY() + 0.9, this.getZ())).getFluidState().isEmpty()
            ) {
                Vec3d vec3d = this.getVelocity();
                this.setVelocity(vec3d.add(0.0D, (rotationY - vec3d.y) * verticalResistance, 0.0D));
            }
        }

        super.travel(movementInput);
    }

    protected boolean doesNotSuffocate(BlockPos pos) {
        return !this.world.getBlockState(pos).shouldSuffocate(this.world, pos);
    }

    @Override
    public float getMovementSpeed() {
        return (float) this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
    }

    @Override
    protected SoundEvent getFallSound(int distance) {
        if (hasPaddedFeet()) {
            return distance > 4 ? SoundEvents.ENTITY_PLAYER_BIG_FALL : SoundEvents.ENTITY_PLAYER_SMALL_FALL;
        }

        return super.getFallSound(distance);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public boolean shouldRenderName() {
        return hasCustomName();
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            return this.inventory.getMainHandStack();
        } else if (slot == EquipmentSlot.OFFHAND) {
            return this.inventory.offHand.get(0);
        } else if (slot.getType() == EquipmentSlot.Type.ARMOR) {
            return this.inventory.armor.get(slot.getEntitySlotId());
        }

        return ItemStack.EMPTY;
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {
        if (slot == EquipmentSlot.MAINHAND) {
            this.onEquipStack(stack);
            this.inventory.mainHand.set(0, stack);
        } else if (slot == EquipmentSlot.OFFHAND) {
            this.onEquipStack(stack);
            this.inventory.offHand.set(0, stack);
        } else if (slot.getType() == EquipmentSlot.Type.ARMOR) {
            this.onEquipStack(stack);
            this.inventory.armor.set(slot.getEntitySlotId(), stack);
        }
    }

    @Override
    public boolean giveItemStack(ItemStack stack) {
        this.onEquipStack(stack);
        return this.inventory.insertStack(stack);
    }

    @Override
    public byte getEquipmentSwapTicks() {
        switch (this.getGrowthPhase()) {
            case BABY:
                return 40;
            case CHILD:
                return 30;
            case PRETEEN:
                return 20;
            case TEEN:
                return 10;
            case ADULT:
                return 5;
        }

        throw new IllegalStateException("Growth phase not found!");
    }

    @Override
    public Iterable<ItemStack> getItemsHand() {
        return Lists.newArrayList(this.getMainHandStack(), this.getOffHandStack());
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return this.inventory.armor;
    }

    @Override
    public boolean equip(int slot, ItemStack item) {
        if (slot >= 0 && slot < this.inventory.main.size()) {
            this.inventory.setStack(slot, item);
            return true;
        }
        EquipmentSlot equipmentSlot;
        if (slot == 100 + EquipmentSlot.HEAD.getEntitySlotId()) {
            equipmentSlot = EquipmentSlot.HEAD;
        } else if (slot == 100 + EquipmentSlot.CHEST.getEntitySlotId()) {
            equipmentSlot = EquipmentSlot.CHEST;
        } else if (slot == 100 + EquipmentSlot.LEGS.getEntitySlotId()) {
            equipmentSlot = EquipmentSlot.LEGS;
        } else if (slot == 100 + EquipmentSlot.FEET.getEntitySlotId()) {
            equipmentSlot = EquipmentSlot.FEET;
        } else {
            equipmentSlot = null;
        }

        if (slot == 98) {
            this.equipStack(EquipmentSlot.MAINHAND, item);
            return true;
        } else if (slot == 99) {
            this.equipStack(EquipmentSlot.OFFHAND, item);
            return true;
        }
        if (!item.isEmpty() && MobEntity.getPreferredEquipmentSlot(item) != equipmentSlot) {
            return false;
        }

        this.inventory.setStack((equipmentSlot != null ? equipmentSlot.getEntitySlotId() : 0) + this.inventory.main.size(), item);
        return true;
    }

    public ItemCooldownManager getItemCooldownManager() {
        return this.itemCooldownManager;
    }

    @Override
    public boolean canPickupItem(ItemStack stack) {
        EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(stack);
        return this.getEquippedStack(equipmentSlot).isEmpty();
    }

    @Override
    public ItemStack getArrowType(ItemStack rangedWeaponStack) {
        if (equipmentHelper.hasAmmoEquipped(this)) {
            return getOffHandStack();
        }

        return ItemStack.EMPTY;
    }

    public void setGrowthPhase(SkeletonGrowthPhase newPhase) {
        dataTracker.set(GROWTH_PHASE, newPhase.ordinal());
        calculateDimensions();
    }

    @Override
    public float getScaleFactor() {
        return 1 - (0.1f * (4 - dataTracker.get(GROWTH_PHASE)));
    }

    public SkeletonGrowthPhase getGrowthPhase() {
        return SkeletonGrowthPhase.values()[this.dataTracker.get(GROWTH_PHASE)];
    }

    public void setSkinsuit(UUID playerId) {
        this.dataTracker.set(SKINSUIT, Optional.of(playerId));
        if (!world.isClient()) {
            //TODO find a way to set left-handedness based on skinsuit
        }
    }

    public boolean hasSkinsuit() {
        return dataTracker.get(SKINSUIT).isPresent();
    }

    public UUID getSkinsuit() {
        Optional<UUID> skinsuit = dataTracker.get(SKINSUIT);
        return skinsuit.orElseGet(() -> DIContainer.get().getInstance(EmptyUUID.class).get());
    }

    public boolean hasSkin() {
        return this.dataTracker.get(HAS_SKIN);
    }

    public void setHasSkin(boolean hasSkin) {
        this.dataTracker.set(HAS_SKIN, hasSkin);
    }

    public boolean hasMuscles() {
        return dataTracker.get(HAS_MUSCLES);
    }

    public void setHasMuscles(boolean hasMuscles) {
        this.dataTracker.set(HAS_MUSCLES, hasMuscles);

        this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).removeModifier(MUSCLE_ATTACK_BONUS);
        this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).removeModifier(MUSCLE_TOUGHNESS_BONUS);
        this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).removeModifier(MUSCLE_SPEED_BONUS);
        if (hasMuscles) {
            this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).addPersistentModifier(MUSCLE_ATTACK_BONUS);
            this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).addPersistentModifier(MUSCLE_TOUGHNESS_BONUS);
            this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).addPersistentModifier(MUSCLE_SPEED_BONUS);
        }
    }

    @Override
    public UUID getOwnerId() {
        return owner;
    }

    @Nullable
    @Override
    public LivingEntity getOwner() {
        if (this.world == null || !(world instanceof ServerWorld)) {
            return null;
        }

        Entity entity = ((ServerWorld) this.world).getEntity(this.getOwnerId());
        return entity instanceof LivingEntity ? (LivingEntity) entity : null;
    }

    public OwnedSkeletonContainer getContainer(PlayerInventory playerInv, int syncId) {
        return new OwnedSkeletonContainer(playerInv, !world.isClient, this, syncId);
    }

    @Override
    public boolean isUsingItem() {
        return super.isUsingItem();
    }

    @Override
    public SkeletonInventory getInventory() {
        return inventory;
    }

    public void setOwner(UUID newOwner) {
        this.owner = newOwner;
    }

    @Override
    public void shoot(LivingEntity target, ItemStack crossbow, ProjectileEntity projectile, float multiShotSpray) {
        shootCrossbow(target, crossbow, projectile, multiShotSpray);
    }

    @Override
    public void postShoot() {

    }

    private void shootCrossbow(LivingEntity target, ItemStack crossbow, ProjectileEntity projectile, float multiShotSpray) {
        if (projectile == null) {
            Overlord.getLogger().warn("Projectile is not an entity! {}", projectile.getClass());
            return;
        }
        boolean isMultishotProjectile = EnchantmentHelper.getLevel(Enchantments.MULTISHOT, crossbow) > 0;
        if (projectile instanceof PersistentProjectileEntity && !isMultishotProjectile) {
            ((PersistentProjectileEntity) projectile).pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED;
        }
        double d = target.getX() - this.getX();
        double e = target.getZ() - this.getZ();
        double f = MathHelper.sqrt(d * d + e * e);
        double g = target.getBodyY(1.0 / 3.0) - projectile.getY() + f * 0.2;
        Vec3f vector3f = this.getProjectileVelocity(new Vec3d(d, g, e), multiShotSpray);
        projectile.setVelocity(vector3f.getX(), vector3f.getY(), vector3f.getZ(), 1.6F, (float) (14 - this.world.getDifficulty().getId() * 4));
        this.playSound(SoundEvents.ITEM_CROSSBOW_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
    }

    private Vec3f getProjectileVelocity(Vec3d vec3d, float multiShotSpray) {
        Vec3d vec3d2 = vec3d.normalize();
        Vec3d vec3d3 = vec3d2.crossProduct(new Vec3d(0.0D, 1.0D, 0.0D));
        if (vec3d3.lengthSquared() <= 1.0E-7D) {
            vec3d3 = vec3d2.crossProduct(this.getOppositeRotationVector(1.0F));
        }

        Quaternion quaternion = new Quaternion(new Vec3f(vec3d3), 90.0F, true);
        Vec3f vector3f = new Vec3f(vec3d2);
        vector3f.rotate(quaternion);
        Quaternion quaternion2 = new Quaternion(vector3f, multiShotSpray, true);
        Vec3f vector3f2 = new Vec3f(vec3d2);
        vector3f2.rotate(quaternion2);
        return vector3f2;
    }

    @Override
    public void attack(LivingEntity target, float f) {
        ItemStack mainHandStack = this.getMainHandStack();
        if (mainHandStack.getItem() instanceof CrossbowItem) {//TODO adjust numbers as needed
            CrossbowItem.shootAll(this.world, this, Hand.MAIN_HAND, mainHandStack, 1.6F, (float) (14 - this.world.getDifficulty().getId() * 4));
        } else if (mainHandStack.getItem() instanceof BowItem) {
            shootBow(target, f);
        }
    }

    public void shootBow(LivingEntity target, float f) {
        ItemStack arrowStack = this.getArrowType(this.getMainHandStack());
        PersistentProjectileEntity projectileEntity = this.createArrowProjectile(arrowStack, f);
        projectileEntity.pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED;
        double d = target.getX() - this.getX();
        double e = target.getBodyY(1.0 / 3.0) - projectileEntity.getY();
        double g = target.getZ() - this.getZ();
        double h = MathHelper.sqrt(d * d + g * g);
        projectileEntity.setVelocity(d, e + h * 0.2, g, 1.6F, (float) (14 - this.world.getDifficulty().getId() * 4));
        this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.world.spawnEntity(projectileEntity);
        if (arrowStack.getCount() == 1) {
            setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
        } else {
            getOffHandStack().setCount(arrowStack.getCount() - 1);
        }
        this.getMainHandStack().damage(1, this, (entity) -> entity.sendToolBreakStatus(Hand.MAIN_HAND));
    }

    protected PersistentProjectileEntity createArrowProjectile(ItemStack arrow, float f) {
        return ProjectileUtil.createArrowProjectile(this, arrow, f);
    }

    @Override
    public void startDrinkingMilkAnimation() {
        this.dataTracker.set(DRINKING_MILK, true);
    }

    @Override
    public void completeDrinkingMilk() {
        this.dataTracker.set(DRINKING_MILK, false);
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
                if (milkBucketsDrank >= CHILD_REQUIRED_MILK) {
                    setGrowthPhase(SkeletonGrowthPhase.CHILD);
                }
                break;
            case CHILD:
                if (milkBucketsDrank >= PRETEEN_REQUIRED_MILK) {
                    setGrowthPhase(SkeletonGrowthPhase.PRETEEN);
                }
                break;
            case PRETEEN:
                if (milkBucketsDrank >= TEEN_REQUIRED_MILK) {
                    setGrowthPhase(SkeletonGrowthPhase.TEEN);
                }
                break;
            case TEEN:
                if (milkBucketsDrank >= ADULT_REQUIRED_MILK) {
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
        return this.dataTracker.get(DRINKING_MILK);
    }

    @Override
    public boolean canDrinkMilk() {
        return canGrow() || getHealth() < getMaxHealth();
    }

    @Override
    protected int addBasicSurvivalGoals(int goalWeight, MovementCategory movement) {
        goalWeight = super.addBasicSurvivalGoals(goalWeight, movement);
        this.goalSelector.add(goalWeight++, new DrinkMilkForHealthGoal<>(this));
        return goalWeight;
    }

    @Override
    protected int addTaskGoals(int goalWeight, TasksCategory tasks) {
        goalWeight = super.addTaskGoals(goalWeight, tasks);
        this.goalSelector.add(goalWeight++, new DrinkMilkGoal<>(this));
        return goalWeight;
    }

    @Override
    public boolean hasAugment(Identifier augment) {
        Item augmentBlockItem = getAugmentBlockStack().getItem();
        return augmentBlockItem instanceof BlockItem && augment.equals(headBlockAugmentRegistry.get(((BlockItem) augmentBlockItem).getBlock()));
    }

    public void setAugmentBlock(ItemStack augmentBlock) {
        this.dataTracker.set(AUGMENT_BLOCK, augmentBlock);
    }

    @Nullable
    public Identifier getAugment() {
        Item augmentBlockItem = getAugmentBlockStack().getItem();
        if (augmentBlockItem instanceof BlockItem) {
            return headBlockAugmentRegistry.get(((BlockItem) augmentBlockItem).getBlock());
        }

        return null;
    }

    @Environment(EnvType.CLIENT)
    public enum AnimationState
    {
        MELEE_ATTACK,
        BOW_AND_ARROW,
        CROSSBOW_AIM,
        CROSSBOW_CHARGE,
        DRINK,
        NEUTRAL
    }

    public static DefaultAttributeContainer.Builder createOwnedSkeletonAttributes() {
        return ArmyEntity.createArmyAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25D);
    }
}
