package the_fireplace.overlord.entity;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import the_fireplace.overlord.config.ConfigValues;
import the_fireplace.overlord.entity.ai.*;
import the_fireplace.overlord.registry.AugmentRegistry;
import the_fireplace.overlord.tools.Alliances;
import the_fireplace.overlord.tools.Augment;
import the_fireplace.overlord.tools.CustomDataSerializers;
import the_fireplace.overlord.tools.Enemies;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

/**
 * The base Army Member class. All entities that you want to be part of the army should extend this.
 *
 * @author The_Fireplace
 */
public abstract class EntityArmyMember extends EntityCreature implements IEntityOwnable, INpc {

	protected static final DataParameter<UUID> OWNER_UNIQUE_ID = EntityDataManager.createKey(EntityArmyMember.class, CustomDataSerializers.UNIQUE_ID);
	protected static final DataParameter<String> SQUAD = EntityDataManager.createKey(EntityArmyMember.class, DataSerializers.STRING);
	/**
	 * The attack mode. 0 is passive, 1 is defensive, 2 is aggressive
	 */
	protected static final DataParameter<Byte> ATTACK_MODE = EntityDataManager.createKey(EntityArmyMember.class, DataSerializers.BYTE);
	/**
	 * The movement mode. 0 is stationed, 1 is follower, 2 is base
	 */
	protected static final DataParameter<Byte> MOVEMENT_MODE = EntityDataManager.createKey(EntityArmyMember.class, DataSerializers.BYTE);
	private static final DataParameter<Boolean> SWINGING_ARMS = EntityDataManager.createKey(EntityArmyMember.class, DataSerializers.BOOLEAN);

	protected EntityAIAttackMelee aiAttackOnCollide = null;

	private ItemStack augmentDisplayStack = ItemStack.EMPTY;
	private Augment clientAugment = null;
	public boolean cachedClientAugment = false;

	public EntityArmyMember(World world, @Nullable UUID owner) {
		super(world);
		if (owner != null)
			this.setOwnerId(owner);
		else
			this.setOwnerId(UUID.fromString("0b1ec5ad-cb2a-43b7-995d-889320eb2e5b"));

		((PathNavigateGround) this.getNavigator()).setBreakDoors(true);

		enablePersistence();
	}

	protected boolean isUpdatingAI = false;

	@Override
	protected void initEntityAI() {
		if (!isUpdatingAI) {
			isUpdatingAI = true;
			this.tasks.taskEntries.clear();//Clear first so this can be called when the AI Modes change
			this.getNavigator().clearPath();
			this.tasks.addTask(1, new EntityAISwimming(this));
			this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
			this.tasks.addTask(8, new EntityAILookIdle(this));
			addMovementTasks();
			if (this.dataManager.get(ATTACK_MODE) != 0)
				addAttackTasks();
			addTargetTasks();
			this.playSound(getAmbientSound(), getSoundVolume() * 2.0F, getSoundPitch() * 1.5F);
			isUpdatingAI = false;
		}
	}

	/**
	 * Register your movement tasks here.
	 */
	public void addMovementTasks() {
		switch (dataManager.get(MOVEMENT_MODE)) {
			case 1:
				if (!ConfigValues.HUNTCREEPERS)
					this.tasks.addTask(3, new EntityAIAvoidEntity(this, EntityCreeper.class, 10.0F, 1.2D, 1.6D));
				this.tasks.addTask(4, new EntityAIOpenDoor(this, getAttackMode() != 2));
				this.tasks.addTask(6, new EntityAIFollowMaster(this, 1.0D, 10.0F, 2.0F));
			case 0:
				this.detachHome();
				break;
			case 2:
			default:
				this.setHomePosAndDistance(new BlockPos(this.posX, this.posY, this.posZ), 20);
				this.tasks.addTask(2, new EntityAIRestrictSun(this));
				this.tasks.addTask(3, new EntityAIFleeSun(this, 1.0D));
				if (!ConfigValues.HUNTCREEPERS)
					this.tasks.addTask(3, new EntityAIAvoidEntity(this, EntityCreeper.class, 10.0F, 1.2D, 1.6D));
				this.tasks.addTask(4, new EntityAIOpenDoor(this, getAttackMode() != 2));
				this.tasks.addTask(7, new EntityAIWanderBase(this, 1.0D));
		}
	}

	protected int raiseArmTicks;

