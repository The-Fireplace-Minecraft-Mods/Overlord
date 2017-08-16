package the_fireplace.overlord.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.player.EntityPlayer;
import the_fireplace.overlord.entity.EntityArmyMember;
import the_fireplace.overlord.tools.Alliances;

/**
 * @author The_Fireplace
 */
public class EntityAIHurtByNonAllied extends EntityAIHurtByTarget {
	EntityArmyMember armyMember;

	public EntityAIHurtByNonAllied(EntityArmyMember creatureIn, boolean entityCallsForHelpIn, Class<?>... targetClassesIn) {
		super(creatureIn, entityCallsForHelpIn, targetClassesIn);
		armyMember = creatureIn;
	}

	@Override
	public boolean shouldExecute() {
		EntityLivingBase attackTarget = this.armyMember.getRevengeTarget();
		return !(attackTarget instanceof EntityPlayer && (attackTarget.getUniqueID().equals(this.armyMember.getOwnerId()) || Alliances.getInstance().isAlliedTo(attackTarget.getUniqueID(), this.armyMember.getOwnerId()) || ((EntityPlayer) attackTarget).isCreative())) && !(attackTarget instanceof EntityArmyMember && (((EntityArmyMember) attackTarget).getOwnerId().equals(this.armyMember.getOwnerId()) || Alliances.getInstance().isAlliedTo(((EntityArmyMember) attackTarget).getOwnerId(), this.armyMember.getOwnerId()))) && super.shouldExecute();
	}
}
