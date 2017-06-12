package the_fireplace.overlord.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.player.EntityPlayer;
import the_fireplace.overlord.entity.EntityArmyMember;
import the_fireplace.overlord.tools.Alliances;

public class EntityAIMasterHurtByTarget extends EntityAITarget {
	EntityArmyMember theDefendingTameable;
	EntityLivingBase theOwnerAttacker;
	private int timestamp;

	public EntityAIMasterHurtByTarget(EntityArmyMember theDefendingTameableIn) {
		super(theDefendingTameableIn, false);
		this.theDefendingTameable = theDefendingTameableIn;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		EntityLivingBase entitylivingbase = this.theDefendingTameable.getOwner();

		if (entitylivingbase instanceof EntityArmyMember)
			if (((EntityArmyMember) entitylivingbase).getOwnerId().equals(this.theDefendingTameable.getOwnerId()))
				return false;
			else if (Alliances.getInstance().isAlliedTo(((EntityArmyMember) entitylivingbase).getOwnerId(), this.theDefendingTameable.getOwnerId()))
				return false;
		if (entitylivingbase instanceof EntityPlayer)
			if (entitylivingbase.getUniqueID().equals(this.theDefendingTameable.getOwnerId()))
				return false;
			else if (Alliances.getInstance().isAlliedTo(entitylivingbase.getUniqueID(), this.theDefendingTameable.getOwnerId()))
				return false;

		if (entitylivingbase == null) {
			return false;
		} else {
			this.theOwnerAttacker = entitylivingbase.getRevengeTarget();
			int i = entitylivingbase.getRevengeTimer();
			return i != this.timestamp && this.isSuitableTarget(this.theOwnerAttacker, false) && this.theDefendingTameable.shouldAttackEntity(this.theOwnerAttacker, entitylivingbase);
		}
	}

	@Override
	public void startExecuting() {
		this.taskOwner.setAttackTarget(this.theOwnerAttacker);
		EntityLivingBase entitylivingbase = this.theDefendingTameable.getOwner();

		if (entitylivingbase != null) {
			this.timestamp = entitylivingbase.getRevengeTimer();
		}

		super.startExecuting();
	}
}