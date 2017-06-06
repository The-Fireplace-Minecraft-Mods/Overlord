package the_fireplace.overlord.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.player.EntityPlayer;
import the_fireplace.overlord.entity.EntityArmyMember;
import the_fireplace.overlord.tools.Alliances;

public class EntityAIMasterHurtTarget extends EntityAITarget {
	EntityArmyMember theEntityTameable;
	EntityLivingBase theTarget;
	private int timestamp;

	public EntityAIMasterHurtTarget(EntityArmyMember theEntityTameableIn) {
		super(theEntityTameableIn, false);
		this.theEntityTameable = theEntityTameableIn;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		EntityLivingBase entitylivingbase = this.theEntityTameable.getOwner();
		if (entitylivingbase instanceof EntityArmyMember)
			if (((EntityArmyMember) entitylivingbase).getOwnerId().equals(this.theEntityTameable.getOwnerId()))
				return false;
			else if (Alliances.getInstance().isAlliedTo(((EntityArmyMember) entitylivingbase).getOwnerId(), this.theEntityTameable.getOwnerId()))
				return false;
		if (entitylivingbase instanceof EntityPlayer)
			if (entitylivingbase.getUniqueID().equals(this.theEntityTameable.getOwnerId()))
				return false;
			else if (Alliances.getInstance().isAlliedTo(entitylivingbase.getUniqueID(), this.theEntityTameable.getOwnerId()))
				return false;
		if (entitylivingbase == null) {
			return false;
		} else {
			this.theTarget = entitylivingbase.getLastAttacker();
			int i = entitylivingbase.getLastAttackerTime();
			return i != this.timestamp && this.isSuitableTarget(this.theTarget, false) && this.theEntityTameable.shouldAttackEntity(this.theTarget, entitylivingbase);
		}
	}

	@Override
	public void startExecuting() {
		this.taskOwner.setAttackTarget(this.theTarget);
		EntityLivingBase entitylivingbase = this.theEntityTameable.getOwner();

		if (entitylivingbase != null) {
			this.timestamp = entitylivingbase.getLastAttackerTime();
		}

		super.startExecuting();
	}
}