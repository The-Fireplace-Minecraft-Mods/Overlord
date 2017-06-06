package the_fireplace.overlord.entity.ai;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import the_fireplace.overlord.config.ConfigValues;
import the_fireplace.overlord.entity.EntityArmyMember;
import the_fireplace.overlord.tools.Alliances;
import the_fireplace.overlord.tools.Enemies;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

/**
 * @author The_Fireplace
 */
@SuppressWarnings("Guava")
public class EntityAINearestNonTeamTarget<T extends EntityLivingBase> extends EntityAITarget {
	protected final Class<T> targetClass;
	private final int targetChance;
	protected final Sorter theNearestAttackableTargetSorter;
	protected final Predicate<? super T> targetEntitySelector;
	protected T targetEntity;

	public EntityAINearestNonTeamTarget(EntityCreature creature, Class<T> classTarget, boolean checkSight) {
		this(creature, classTarget, checkSight, false);
	}

	public EntityAINearestNonTeamTarget(EntityCreature creature, Class<T> classTarget, boolean checkSight, boolean onlyNearby) {
		this(creature, classTarget, 10, checkSight, onlyNearby, null);
	}

	public EntityAINearestNonTeamTarget(EntityCreature creature, Class<T> classTarget, int chance, boolean checkSight, boolean onlyNearby, @Nullable final java.util.function.Predicate<? super T> targetSelector) {
		super(creature, checkSight, onlyNearby);
		this.targetClass = classTarget;
		this.targetChance = chance;
		this.theNearestAttackableTargetSorter = new Sorter(creature);
		this.setMutexBits(1);
		this.targetEntitySelector = (Predicate<T>) p_apply_1_ -> p_apply_1_ != null && (!(targetSelector != null && !targetSelector.test(p_apply_1_)) && (EntitySelectors.NOT_SPECTATING.apply(p_apply_1_) && EntityAINearestNonTeamTarget.this.isSuitableTarget(p_apply_1_, false)));
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public boolean shouldExecute() {
		if (this.targetChance > 0 && this.taskOwner.getRNG().nextInt(this.targetChance) != 0) {
			return false;
		} else if (this.targetClass != EntityPlayer.class && this.targetClass != EntityPlayerMP.class) {
			List<T> list = getEntitiesWithinAABB(this.taskOwner.world, this.targetClass, this.getTargetableArea(this.getTargetDistance()), this.targetEntitySelector);

			if (list.isEmpty()) {
				return false;
			} else {
				list.sort(this.theNearestAttackableTargetSorter);
				if (!((EntityArmyMember) this.taskOwner).shouldAttackEntity(list.get(0), ((EntityArmyMember) this.taskOwner).getOwner()))
					return false;
				this.targetEntity = list.get(0);
				return true;
			}
		} else {
			this.targetEntity = (T) getNearestAttackablePlayer(this.taskOwner.world, this.taskOwner.posX, this.taskOwner.posY + (double) this.taskOwner.getEyeHeight(), this.taskOwner.posZ, this.getTargetDistance(), this.getTargetDistance(), new Function<EntityPlayer, Double>() {
				@Override
				@Nullable
				public Double apply(@Nullable EntityPlayer p_apply_1_) {
					return Double.valueOf(1.0D);
				}
			}, (Predicate<EntityPlayer>) this.targetEntitySelector);
			return this.targetEntity != null;
		}
	}

	protected AxisAlignedBB getTargetableArea(double targetDistance) {
		return this.taskOwner.getEntityBoundingBox().expand(targetDistance, 4.0D, targetDistance);
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	@Override
	public void startExecuting() {
		this.taskOwner.setAttackTarget(this.targetEntity);
		super.startExecuting();
	}

	public static class Sorter implements Comparator<Entity> {
		private final Entity theEntity;

		public Sorter(Entity theEntityIn) {
			this.theEntity = theEntityIn;
		}

		@Override
		public int compare(Entity p_compare_1_, Entity p_compare_2_) {
			double d0 = this.theEntity.getDistanceSqToEntity(p_compare_1_);
			double d1 = this.theEntity.getDistanceSqToEntity(p_compare_2_);
			boolean b0 = false;
			boolean b1i = true;
			if (p_compare_1_ instanceof EntityArmyMember) {
				b0 = ((EntityArmyMember) p_compare_1_).getOwnerId().equals(theEntity.getUniqueID());
				if (!b0)
					b0 = Alliances.getInstance().isAlliedTo(((EntityArmyMember) p_compare_1_).getOwnerId(), theEntity.getUniqueID());
			}
			if (p_compare_2_ instanceof EntityArmyMember) {
				b1i = !((EntityArmyMember) p_compare_2_).getOwnerId().equals(theEntity.getUniqueID());
				if (b1i)
					b1i = !Alliances.getInstance().isAlliedTo(((EntityArmyMember) p_compare_2_).getOwnerId(), theEntity.getUniqueID());
			}
			if (!ConfigValues.HUNTCREEPERS && p_compare_1_ instanceof EntityCreeper)
				b0 = true;
			if (!ConfigValues.HUNTCREEPERS && p_compare_2_ instanceof EntityCreeper)
				b1i = false;
			if (!b0 && b1i) {
				return d0 < d1 ? -1 : (d0 > d1 ? 1 : 0);
			} else if (b0 && b1i) {
				return 1;
			} else if (!b0) {
				return -1;
			} else {
				return 0;
			}
		}
	}

	@Nullable
	public EntityPlayer getNearestAttackablePlayer(World worldObj, double posX, double posY, double posZ, double maxXZDistance, double maxYDistance, @Nullable Function<EntityPlayer, Double> playerToDouble, @Nullable Predicate<EntityPlayer> p_184150_12_) {
		double d0 = -1.0D;
		EntityPlayer entityplayer = null;

		for (int i = 0; i < worldObj.playerEntities.size(); ++i) {
			EntityPlayer entityplayer1 = worldObj.playerEntities.get(i);
			if (entityplayer1.getUniqueID() == ((EntityArmyMember) this.taskOwner).getOwnerId() || Alliances.getInstance().isAlliedTo(entityplayer1.getUniqueID(), ((EntityArmyMember) this.taskOwner).getOwnerId()))
				continue;//Skip the owner
			if (((EntityArmyMember) taskOwner).getAttackMode() < 2 && Enemies.getInstance().isNotEnemiesWith(((EntityArmyMember) taskOwner).getOwnerId(), entityplayer1.getUniqueID()))
				continue;

			if (!entityplayer1.capabilities.disableDamage && entityplayer1.isEntityAlive() && !entityplayer1.isSpectator() && (p_184150_12_ == null || p_184150_12_.apply(entityplayer1))) {
				double d1 = entityplayer1.getDistanceSq(posX, entityplayer1.posY, posZ);
				double d2 = maxXZDistance;

				if (entityplayer1.isSneaking()) {
					d2 = maxXZDistance * 0.800000011920929D;
				}

				if (entityplayer1.isInvisible()) {
					float f = entityplayer1.getArmorVisibility();

					if (f < 0.1F) {
						f = 0.1F;
					}

					d2 *= (double) (0.7F * f);
				}

				if (playerToDouble != null) {
					d2 *= Objects.firstNonNull(playerToDouble.apply(entityplayer1), 1.0D);
				}

				if ((maxYDistance < 0.0D || Math.abs(entityplayer1.posY - posY) < maxYDistance * maxYDistance) && (maxXZDistance < 0.0D || d1 < d2 * d2) && (d0 == -1.0D || d1 < d0)) {
					d0 = d1;
					entityplayer = entityplayer1;
				}
			}
		}

		return entityplayer;
	}

	/**
	 * Returns a list of all entities within the given bounding box that match the class or interface provided
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> getEntitiesWithinAABB(World world, Class<T> clazz, AxisAlignedBB aabb, Predicate<? super T> predicate) {
		List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, aabb);
		List<T> found = Lists.newArrayList();
		for (Entity e : entities) {
			if (clazz.isAssignableFrom(e.getClass())) {
				found.add((T) e);
			}
		}
		found = new ArrayList<>(Collections2.filter(found, predicate));
		return found;
	}
}