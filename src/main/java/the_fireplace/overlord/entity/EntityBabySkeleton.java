package the_fireplace.overlord.entity;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.advancements.CriterionRegistry;
import the_fireplace.overlord.config.ConfigValues;
import the_fireplace.overlord.tools.ISkinsuitWearer;
import the_fireplace.overlord.tools.SkinType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.UUID;

/**
 * @author The_Fireplace
 */
public class EntityBabySkeleton extends EntityArmyMember implements ISkinsuitWearer {

	private static final DataParameter<String> SKINSUIT_NAME = EntityDataManager.createKey(EntityBabySkeleton.class, DataSerializers.STRING);
	private static final DataParameter<Byte> SKINSUIT_TYPE = EntityDataManager.createKey(EntityBabySkeleton.class, DataSerializers.BYTE);

	public final InventoryBasic equipInventory;

	public EntityBabySkeleton(World world) {
		this(world, null);
	}

	public EntityBabySkeleton(World world, @Nullable UUID owner) {
		super(world, owner);
		this.equipInventory = new InventoryBasic("Equipment", false, 5) {
			@Override
			public boolean isItemValidForSlot(int index, ItemStack stack) {
				return index >= 4 || !stack.isEmpty() && stack.getItem().isValidArmor(stack, EntityEquipmentSlot.values()[index], null);
			}
		};
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.255D);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(25.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.5D);
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(0.5D);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		if (this.getOwner() != null) {
			if (this.getOwner().equals(player)) {
				if (!player.isSneaking()) {
					FMLNetworkHandler.openGui(player, Overlord.instance, hashCode(), world, (int) this.posX, (int) this.posY, (int) this.posZ);
					return true;
				} else {
					if (!world.isRemote) {
						ItemStack stack = player.getHeldItem(hand);
						if (!stack.isEmpty()) {
							if (stack.getItem() == Overlord.baby_spawner) {
								NBTTagCompound compound = new NBTTagCompound();
								this.writeEntityToNBT(compound);
								stack.setTagCompound(compound);
							}
						}
					}
				}
			}
		}
		return super.processInteract(player, hand);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(SKINSUIT_TYPE, Byte.valueOf((byte) SkinType.NONE.ordinal()));
		this.dataManager.register(SKINSUIT_NAME, String.valueOf(""));
	}

	@Override
	protected void playStepSound(BlockPos pos, Block blockIn) {
		SoundEvent soundevent = SoundEvents.ENTITY_SKELETON_STEP;
		this.playSound(soundevent, 0.15F, 1.0F);
	}

	@Override
	@Nonnull
	public EnumCreatureAttribute getCreatureAttribute() {
		return EnumCreatureAttribute.UNDEAD;
	}

	boolean sally = false;
	boolean armed = false;

	@Override
	public void onLivingUpdate() {
		if (!this.world.isRemote) {
			if (this.world.isDaytime()) {
				float f = this.getBrightness();
				BlockPos blockpos = this.getRidingEntity() instanceof EntityBoat ? (new BlockPos(this.posX, (double) Math.round(this.posY), this.posZ)).up() : new BlockPos(this.posX, (double) Math.round(this.posY), this.posZ);

				if (!getSkinType().protectsFromSun())
					if (f > 0.5F && this.rand.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && this.world.canSeeSky(blockpos)) {
						boolean flag = true;
						ItemStack itemstack = this.getItemStackFromSlot(EntityEquipmentSlot.HEAD);

						if (!itemstack.isEmpty()) {
							if (ConfigValues.HELMETDAMAGE)
								if (itemstack.isItemStackDamageable()) {
									itemstack.setItemDamage(itemstack.getItemDamage() + this.rand.nextInt(2));

									if (itemstack.getItemDamage() >= itemstack.getMaxDamage()) {
										this.renderBrokenItemStack(itemstack);
										this.setItemStackToSlot(EntityEquipmentSlot.HEAD, ItemStack.EMPTY);
									}
								}

							flag = false;
						}

						if (flag)
							this.setFire(6);
					}
			}
			//Equipment Achievements
			if (!getHeldItemMainhand().isEmpty()) {
				if (getOwner() != null) {
					if (getOwner() instanceof EntityPlayerMP)
						if (!armed) {
							CriterionRegistry.instance.SKELETON_STATUS_UPDATE.trigger((EntityPlayerMP) getOwner(), this, Items.WOODEN_SWORD, 0);
							armed = true;
						}
				}
			}else if (armed){
				armed = false;
			}

			if (getSkinType().equals(SkinType.PLAYER)) {
				if (getOwner() != null) {
					if (getOwner() instanceof EntityPlayerMP)
						if (!sally) {
							CriterionRegistry.instance.SKELETON_STATUS_UPDATE.trigger((EntityPlayerMP) getOwner(), this, Overlord.skinsuit, 0);
							sally = true;
						}
				}
			}else if (sally){
				sally = false;
			}
		}

		this.setSize(0.3F, 0.995F);

		float f = this.getBrightness();

		if (f > 0.5F && !getSkinType().protectsFromSun())
			this.idleTime += 1;
		super.onLivingUpdate();
	}

	@Override
	public boolean isChild() {
		return true;
	}

	@Override
	public void onDeath(@Nonnull DamageSource cause) {
		super.onDeath(cause);

		if (cause.getTrueSource() instanceof EntityCreeper && ((EntityCreeper) cause.getTrueSource()).getPowered() && !((EntityCreeper) cause.getTrueSource()).isAIDisabled()) {
			((EntityCreeper) cause.getTrueSource()).incrementDroppedSkulls();
			this.entityDropItem(new ItemStack(Items.SKULL), 0.0F);
		}

		if (!this.world.isRemote) {
			for (int i = 0; i < equipInventory.getSizeInventory(); i++) {
				if (!equipInventory.getStackInSlot(i).isEmpty()) {
					EntityItem entityitem = new EntityItem(world, posX, posY, posZ, equipInventory.getStackInSlot(i));
					entityitem.setDefaultPickupDelay();
					world.spawnEntity(entityitem);
				}
			}
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		if (compound.hasKey("SkinsuitName")) {
			String s = compound.getString("SkinsuitName");
			this.dataManager.set(SKINSUIT_NAME, s);
		}
		if (compound.hasKey("HasSkinsuit")) {
			boolean b = compound.getBoolean("HasSkinsuit");
			this.dataManager.set(SKINSUIT_TYPE, Byte.valueOf((byte) (b ? 1 : 0)));
		}
		if (compound.hasKey("SkinsuitType")) {
			byte b = compound.getByte("SkinsuitType");
			this.dataManager.set(SKINSUIT_TYPE, Byte.valueOf(b));
		}
		NBTTagList armorInv = (NBTTagList) compound.getTag("SkeletonEquipment");
		if (armorInv != null) {
			for (int i = 0; i < armorInv.tagCount(); i++) {
				NBTTagCompound item = (NBTTagCompound) armorInv.get(i);
				int slot = item.getByte("SlotSkeletonEquipment");
				if (slot >= 0 && slot < equipInventory.getSizeInventory()) {
					equipInventory.setInventorySlotContents(slot, new ItemStack(item));
				}
			}
		} else {
			Overlord.logWarn("List was null when reading Baby Skeleton's Equipment");
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setByte("SkinsuitType", this.dataManager.get(SKINSUIT_TYPE));
		compound.setString("SkinsuitName", this.dataManager.get(SKINSUIT_NAME));

		NBTTagList armorInv = new NBTTagList();
		for (int i = 0; i < equipInventory.getSizeInventory(); i++) {
			ItemStack is = equipInventory.getStackInSlot(i);
			if (!is.isEmpty()) {
				NBTTagCompound item = new NBTTagCompound();

				item.setByte("SlotSkeletonEquipment", (byte) i);
				is.writeToNBT(item);

				armorInv.appendTag(item);
			}
		}
		compound.setTag("SkeletonEquipment", armorInv);
	}

	@Override
	public float getEyeHeight() {
		return 0.93F;
	}

	@Override
	@Nonnull
	public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn) {
		return slotIn == EntityEquipmentSlot.MAINHAND ? equipInventory.getStackInSlot(4) : (slotIn == EntityEquipmentSlot.OFFHAND ? ItemStack.EMPTY : (slotIn.getSlotType() == EntityEquipmentSlot.Type.ARMOR ? this.equipInventory.getStackInSlot(slotIn.getIndex()) : ItemStack.EMPTY));
	}

	@Override
	public void setItemStackToSlot(EntityEquipmentSlot slotIn, @Nonnull ItemStack stack) {
		if (slotIn == EntityEquipmentSlot.MAINHAND) {
			this.playEquipSound(stack);
			this.equipInventory.setInventorySlotContents(4, stack);
		} else if (slotIn.getSlotType() == EntityEquipmentSlot.Type.ARMOR) {
			this.playEquipSound(stack);
			this.equipInventory.setInventorySlotContents(slotIn.getIndex(), stack);
		}
	}

	@Override
	@Nonnull
	public Iterable<ItemStack> getHeldEquipment() {
		return Lists.newArrayList(this.getHeldItemMainhand());
	}

	@Override
	@Nonnull
	public Iterable<ItemStack> getArmorInventoryList() {
		return Arrays.asList(equipInventory.getStackInSlot(0), equipInventory.getStackInSlot(1), equipInventory.getStackInSlot(2), equipInventory.getStackInSlot(3));
	}

	@Override
	@Nonnull
	public ItemStack getHeldItemMainhand() {
		if (equipInventory == null)
			return ItemStack.EMPTY;
		return equipInventory.getStackInSlot(4);
	}

	@Override
	@Nonnull
	public ItemStack getHeldItemOffhand() {
		return ItemStack.EMPTY;
	}

	@Override
	@Nonnull
	public ItemStack getHeldItem(EnumHand hand) {
		if (hand == EnumHand.MAIN_HAND) {
			return getHeldItemMainhand();
		} else if (hand == EnumHand.OFF_HAND) {
			return ItemStack.EMPTY;
		} else {
			throw new IllegalArgumentException("Invalid hand: " + hand);
		}
	}

	@Override
	public void setHeldItem(EnumHand hand, @Nonnull ItemStack stack) {
		if (hand == EnumHand.MAIN_HAND) {
			this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, stack);
		} else {
			if (hand != EnumHand.OFF_HAND) {
				throw new IllegalArgumentException("Invalid hand: " + hand);
			}
		}
	}

	@Override
	public float getBlockPathWeight(BlockPos pos) {
		if (!this.getSkinType().protectsFromSun())
			return 0.5F - this.world.getLightBrightness(pos);
		else
			return super.getBlockPathWeight(pos);
	}

	@Override
	public boolean willBeAttackedBy(@Nonnull EntityLiving mob) {
		return this.getSkinType().equals(SkinType.PLAYER) && (getItemStackFromSlot(EntityEquipmentSlot.HEAD).isEmpty() || getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() != Items.SKULL || getItemStackFromSlot(EntityEquipmentSlot.HEAD).getMetadata() == 3);
	}

	@Override
	@Nonnull
	public SkinType getSkinType() {
		return SkinType.get(this.dataManager.get(SKINSUIT_TYPE));
	}

	@Override
	@Nonnull
	public String getSkinName() {
		return this.dataManager.get(SKINSUIT_NAME);
	}

	@SuppressWarnings("UnnecessaryBoxing")
	@Override
	public void setSkinsuit(@Nonnull ItemStack stack, @Nonnull SkinType type) {
		if (type.isNone()) {
			ItemStack dropStack = getSkinType().equals(SkinType.PLAYER) ? new ItemStack(Overlord.skinsuit) : new ItemStack(Overlord.skinsuit_mummy);
			if (ConfigValues.SKINSUITNAMETAGS && this.hasCustomName())
				if (this.getCustomNameTag().equals(getSkinName())) {
					dropStack.setStackDisplayName(getSkinName());
					this.setCustomNameTag("");
				}
			entityDropItem(dropStack, 0.1F);
		}

		this.dataManager.set(SKINSUIT_TYPE, Byte.valueOf((byte) type.ordinal()));
		if (stack.hasDisplayName() && !type.isNone()) {
			this.dataManager.set(SKINSUIT_NAME, String.valueOf(stack.getDisplayName()));
			if (ConfigValues.SKINSUITNAMETAGS && !this.hasCustomName())
				setCustomNameTag(stack.getDisplayName());
		} else
			this.dataManager.set(SKINSUIT_NAME, String.valueOf(""));
	}
}