	/**
	 * Register attack tasks here
	 */
	public void addAttackTasks() {
		if (aiAttackOnCollide == null) {
			aiAttackOnCollide = new EntityAIAttackMelee(this, 1.2D, true) {
				@Override
				public void resetTask() {
					super.resetTask();
					EntityArmyMember.this.setSwingingArms(false);
				}

				@Override
				public void startExecuting() {
					super.startExecuting();
					raiseArmTicks = 0;
				}

				@Override
				public void updateTask() {
					if (shouldContinueExecuting()) {
						++raiseArmTicks;

						if (raiseArmTicks >= 5 && this.attackTick < 10) {
							EntityArmyMember.this.setSwingingArms(true);
						} else {
							EntityArmyMember.this.setSwingingArms(false);
						}
						if(getAttackTarget() != null)
							super.updateTask();
					}
				}
			};
		}
		if (this.dataManager.get(MOVEMENT_MODE) > 0)
			this.tasks.addTask(5, aiAttackOnCollide);
	}

	@SuppressWarnings("unchecked")
	/*
	  Register targeting tasks here
     */
	public void addTargetTasks() {
		switch (dataManager.get(ATTACK_MODE)) {
			case 2:
				this.targetTasks.addTask(2, new EntityAIMasterHurtTarget(this));
			case 1:
				this.targetTasks.addTask(1, new EntityAIMasterHurtByTarget(this));
				this.targetTasks.addTask(1, new EntityAIHurtByNonAllied(this, true));
				this.targetTasks.addTask(2, new EntityAINearestNonTeamTarget(this, EntityPlayer.class, true));
				this.targetTasks.addTask(2, new EntityAINearestNonTeamTarget(this, EntityArmyMember.class, true));
				this.targetTasks.addTask(3, new EntityAINearestNonTeamTarget(this, IMob.class, true));
				break;
			case 0:
			default:
				this.setAttackTarget(null);
				this.setRevengeTarget(null);
				break;
		}
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
	}

	@Override
	public void onLivingUpdate() {
		updateArmSwingProgress();
		Augment aug = getAugment();
		if (aug != null)
			aug.onEntityTick(this);
		super.onLivingUpdate();
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(OWNER_UNIQUE_ID, UUID.fromString("0b1ec5ad-cb2a-43b7-995d-889320eb2e5b"));
		this.dataManager.register(SQUAD, String.valueOf(""));
		this.dataManager.register(SWINGING_ARMS, Boolean.valueOf(false));
		this.dataManager.register(ATTACK_MODE, Byte.valueOf((byte) 1));
		this.dataManager.register(MOVEMENT_MODE, Byte.valueOf((byte) 1));
	}

	@Override
	public void onDeath(@Nonnull DamageSource cause) {
		super.onDeath(cause);
		EntityLivingBase owner = getOwner();
		if (!this.world.isRemote && this.world.getGameRules().getBoolean("showDeathMessages") && owner instanceof EntityPlayerMP)
			owner.sendMessage(cause.getDeathMessage(this));
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_SKELETON_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.ENTITY_SKELETON_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_SKELETON_DEATH;
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		if (compound.hasKey("AttackMode")) {
			byte b = compound.getByte("AttackMode");
			this.dataManager.set(ATTACK_MODE, b);
		}
		if (compound.hasKey("MovementMode")) {
			byte b = compound.getByte("MovementMode");
			this.dataManager.set(MOVEMENT_MODE, b);
		}
		if (compound.hasKey("Squad")) {
			String s = compound.getString("Squad");
			setSquad(s);
		}
		String s;
		if (compound.hasKey("OwnerUUID", 8)) {
			s = compound.getString("OwnerUUID");
		} else {
			String s1 = compound.getString("Owner");
			s = PreYggdrasilConverter.convertMobOwnerIfNeeded(this.getServer(), s1);
		}
		if (!s.isEmpty()) {
			try {
				this.setOwnerId(UUID.fromString(s));
			} catch (Throwable var4) {
				var4.printStackTrace();
			}
		}
		initEntityAI();
	}

	@Override
	public boolean canBeLeashedTo(EntityPlayer player) {
		return this.isOwner(player);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setBoolean("IsMinimapHostile", this.dataManager.get(ATTACK_MODE) == 2);
		compound.setByte("AttackMode", this.dataManager.get(ATTACK_MODE));
		compound.setByte("MovementMode", this.dataManager.get(MOVEMENT_MODE));
		compound.setString("Squad", getSquad());
		UUID ownerId = this.getOwnerId();
		if (ownerId == null) {
			compound.setString("OwnerUUID", "0b1ec5ad-cb2a-43b7-995d-889320eb2e5b");
		} else {
			compound.setString("OwnerUUID", ownerId.toString());
		}
	}

