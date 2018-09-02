package the_fireplace.overlord.entity.ai;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.Team;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import the_fireplace.overlord.entity.EntityArmyMember;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class EntityAIFindEntityNearestSkins extends EntityAIBase {
	private static final Logger LOGGER = LogManager.getLogger();
	private final EntityLiving taskOwner;
	private final Predicate<Entity> predicate;
	private final EntityAINearestAttackableTarget.Sorter sorter;
	private EntityLivingBase entityTarget;

	public final Predicate<EntityArmyMember> CAN_ATTACK_ARMY_MEMBER = new Predicate<EntityArmyMember>() {
		@Override
		public boolean test(@Nullable EntityArmyMember p_apply_1_) {
			return p_apply_1_ != null && p_apply_1_.willBeAttackedBy(taskOwner);
		}
	};

	public EntityAIFindEntityNearestSkins(EntityLiving entityLivingIn) {
		this.taskOwner = entityLivingIn;

		if (entityLivingIn instanceof EntityCreature) {
			LOGGER.warn("Use NearestAttackableTargetGoal.class for PathfinderMob mobs!");
		}

		this.predicate = p_apply_1_ -> {
			if (!(p_apply_1_ instanceof EntityArmyMember)) {
				return false;
			} else if (p_apply_1_.getIsInvulnerable()) {
				return false;
			} else {
				double maxTargetRange = EntityAIFindEntityNearestSkins.this.maxTargetRange();

				return !((double) p_apply_1_.getDistance(EntityAIFindEntityNearestSkins.this.taskOwner) > maxTargetRange) && (EntityAITarget.isSuitableTarget(EntityAIFindEntityNearestSkins.this.taskOwner, (EntityLivingBase) p_apply_1_, false, true) && CAN_ATTACK_ARMY_MEMBER.test((EntityArmyMember) p_apply_1_));
			}
		};
		this.sorter = new EntityAINearestAttackableTarget.Sorter(entityLivingIn);
	}

	@Override
	public boolean shouldExecute() {
		double maxTargetRange = this.maxTargetRange();
		List<EntityPlayer> players = this.taskOwner.world.getEntitiesWithinAABB(EntityPlayer.class, this.taskOwner.getEntityBoundingBox().grow(maxTargetRange, 4.0D, maxTargetRange), this.predicate::test);
		players.sort(this.sorter);

		if (players.isEmpty())
			return false;
		else {
			this.entityTarget = players.get(0);
			return true;
		}
	}

	@Override
	public boolean shouldContinueExecuting() {
		EntityLivingBase attackTarget = this.taskOwner.getAttackTarget();

		if (attackTarget == null) {
			return false;
		} else if (!attackTarget.isEntityAlive()) {
			return false;
		} else if (attackTarget instanceof EntityPlayer && ((EntityPlayer) attackTarget).capabilities.disableDamage) {
			return false;
		} else {
			Team ownerTeam = this.taskOwner.getTeam();
			Team targetTeam = attackTarget.getTeam();

			if (ownerTeam != null && targetTeam == ownerTeam)
				return false;
			else {
				double maxTargetRange = this.maxTargetRange();
				return !(this.taskOwner.getDistanceSq(attackTarget) > maxTargetRange * maxTargetRange) && (!(attackTarget instanceof EntityPlayerMP) || !((EntityPlayerMP) attackTarget).interactionManager.isCreative());
			}
		}
	}

	@Override
	public void startExecuting() {
		this.taskOwner.setAttackTarget(this.entityTarget);
		super.startExecuting();
	}

	@Override
	public void resetTask() {
		this.taskOwner.setAttackTarget(null);
		super.startExecuting();
	}

	protected double maxTargetRange() {
		IAttributeInstance iattributeinstance = this.taskOwner.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
		return iattributeinstance == null ? 16.0D : iattributeinstance.getAttributeValue();
	}
}