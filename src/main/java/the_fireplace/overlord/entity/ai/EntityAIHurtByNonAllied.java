package the_fireplace.overlord.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.player.EntityPlayer;
import the_fireplace.overlord.entity.EntitySkeletonWarrior;
import the_fireplace.overlord.tools.Alliances;

/**
 * @author The_Fireplace
 */
public class EntityAIHurtByNonAllied extends EntityAIHurtByTarget {
    EntitySkeletonWarrior owner;
    public EntityAIHurtByNonAllied(EntitySkeletonWarrior creatureIn, boolean entityCallsForHelpIn, Class<?>... targetClassesIn) {
        super(creatureIn, entityCallsForHelpIn, targetClassesIn);
        owner = creatureIn;
    }
    @Override
    public boolean shouldExecute()
    {
        EntityLivingBase entitylivingbase = this.taskOwner.getAITarget();
        if(entitylivingbase instanceof EntitySkeletonWarrior)
            if(((EntitySkeletonWarrior) entitylivingbase).getOwnerId().equals(this.owner.getOwnerId()))
                return false;
            else if(Alliances.getInstance().isAlliedTo(((EntitySkeletonWarrior) entitylivingbase).getOwnerId(), this.owner.getOwnerId()))
                return false;
        if(entitylivingbase instanceof EntityPlayer)
            if(entitylivingbase.getUniqueID().equals(this.owner.getOwnerId()))
                return false;
            else if(Alliances.getInstance().isAlliedTo(entitylivingbase.getUniqueID(), this.owner.getOwnerId()))
                return false;
        return super.shouldExecute();
    }
}