	@SuppressWarnings("UnnecessaryUnboxing")
	@SideOnly(Side.CLIENT)
	public boolean isSwingingArms() {
		return this.dataManager.get(SWINGING_ARMS).booleanValue();
	}

	@SuppressWarnings("UnnecessaryBoxing")
	public void setSwingingArms(boolean swingingArms) {
		this.dataManager.set(SWINGING_ARMS, Boolean.valueOf(swingingArms));
	}

	public void cycleAttackMode() {
		byte b = getAttackMode();
		//noinspection Duplicates
		if (b < 2) {
			byte b1 = ++b;
			dataManager.set(ATTACK_MODE, b1);
			initEntityAI();
		} else {
			dataManager.set(ATTACK_MODE, (byte) 0);
			initEntityAI();
		}
	}

	public void setAttackMode(byte b) {
		dataManager.set(ATTACK_MODE, b);
		initEntityAI();
	}

	/**
	 * Gets the attack mode of the skeleton
	 *
	 * @return 0 for passive, 1 for defensive, 2 for aggressive
	 */
	public byte getAttackMode() {
		return dataManager.get(ATTACK_MODE);
	}

	public void cycleMovementMode() {
		byte b = getMovementMode();
		//noinspection Duplicates
		if (b < 2) {
			byte b1 = ++b;
			dataManager.set(MOVEMENT_MODE, b1);
			initEntityAI();
		} else {
			dataManager.set(MOVEMENT_MODE, (byte) 0);
			initEntityAI();
		}
	}

	public void setMovementMode(byte b) {
		dataManager.set(MOVEMENT_MODE, b);
		initEntityAI();
	}

	/**
	 * Gets the movement mode of the skeleton
	 *
	 * @return 0 for stationed, 1 for follower, 2 for base
	 */
	public byte getMovementMode() {
		return dataManager.get(MOVEMENT_MODE);
	}

	@Override
	public UUID getOwnerId() {
		return this.dataManager.get(OWNER_UNIQUE_ID);
	}

	public void setOwnerId(@Nonnull UUID ownerId) {
		this.dataManager.set(OWNER_UNIQUE_ID, ownerId);
	}

	@Nullable
	@Override
	public EntityLivingBase getOwner() {
		try {
			return this.world.getPlayerEntityByUUID(getOwnerId());
		} catch (IllegalArgumentException var2) {
			return null;
		}
	}

	public boolean isOwner(EntityLivingBase entityIn) {
		return entityIn == this.getOwner();
	}

	@Override
	public Team getTeam() {
		EntityLivingBase entitylivingbase = this.getOwner();

		if (entitylivingbase != null)
			return entitylivingbase.getTeam();

		return super.getTeam();
	}

	@Override
	public boolean isOnSameTeam(Entity entityIn) {
		EntityLivingBase owner = this.getOwner();

		if (entityIn == owner)
			return true;

		if (entityIn instanceof EntityArmyMember)
			return ((EntityArmyMember) entityIn).getOwnerId() == this.getOwnerId();

		if (owner != null)
			return owner.isOnSameTeam(entityIn);

		return super.isOnSameTeam(entityIn);
	}

	public String getSquad() {
		return this.dataManager.get(SQUAD);
	}

	public void setSquad(String s) {
		this.dataManager.set(SQUAD, String.valueOf(s));
	}

	public Augment getAugment() {
		return null;
	}

	@SideOnly(Side.CLIENT)
	public void setClientAugment(@Nullable String augment) {
		clientAugment = AugmentRegistry.getAugment(augment);
		if (!cachedClientAugment)
			cachedClientAugment = true;
	}

	@SideOnly(Side.CLIENT)
	@Nullable
	public Augment getClientAugment() {
		return clientAugment;
	}

	@Nonnull
	public ItemStack getAugmentStack() {
		return ItemStack.EMPTY;
	}

	@SideOnly(Side.CLIENT)
	@Nonnull
	public ItemStack getAugmentDisplayStack() {
		return augmentDisplayStack;
	}

	@SideOnly(Side.CLIENT)
	public void setAugmentDisplayStack(@Nonnull ItemStack stack) {
		augmentDisplayStack = stack;
		if (!cachedClientAugment)
			cachedClientAugment = true;
	}

	@Override
	@Nonnull
	public SoundCategory getSoundCategory() {
		return SoundCategory.NEUTRAL;
	}

	@Override
	@Nonnull
	protected SoundEvent getSwimSound() {
		return SoundEvents.ENTITY_HOSTILE_SWIM;
	}

