package the_fireplace.overlord.entity.ai;

import com.google.common.base.MoreObjects;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityPigZombie;
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
	protected final Predicate<? super T> isPassivePigman = (Predicate<T>) input -> input instanceof EntityPigZombie && !((EntityPigZombie)input).isAngry();
	protected final Predicate<? super T> shouldNotAttackEntity;
	protected T targetEntity;
	protected EntityArmyMember armyMember;

	public EntityAINearestNonTeamTarget(EntityArmyMember armyMember, Class<T> classTarget, boolean checkSight) {
		this(armyMember, classTarget, checkSight, false);
	}

	public EntityAINearestNonTeamTarget(EntityArmyMember armyMember, Class<T> classTarget, boolean checkSight, boolean onlyNearby) {
		this(armyMember, classTarget, 10, checkSight, onlyNearby, null);
	}

	public EntityAINearestNonTeamTarget(EntityArmyMember armyMember, Class<T> classTarget, int chance, boolean checkSight, boolean onlyNearby, @Nullable final java.util.function.Predicate<? super T> targetSelector) {
		super(armyMember, checkSight, onlyNearby);
		this.targetClass = classTarget;
		this.targetChance = chance;
		this.armyMember = armyMember;
		this.theNearestAttackableTargetSorter = new Sorter(armyMember);
		this.setMutexBits(1);
		this.targetEntitySelector = (Predicate<T>) input -> input != null && (!(targetSelector != null && !targetSelector.test(input)) && (EntitySelectors.NOT_SPECTATING.apply(input) && EntityAINearestNonTeamTarget.this.isSuitableTarget(input, false)));
		this.shouldNotAttackEntity = (Predicate<T>) input -> !armyMember.shouldAttackEntity(input);
	}

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
				if(armyMember.getAttackMode() != 2)
					list.removeIf(isPassivePigman);
				list.removeIf(shouldNotAttackEntity);
				if(list.isEmpty())
					return false;
				list.sort(this.theNearestAttackableTargetSorter);
				if (!armyMember.shouldAttackEntity(list.get(0)))
					return false;
				this.targetEntity = list.get(0);
				return true;
			}
		} else {
			this.targetEntity = (T) getNearestAttackablePlayer(this.taskOwner.world, this.taskOwner.posX, this.taskOwner.posY + (double) this.taskOwner.getEyeHeight(), this.taskOwner.posZ, this.getTargetDistance(), this.getTargetDistance(), p_apply_1_ -> 1.0D, (Predicate<EntityPlayer>) this.targetEntitySelector);
			return this.targetEntity != null;
		}
	}

	protected AxisAlignedBB getTargetableArea(double targetDistance) {
		return this.taskOwner.getEntityBoundingBox().grow(targetDistance, 4.0D, targetDistance);
	}

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
			double compareDistance1 = this.theEntity.getDistanceSqToEntity(p_compare_1_);
			double compareDistance2 = this.theEntity.getDistanceSqToEntity(p_compare_2_);
			boolean b0 = false;
			boolean b1i = true;
			if (!ConfigValues.HUNTCREEPERS && p_compare_1_ instanceof EntityCreeper)
				b0 = true;
			if (!ConfigValues.HUNTCREEPERS && p_compare_2_ instanceof EntityCreeper)
				b1i = false;
			if (!b0 && b1i) {
				return Double.compare(compareDistance1, compareDistance2);
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
					d2 *= MoreObjects.firstNonNull(playerToDouble.apply(entityplayer1), 1.0D);
				}

				if ((maxYDistance < 0.0D || Math.abs(entityplayer1.posY - posY) < maxYDistance * maxYDistance) && (maxXZDistance < 0.0D || d1 < d2 * d2) && (d0 == -1.0D || d1 < d0)) {
					d0 = d1;
					entityplayer = entityplayer1;
				}
			}
		}

		return entityplayer;
	}

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