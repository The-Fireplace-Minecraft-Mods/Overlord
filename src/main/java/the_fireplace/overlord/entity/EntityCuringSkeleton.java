package the_fireplace.overlord.entity;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author The_Fireplace
 */
public class EntityCuringSkeleton extends EntitySkeleton {
	private int conversionTime;
	private UUID owner;

	public EntityCuringSkeleton(World worldIn) {
		this(worldIn, null);
	}

	public EntityCuringSkeleton(World worldIn, @Nullable UUID owner) {
		super(worldIn);
		this.owner = owner;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setInteger("ConversionTime", this.conversionTime);
	}

	@Override
	public void readEntityFromNBT(@Nonnull NBTTagCompound compound) {
		super.readEntityFromNBT(compound);

		if (compound.hasKey("ConversionTime", 99)) {
			this.startConverting(compound.getInteger("ConversionTime"));
		}
	}

	@Override
	public void onUpdate() {
		if (!this.world.isRemote) {
			int i = this.getConversionProgress();
			this.conversionTime -= i;

			if (this.conversionTime <= 0) {
				this.finishConversion();
			}
		}

		super.onUpdate();
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	public void startConverting(int startingTime) {
		this.conversionTime = startingTime;
		this.removePotionEffect(MobEffects.WEAKNESS);
		this.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, startingTime, Math.min(this.world.getDifficulty().getDifficultyId() - 1, 0)));
		this.world.setEntityState(this, (byte) 16);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		if (id == 16) {
			if (!this.isSilent()) {
				this.world.playSound(this.posX + 0.5D, this.posY + 0.5D, this.posZ + 0.5D, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, this.getSoundCategory(), 1.0F + this.rand.nextFloat(), this.rand.nextFloat() * 0.7F + 0.3F, false);
			}
		} else {
			super.handleStatusUpdate(id);
		}
	}

	protected void finishConversion() {
		EntityConvertedSkeleton entityConvertedSkeleton = new EntityConvertedSkeleton(this.world, this.owner);
		entityConvertedSkeleton.copyLocationAndAnglesFrom(this);
		entityConvertedSkeleton.setHeldItem(EnumHand.MAIN_HAND, this.getHeldItemMainhand());
		entityConvertedSkeleton.setHeldItem(EnumHand.OFF_HAND, this.getHeldItemOffhand());
		entityConvertedSkeleton.setItemStackToSlot(EntityEquipmentSlot.HEAD, getItemStackFromSlot(EntityEquipmentSlot.HEAD));
		entityConvertedSkeleton.setItemStackToSlot(EntityEquipmentSlot.CHEST, getItemStackFromSlot(EntityEquipmentSlot.CHEST));
		entityConvertedSkeleton.setItemStackToSlot(EntityEquipmentSlot.LEGS, getItemStackFromSlot(EntityEquipmentSlot.LEGS));
		entityConvertedSkeleton.setItemStackToSlot(EntityEquipmentSlot.FEET, getItemStackFromSlot(EntityEquipmentSlot.FEET));

		this.world.removeEntity(this);
		entityConvertedSkeleton.setNoAI(this.isAIDisabled());

		if (this.hasCustomName()) {
			entityConvertedSkeleton.setCustomNameTag(this.getCustomNameTag());
			entityConvertedSkeleton.setAlwaysRenderNameTag(this.getAlwaysRenderNameTag());
		}

		this.world.spawnEntity(entityConvertedSkeleton);

		EntityLivingBase owner = entityConvertedSkeleton.getOwner();

		if (owner != null && owner instanceof EntityPlayerMP) {
			CriteriaTriggers.SUMMONED_ENTITY.trigger((EntityPlayerMP) owner, entityConvertedSkeleton);
		}

		entityConvertedSkeleton.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 200, 0));
		this.world.playEvent(null, 1027, new BlockPos((int) this.posX, (int) this.posY, (int) this.posZ), 0);
	}

	protected int getConversionProgress() {
		int i = 1;

		if (this.rand.nextFloat() < 0.01F) {
			int j = 0;
			BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

			for (int k = (int) this.posX - 4; k < (int) this.posX + 4 && j < 14; ++k) {
				for (int l = (int) this.posY - 4; l < (int) this.posY + 4 && j < 14; ++l) {
					for (int i1 = (int) this.posZ - 4; i1 < (int) this.posZ + 4 && j < 14; ++i1) {
						Block block = this.world.getBlockState(blockpos$mutableblockpos.setPos(k, l, i1)).getBlock();

						if (block == Blocks.IRON_BARS || block == Blocks.BED) {
							if (this.rand.nextFloat() < 0.3F) {
								++i;
							}

							++j;
						}
					}
				}
			}
		}

		return i;
	}
}
