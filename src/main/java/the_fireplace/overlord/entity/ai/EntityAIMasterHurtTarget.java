package the_fireplace.overlord.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import the_fireplace.overlord.entity.EntityArmyMember;

public class EntityAIMasterHurtTarget extends EntityAITarget
{
    EntityArmyMember theEntityTameable;
    EntityLivingBase theTarget;
    private int timestamp;

    public EntityAIMasterHurtTarget(EntityArmyMember theEntityTameableIn)
    {
        super(theEntityTameableIn, false);
        this.theEntityTameable = theEntityTameableIn;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute()
    {
        EntityLivingBase entitylivingbase = this.theEntityTameable.getOwner();

        if (entitylivingbase == null)
        {
            return false;
        } else {
            this.theTarget = entitylivingbase.getLastAttacker();
            int i = entitylivingbase.getLastAttackerTime();
            return i != this.timestamp && this.isSuitableTarget(this.theTarget, false) && this.theEntityTameable.shouldAttackEntity(this.theTarget, entitylivingbase);
        }
    }

    @Override
    public void startExecuting()
    {
        this.taskOwner.setAttackTarget(this.theTarget);
        EntityLivingBase entitylivingbase = this.theEntityTameable.getOwner();

        if (entitylivingbase != null)
        {
            this.timestamp = entitylivingbase.getLastAttackerTime();
        }

        super.startExecuting();
    }
}