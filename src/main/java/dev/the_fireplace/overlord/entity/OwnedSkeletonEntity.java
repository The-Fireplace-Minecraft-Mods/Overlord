package dev.the_fireplace.overlord.entity;

import com.google.common.collect.Lists;
import dev.the_fireplace.lib.api.util.EmptyUUID;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.api.inventory.InventorySearcher;
import dev.the_fireplace.overlord.api.mechanic.AIControllable;
import dev.the_fireplace.overlord.api.mechanic.Ownable;
import dev.the_fireplace.overlord.api.world.BreakSpeedModifiers;
import dev.the_fireplace.overlord.api.world.DaylightDetector;
import dev.the_fireplace.overlord.api.world.MeleeAttackExecutor;
import dev.the_fireplace.overlord.api.world.UndeadDaylightDamager;
import dev.the_fireplace.overlord.init.OverlordEntities;
import dev.the_fireplace.overlord.model.aiconfig.AISettings;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.SharedConstants;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class OwnedSkeletonEntity extends LivingEntity implements Ownable, AIControllable {

    private UUID owner = new UUID(801295133947085751L, -7395604847578632613L);
    private UUID skinsuit = EmptyUUID.EMPTY_UUID;
    private byte growthPhase = 0;
    private boolean hasSkin = false;
    private boolean hasMuscles = false;

    private final AISettings aiSettings = new AISettings();
    private final SkeletonInventory inventory = new SkeletonInventory(this);
    private final ItemCooldownManager itemCooldownManager = new ItemCooldownManager();
    private boolean lefty;

    private final InventorySearcher inventorySearcher = InventorySearcher.getInstance();

    /**
     * @deprecated Only public because Minecraft requires it to be. Use the factory.
     * Intended for internal use, but it technically works. Use {@link OwnedSkeletonEntity#create(World, UUID)} when possible.
     */
    @Deprecated
    public OwnedSkeletonEntity(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
        lefty = world.random.nextBoolean();
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
        if (this.world.getDifficulty() == Difficulty.PEACEFUL && this.world.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION)) {
            if (this.getHealth() < this.getMaximumHealth() && this.age % 20 == 0) {
                this.heal(1.0F);
            }
        }

        inventory.tickItems();
        if (!hasSkin() && DaylightDetector.getInstance().isInDaylight(this)) {
            UndeadDaylightDamager.getInstance().applyDamage(this);
        }

        super.tickMovement();
    }

    @Override
    public boolean interact(PlayerEntity player, Hand hand) {
        if (!player.world.isClient()) {
            ContainerProviderRegistry.INSTANCE.openContainer(OverlordEntities.OWNED_SKELETON_ID, player, buf -> buf.writeUuid(this.getUuid()));
        }
        return true;
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        this.refreshPosition();
        //this.drop(source);

        if (source != null) {
            this.setVelocity(
                -MathHelper.cos((this.knockbackVelocity + this.yaw) * (float) Math.PI/180) * 0.1f,
                0.1f,
                -MathHelper.sin((this.knockbackVelocity + this.yaw) * (float) Math.PI/180) * 0.1f
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
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(getStepSound(), 0.15F, 1.0F);
    }

    private SoundEvent getStepSound() {
        return hasPaddedFeet() ? SoundEvents.ENTITY_ZOMBIE_STEP : SoundEvents.ENTITY_SKELETON_STEP;
    }

    private boolean hasPaddedFeet() {
        return getGrowthPhase() == 4 && hasMuscles() || hasSkin();
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
        for (int slot: slots) {
            this.inventory.removeInvStack(slot);
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        if (hasPlayerlikeBody()) {
            if (source == DamageSource.ON_FIRE) {
                return SoundEvents.ENTITY_PLAYER_HURT_ON_FIRE;
            } else if (source == DamageSource.DROWN) {
                return SoundEvents.ENTITY_PLAYER_HURT_DROWN;
            } else if (source == DamageSource.SWEET_BERRY_BUSH) {
                return SoundEvents.ENTITY_PLAYER_HURT_SWEET_BERRY_BUSH;
            } else {
                return SoundEvents.ENTITY_PLAYER_HURT;
            }
        } else {
            return SoundEvents.ENTITY_SKELETON_HURT;
        }
    }

    @Override
    protected SoundEvent getDeathSound() {
        return hasPlayerlikeBody() ? SoundEvents.ENTITY_PLAYER_DEATH : SoundEvents.ENTITY_SKELETON_DEATH;
    }

    private boolean hasPlayerlikeBody() {
        return hasMuscles() && getGrowthPhase() >= 3;
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        ListTag listTag = tag.getList("Inventory", 10);
        this.inventory.deserialize(listTag);
        this.owner = tag.getUuid("Owner");
        this.lefty = tag.getBoolean("Lefty");
        this.hasMuscles = tag.getBoolean("Muscles");
        this.hasSkin = tag.getBoolean("Skin");
        if (tag.contains("Skinsuit")) {
            this.skinsuit = tag.getUuid("Skinsuit");
        }
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        tag.put("Inventory", this.inventory.serialize(new ListTag()));
        tag.putUuid("Owner", this.owner);
        tag.putBoolean("Lefty", this.lefty);
        tag.putBoolean("Muscles", this.hasMuscles);
        tag.putBoolean("Skin", this.hasSkin);
        if (skinsuit != null) {
            tag.putUuid("Skinsuit", this.skinsuit);
        }
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
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            if (this.getHealth() <= 0.0F) {
                return false;
            } else {
                if (source.isScaledWithDifficulty()) {
                    if (this.world.getDifficulty() == Difficulty.PEACEFUL) {
                        amount = 0.0F;
                    }

                    if (this.world.getDifficulty() == Difficulty.EASY) {
                        amount = Math.min(amount / 2.0F + 1.0F, amount);
                    }

                    if (this.world.getDifficulty() == Difficulty.HARD) {
                        amount = amount * 3.0F / 2.0F;
                    }
                }

                return amount != 0.0F && super.damage(source, amount);
            }
        }
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
        if (amount >= 3.0F && this.activeItemStack.getItem() == Items.SHIELD) {
            int i = 1 + MathHelper.floor(amount);
            Hand hand = this.getActiveHand();
            this.activeItemStack.damage(i, this, (playerEntity) -> {
                //TODO make sure this works
                this.sendEquipmentBreakStatus(hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            });
            if (this.activeItemStack.isEmpty()) {
                if (hand == Hand.MAIN_HAND)
                    this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                else
                    this.equipStack(EquipmentSlot.OFFHAND, ItemStack.EMPTY);

                this.activeItemStack = ItemStack.EMPTY;
                this.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8F, 0.8F + this.world.random.nextFloat() * 0.4F);
            }
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
        MeleeAttackExecutor.getInstance().attack(this, target, getAttackCooldownProgress(0.5F));
        resetLastAttackedTicks();
    }

    public void disableShield(boolean sprinting) {
        float f = 0.25F + (float)EnchantmentHelper.getEfficiency(this) * 0.05F;
        if (sprinting) {
            f += 0.75F;
        }

        if (this.random.nextFloat() < f) {
            this.getItemCooldownManager().set(Items.SHIELD, 100);
            this.clearActiveItem();
            this.world.sendEntityStatus(this, (byte)30);
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
        return (float)this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue();
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

    public boolean giveItemStack(ItemStack stack) {
        this.onEquipStack(stack);
        return this.inventory.insertStack(stack);
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
        } else {
            EquipmentSlot equipmentSlot;
            if (slot == 100 + EquipmentSlot.HEAD.getEntitySlotId())
                equipmentSlot = EquipmentSlot.HEAD;
            else if (slot == 100 + EquipmentSlot.CHEST.getEntitySlotId())
                equipmentSlot = EquipmentSlot.CHEST;
            else if (slot == 100 + EquipmentSlot.LEGS.getEntitySlotId())
                equipmentSlot = EquipmentSlot.LEGS;
            else if (slot == 100 + EquipmentSlot.FEET.getEntitySlotId())
                equipmentSlot = EquipmentSlot.FEET;
            else
                equipmentSlot = null;

            if (slot == 98) {
                this.equipStack(EquipmentSlot.MAINHAND, item);
                return true;
            } else if (slot == 99) {
                this.equipStack(EquipmentSlot.OFFHAND, item);
                return true;
            } else {
                if (!item.isEmpty()) {
                    if (!(item.getItem() instanceof ArmorItem) && !(item.getItem() instanceof ElytraItem)) {
                        if (equipmentSlot != EquipmentSlot.HEAD)
                            return false;
                    } else if (MobEntity.getPreferredEquipmentSlot(item) != equipmentSlot)
                        return false;
                }

                this.inventory.setInvStack((equipmentSlot != null ? equipmentSlot.getEntitySlotId() : 0) + this.inventory.main.size(), item);
                return true;
            }
        }
    }

    @Override
    public Arm getMainArm() {
        return lefty ? Arm.LEFT : Arm.RIGHT;
    }

    public float getAttackCooldownProgressPerTick() {
        return (float)(1.0D / this.getAttributeInstance(EntityAttributes.ATTACK_SPEED).getValue() * 20.0D);
    }

    public float getAttackCooldownProgress(float baseTime) {
        return MathHelper.clamp(((float)this.lastAttackedTicks + baseTime) / this.getAttackCooldownProgressPerTick(), 0.0F, 1.0F);
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
        if (!(rangedWeaponStack.getItem() instanceof RangedWeaponItem)) {
            return ItemStack.EMPTY;
        } else {
            Predicate<ItemStack> predicate = ((RangedWeaponItem)rangedWeaponStack.getItem()).getHeldProjectiles();
            ItemStack projectileStack = RangedWeaponItem.getHeldProjectile(this, predicate);
            if (!projectileStack.isEmpty()) {
                return projectileStack;
            } else {
                predicate = ((RangedWeaponItem)rangedWeaponStack.getItem()).getProjectiles();

                for (int i = 0; i < this.inventory.getInvSize(); ++i) {
                    ItemStack itemStack3 = this.inventory.getInvStack(i);
                    if (predicate.test(itemStack3)) {
                        return itemStack3;
                    }
                }

                return ItemStack.EMPTY;
            }
        }
    }

    public void setGrowthPhase(byte newPhase) {
        if(newPhase < 0) {
            newPhase = 0;
            Overlord.errorWithStacktrace("Attempt was made to set grown phase < 0!");
        } else if(newPhase > 4) {
            newPhase = 4;
            Overlord.errorWithStacktrace("Attempt was made to set grown phase > 4!");
        }
        growthPhase = newPhase;
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return super.getDimensions(pose).scaled(1-(0.1f*(4-growthPhase)));
    }

    public byte getGrowthPhase() {
        return growthPhase;
    }

    public void setSkinsuit(UUID playerId) {
        this.skinsuit = playerId;
    }

    public UUID getSkinsuit() {
        return skinsuit;
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

    public boolean isMeleeAttacking() {
        //TODO
        return false;
    }

    @Override
    public UUID getOwnerId() {
        return owner;
    }

    public float getBlockBreakingSpeed(BlockState block) {
        float breakSpeed = this.inventory.getBlockBreakingSpeed(block);

        return BreakSpeedModifiers.getInstance().applyApplicable(this, breakSpeed);
    }

    public boolean isUsingEffectiveTool(BlockState block) {
        return block.getMaterial().canBreakByHand() || this.inventory.isUsingEffectiveTool(block);
    }

    public OwnedSkeletonContainer getContainer(PlayerInventory playerInv, int syncId) {
        return new OwnedSkeletonContainer(playerInv, !world.isClient, this, syncId);
    }

    public SkeletonInventory getInventory() {
        return inventory;
    }

    public void setOwner(UUID newOwner) {
        this.owner = newOwner;
    }

    public AISettings getAiSettings() {
        return aiSettings;
    }

    @Override
    public AISettings getSettings() {
        return aiSettings;
    }
}
