package the_fireplace.overlord.augments;

import net.minecraft.entity.Entity;
import the_fireplace.overlord.entity.EntityArmyMember;
import the_fireplace.overlord.tools.Augment;

/**
 * @author The_Fireplace
 */
public class AugmentFastRegen extends Augment {
    @Override
    public void onStrike(EntityArmyMember attacker, Entity entityAttacked) {

    }

    @Override
    public String augmentId() {
        return "fast_regen";
    }

    @Override
    public void onEntityTick(EntityArmyMember entity) {
        if(entity.getHealth() < entity.getMaxHealth() && entity.ticksExisted % 100 == 0){
            entity.heal(1.0F);
        }
    }
}
