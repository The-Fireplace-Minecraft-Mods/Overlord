package the_fireplace.overlord.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.player.EntityPlayer;
import the_fireplace.overlord.entity.EntityArmyMember;
import the_fireplace.overlord.tools.Alliances;

public class EntityAIMasterHurtTarget extends EntityAITarget {
	private EntityArmyMember tameable;
	private EntityLivingBase targetOfAttack;
	private int timestamp;

	public EntityAIMasterHurtTarget(EntityArmyMember theEntityTameableIn) {
		super(theEntityTameableIn, false);
		this.tameable = theEntityTameableIn;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		EntityLivingBase owner = this.tameable.getOwner();
		if (owner == null)
			return false;

		this.targetOfAttack = owner.getLastAttackedEntity();
		int lastAttackedEntityTime = owner.getLastAttackedEntityTime();
		if (targetOfAttack instanceof EntityArmyMember)
			if (((EntityArmyMember) targetOfAttack).getOwnerId().equals(this.tameable.getOwnerId()) || Alliances.getInstance().isAlliedTo(((EntityArmyMember) targetOfAttack).getOwnerId(), this.tameable.getOwnerId()))
				return false;
		if (targetOfAttack instanceof EntityPlayer)
			if (targetOfAttack.getUniqueID().equals(this.tameable.getOwnerId()) || Alliances.getInstance().isAlliedTo(targetOfAttack.getUniqueID(), this.tameable.getOwnerId()))
				return false;
		return lastAttackedEntityTime != this.timestamp && this.isSuitableTarget(this.targetOfAttack, false) && this.tameable.shouldAttackEntity(this.targetOfAttack);
	}

	@Override
	public void startExecuting() {
		this.taskOwner.setAttackTarget(this.targetOfAttack);
		EntityLivingBase owner = this.tameable.getOwner();

		if (owner != null)
			this.timestamp = owner.getLastAttackedEntityTime();

		super.startExecuting();
	}
}