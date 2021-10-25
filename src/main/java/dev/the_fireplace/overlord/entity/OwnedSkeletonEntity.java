package dev.the_fireplace.overlord.entity;

import com.google.common.collect.Lists;
import com.google.inject.Injector;
import dev.the_fireplace.annotateddi.api.DIContainer;
import dev.the_fireplace.lib.api.uuid.injectables.EmptyUUID;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.domain.inventory.InventorySearcher;
import dev.the_fireplace.overlord.domain.mechanic.Ownable;
import dev.the_fireplace.overlord.domain.world.BreakSpeedModifiers;
import dev.the_fireplace.overlord.domain.world.DaylightDetector;
import dev.the_fireplace.overlord.domain.world.MeleeAttackExecutor;
import dev.the_fireplace.overlord.domain.world.UndeadDaylightDamager;
import dev.the_fireplace.overlord.entity.ai.goal.AIEquipmentHelper;
import dev.the_fireplace.overlord.init.OverlordEntities;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.SharedConstants;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.Projectile;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class OwnedSkeletonEntity extends ArmyEntity implements RangedAttackMob, CrossbowUser
{
    private static final TrackedData<Boolean> CHARGING = DataTracker.registerData(OwnedSkeletonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    //TODO use DataTracker for the other properties?

    private UUID owner = new UUID(801295133947085751L, -7395604847578632613L);
    private UUID skinsuit;
    private SkeletonGrowthPhase growthPhase = SkeletonGrowthPhase.BABY;
    private boolean hasSkin = false;
    private boolean hasMuscles = false;

    private final SkeletonInventory inventory = new SkeletonInventory(this);
    private final ItemCooldownManager itemCooldownManager = new ItemCooldownManager();

    private final DaylightDetector daylightDetector;
    private final UndeadDaylightDamager undeadDaylightDamager;
    private final MeleeAttackExecutor meleeAttackExecutor;
    private final BreakSpeedModifiers breakSpeedModifiers;
    private final InventorySearcher inventorySearcher;
    private final AIEquipmentHelper equipmentHelper;

    /**
     * @deprecated Only public because Minecraft requires it to be. Use the factory.
     * Intended for internal use, but it technically works. Use {@link OwnedSkeletonEntity#create(World, UUID)} when possible.
     */
    @Deprecated
    public OwnedSkeletonEntity(EntityType<? extends OwnedSkeletonEntity> type, World world) {
        super(type, world);
        if (this.random.nextFloat() < 0.05F) {
            this.setLeftHanded(true);
        } else {
            this.setLeftHanded(false);
        }
        Injector injector = DIContainer.get();
        daylightDetector = injector.getInstance(DaylightDetector.class);
        undeadDaylightDamager = injector.getInstance(UndeadDaylightDamager.class);
        meleeAttackExecutor = injector.getInstance(MeleeAttackExecutor.class);
        breakSpeedModifiers = injector.getInstance(BreakSpeedModifiers.class);
        inventorySearcher = injector.getInstance(InventorySearcher.class);
        equipmentHelper = injector.getInstance(AIEquipmentHelper.class);
        setGrowthPhase(SkeletonGrowthPhase.BABY);
    }

    public static OwnedSkeletonEntity create(World world, @Nullable UUID owner) {
        OwnedSkeletonEntity e = new OwnedSkeletonEntity(OverlordEntities.OWNED_SKELETON_TYPE, world);
        if (owner != null) {
            e.setOwner(owner);
        }
        return e;
    }

    @Override
    public void tickMovement() {
        tickRegeneration();
        this.getInventory().tickItems();
        tickDaylight();

        super.tickMovement();
    }

    private void tickRegeneration() {
        if (this.world.getDifficulty() == Difficulty.PEACEFUL && this.world.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION)) {
            if (this.getHealth() < this.getMaximumHealth() && this.age % 20 == 0) {
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
    public boolean interactMob(PlayerEntity player, Hand hand) {
        if (!player.world.isClient() && !player.isSneaking()) {
            ContainerProviderRegistry.INSTANCE.openContainer(OverlordEntities.OWNED_SKELETON_ID, player, buf -> buf.writeUuid(this.getUuid()));
        }
        return !player.isSneaking();
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
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(CHARGING, false);
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
    public IllagerEntity.State getState() {
        if (this.isCharging()) {
            return IllagerEntity.State.CROSSBOW_CHARGE;
        } else if (this.isHolding(Items.CROSSBOW)) {
            return IllagerEntity.State.CROSSBOW_HOLD;
        } else {
            return this.isAttacking() ? IllagerEntity.State.ATTACKING : IllagerEntity.State.NEUTRAL;
        }
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
        List<Integer> slots = inventorySearcher.findSlotsMatching(inventory, EnchantmentHelper::hasVanishingCurse);
        for (int slot : slots) {
            this.inventory.removeInvStack(slot);
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
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        ListTag inventoryTag = tag.getList("Inventory", 10);
        this.inventory.deserialize(inventoryTag);
        this.owner = tag.getUuid("Owner");
        this.hasMuscles = tag.getBoolean("Muscles");
        this.hasSkin = tag.getBoolean("Skin");
        this.skinsuit = tag.getUuid("Skinsuit");
        this.setGrowthPhase(SkeletonGrowthPhase.values()[tag.getInt("GrowthPhase")]);
        this.updateAISettings(tag.getCompound("aiSettings"));
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        tag.put("Inventory", this.inventory.serialize(new ListTag()));
        tag.putUuid("Owner", this.owner);
        tag.putBoolean("Muscles", this.hasMuscles);
        tag.putBoolean("Skin", this.hasSkin);
        tag.putUuid("Skinsuit", this.getSkinsuit());
        tag.put("aiSettings", aiSettings.toTag());
        tag.putInt("GrowthPhase", growthPhase.ordinal());
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
        if (source.isScaledWithDifficulty()) {
            if (this.world.getDifficulty() == Difficulty.PEACEFUL) {
                amount = 0.0F;
            } else if (this.world.getDifficulty() == Difficulty.EASY) {
                amount = Math.min(amount / 2.0F + 1.0F, amount);
            } else if (this.world.getDifficulty() == Difficulty.HARD) {
                amount = amount * 3.0F / 2.0F;
            }
        }

        return amount != 0.0F && super.damage(source, amount);
    }

    @Override
    protected void takeShieldHit(LivingEntity attacker) {
        super.takeShieldHit(attacker);
        if (attacker.getMainHandStack().getItem() instanceof AxeItem) {
            this.disableShield(true);
        }
    }

    @Override
    protected void damageArmor(float amount) {
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
        return !this.world.getBlockState(pos).canSuffocate(this.world, pos);
    }

    @Override
    public float getMovementSpeed() {
        return (float) this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue();
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
            this.inventory.setInvStack(slot, item);
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

        this.inventory.setInvStack((equipmentSlot != null ? equipmentSlot.getEntitySlotId() : 0) + this.inventory.main.size(), item);
        return true;
    }

    public float getAttackCooldownProgressPerTick() {
        return (float) (1.0D / this.getAttributeInstance(EntityAttributes.ATTACK_SPEED).getValue() * 20.0D);
    }

    public float getAttackCooldownProgress(float baseTime) {
        return MathHelper.clamp(((float) this.lastAttackedTicks + baseTime) / this.getAttackCooldownProgressPerTick(), 0.0F, 1.0F);
    }

    public void resetLastAttackedTicks() {
        this.lastAttackedTicks = 0;
    }

    public ItemCooldownManager getItemCooldownManager() {
        return this.itemCooldownManager;
    }

    @Override
    public boolean canPickUp(ItemStack stack) {
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
        growthPhase = newPhase;
        calculateDimensions();
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return super.getDimensions(pose).scaled(1 - (0.1f * (4 - growthPhase.ordinal())));
    }

    public SkeletonGrowthPhase getGrowthPhase() {
        return growthPhase;
    }

    public void setSkinsuit(UUID playerId) {
        this.skinsuit = playerId;
        if (!world.isClient()) {
            //TODO find a way to set left-handedness based on skinsuit
        }
    }

    public UUID getSkinsuit() {
        return skinsuit == null ? DIContainer.get().getInstance(EmptyUUID.class).get() : skinsuit;
    }

    public boolean hasSkin() {
        return hasSkin;
    }

    public void setHasSkin(boolean hasSkin) {
        this.hasSkin = hasSkin;
    }

    public boolean hasMuscles() {
        return hasMuscles;
    }

    public void setHasMuscles(boolean hasMuscles) {
        this.hasMuscles = hasMuscles;
    }

    @Override
    public UUID getOwnerId() {
        return owner;
    }

    @Nullable
    @Override
    public Entity getOwner() {
        if (this.world == null || !(world instanceof ServerWorld)) {
            return null;
        }

        return ((ServerWorld) this.world).getEntity(this.getOwnerId());
    }

    public float getBlockBreakingSpeed(BlockState block) {
        float breakSpeed = this.inventory.getBlockBreakingSpeed(block);

        return breakSpeedModifiers.applyApplicable(this, breakSpeed);
    }

    public boolean isUsingEffectiveTool(BlockState block) {
        return block.getMaterial().canBreakByHand() || this.inventory.isUsingEffectiveTool(block);
    }

    public OwnedSkeletonContainer getContainer(PlayerInventory playerInv, int syncId) {
        return new OwnedSkeletonContainer(playerInv, !world.isClient, this, syncId);
    }

    @Override
    public SkeletonInventory getInventory() {
        return inventory;
    }

    public void setOwner(UUID newOwner) {
        this.owner = newOwner;
    }

    @Override
    public void shoot(LivingEntity target, ItemStack crossbow, Projectile projectile, float multiShotSpray) {
        shootCrossbow(target, projectile, multiShotSpray);
    }

    private void shootCrossbow(LivingEntity target, Projectile projectile, float multiShotSpray) {
        if (!(projectile instanceof Entity)) {
            Overlord.getLogger().warn("Projectile is not an entity! {}", projectile.getClass());
            return;
        }
        Entity entity = (Entity) projectile;
        double d = target.getX() - this.getX();
        double e = target.getZ() - this.getZ();
        double f = MathHelper.sqrt(d * d + e * e);
        double g = target.getBodyY(1.0 / 3.0) - entity.getY() + f * 0.2;
        Vector3f vector3f = this.getProjectileVelocity(new Vec3d(d, g, e), multiShotSpray);
        projectile.setVelocity(vector3f.getX(), vector3f.getY(), vector3f.getZ(), 1.6F, (float) (14 - this.world.getDifficulty().getId() * 4));
        this.playSound(SoundEvents.ITEM_CROSSBOW_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
    }

    private Vector3f getProjectileVelocity(Vec3d vec3d, float multiShotSpray) {
        Vec3d vec3d2 = vec3d.normalize();
        Vec3d vec3d3 = vec3d2.crossProduct(new Vec3d(0.0D, 1.0D, 0.0D));
        if (vec3d3.lengthSquared() <= 1.0E-7D) {
            vec3d3 = vec3d2.crossProduct(this.getOppositeRotationVector(1.0F));
        }

        Quaternion quaternion = new Quaternion(new Vector3f(vec3d3), 90.0F, true);
        Vector3f vector3f = new Vector3f(vec3d2);
        vector3f.rotate(quaternion);
        Quaternion quaternion2 = new Quaternion(vector3f, multiShotSpray, true);
        Vector3f vector3f2 = new Vector3f(vec3d2);
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
        ProjectileEntity projectileEntity = this.createArrowProjectile(arrowStack, f);
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
    }

    protected ProjectileEntity createArrowProjectile(ItemStack arrow, float f) {
        return ProjectileUtil.createArrowProjectile(this, arrow, f);
    }
}