	@Override
	@Nonnull
	protected SoundEvent getSplashSound() {
		return SoundEvents.ENTITY_HOSTILE_SPLASH;
	}

	@Override
	@Nonnull
	protected SoundEvent getFallSound(int heightIn) {
		return heightIn > 4 ? SoundEvents.ENTITY_HOSTILE_BIG_FALL : SoundEvents.ENTITY_HOSTILE_SMALL_FALL;
	}

	@Override
	public boolean attackEntityAsMob(@Nonnull Entity entityIn) {
		ItemStack mainHandItem = this.getHeldItemMainhand();
		if (mainHandItem.isEmpty()) {
			mainHandItem = ItemStack.EMPTY;
			setHeldItem(EnumHand.MAIN_HAND, mainHandItem);
		}
		float attackDamage = (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
		int knockback = 0;

		if (entityIn instanceof EntityLivingBase) {
			attackDamage += EnchantmentHelper.getModifierForCreature(mainHandItem, ((EntityLivingBase) entityIn).getCreatureAttribute());
			knockback += EnchantmentHelper.getKnockbackModifier(this);
		}

		boolean successfulAttack = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), attackDamage);

		if (successfulAttack) {
			if (knockback > 0) {
				((EntityLivingBase) entityIn).knockBack(this, (float) knockback * 0.5F, (double) MathHelper.sin(this.rotationYaw * 0.017453292F), (double) (-MathHelper.cos(this.rotationYaw * 0.017453292F)));
				this.motionX *= 0.6D;
				this.motionZ *= 0.6D;
			}

			int j = EnchantmentHelper.getFireAspectModifier(this);

			if (j > 0) {
				entityIn.setFire(j * 4);
			}

			if (entityIn instanceof EntityPlayer) {
				EntityPlayer entityplayer = (EntityPlayer) entityIn;
				ItemStack playerItemBeingUsed = entityplayer.isHandActive() ? entityplayer.getActiveItemStack() : ItemStack.EMPTY;

				if (!mainHandItem.isEmpty() && !playerItemBeingUsed.isEmpty() && mainHandItem.getItem() instanceof ItemAxe && playerItemBeingUsed.getItem() == Items.SHIELD) {
					float f1 = 0.25F + (float) EnchantmentHelper.getEfficiencyModifier(this) * 0.05F;

					if (this.rand.nextFloat() < f1) {
						entityplayer.getCooldownTracker().setCooldown(Items.SHIELD, 100);
						this.world.setEntityState(entityplayer, (byte) 30);
					}
				}
			}

			this.applyEnchantments(this, entityIn);

			if (!mainHandItem.isEmpty() && entityIn instanceof EntityLivingBase)
				mainHandItem.getItem().hitEntity(mainHandItem, (EntityLivingBase) entityIn, this);

			if (this.getAugment() != null)
				this.getAugment().onStrike(this, entityIn);
		}
		if (mainHandItem.isEmpty())
			setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);

		return successfulAttack;
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	public boolean shouldAttackEntity(EntityLivingBase target){
		EntityLivingBase owner = getOwner();
		if (ConfigValues.HUNTCREEPERS || !(target instanceof EntityCreeper)) {
			if (target instanceof EntityWolf) {
				EntityWolf entitywolf = (EntityWolf) target;

				if (entitywolf.isTamed() && entitywolf.getOwner() == owner || Alliances.getInstance().isAlliedTo(entitywolf.getOwnerId(), getOwnerId()))
					return false;
			}

			if (target instanceof EntityArmyMember)
				if ((getAttackMode() < 2 && Enemies.getInstance().isNotEnemiesWith(((EntityArmyMember) target).getOwnerId(), getOwnerId())) || Objects.equals(((EntityArmyMember) target).getOwnerId(), getOwnerId()) || Alliances.getInstance().isAlliedTo(((EntityArmyMember) target).getOwnerId(), getOwnerId()))
					return false;

			return !(target instanceof EntityPlayer && owner instanceof EntityPlayer && !((EntityPlayer) owner).canAttackPlayer((EntityPlayer) target)) && (!(target instanceof EntityHorse) || !((EntityHorse) target).isTame());
		} else
			return false;
	}

	public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {

	}

	public boolean willBeAttackedBy(EntityLiving mob) {
		return false;
	}

	//Attempt to fix losing ai when switching weapons
	@Override
	public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn) {
		boolean ret = super.replaceItemInInventory(inventorySlot, itemStackIn);
		initEntityAI();
		return ret;
	}
}
