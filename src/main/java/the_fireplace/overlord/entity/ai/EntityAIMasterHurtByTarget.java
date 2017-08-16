package the_fireplace.overlord.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.player.EntityPlayer;
import the_fireplace.overlord.entity.EntityArmyMember;
import the_fireplace.overlord.tools.Alliances;

public class EntityAIMasterHurtByTarget extends EntityAITarget {
	EntityArmyMember theDefendingTameable;
	EntityLivingBase entityAttackingOwner;
	private int timestamp;

	public EntityAIMasterHurtByTarget(EntityArmyMember theDefendingTameableIn) {
		super(theDefendingTameableIn, false);
		this.theDefendingTameable = theDefendingTameableIn;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		EntityLivingBase owner = this.theDefendingTameable.getOwner();
		if (owner == null)
			return false;
		this.entityAttackingOwner = owner.getRevengeTarget();
		int revengeTimer = owner.getRevengeTimer();

		if (entityAttackingOwner instanceof EntityArmyMember)
			if (((EntityArmyMember) entityAttackingOwner).getOwnerId().equals(this.theDefendingTameable.getOwnerId()) || Alliances.getInstance().isAlliedTo(((EntityArmyMember) entityAttackingOwner).getOwnerId(), this.theDefendingTameable.getOwnerId()))
				return false;
		if (entityAttackingOwner instanceof EntityPlayer)
			if (entityAttackingOwner.getUniqueID().equals(this.theDefendingTameable.getOwnerId()) || Alliances.getInstance().isAlliedTo(entityAttackingOwner.getUniqueID(), this.theDefendingTameable.getOwnerId()))
				return false;

		return revengeTimer != this.timestamp && this.isSuitableTarget(this.entityAttackingOwner, false) && this.theDefendingTameable.shouldAttackEntity(this.entityAttackingOwner, owner);
	}

	@Override
	public void startExecuting() {
		this.taskOwner.setAttackTarget(this.entityAttackingOwner);
		EntityLivingBase owner = this.theDefendingTameable.getOwner();

		if (owner != null)
			this.timestamp = owner.getRevengeTimer();

		super.startExecuting();
	}
}