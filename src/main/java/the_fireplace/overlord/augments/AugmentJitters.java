package the_fireplace.overlord.augments;

import net.minecraft.entity.Entity;
import the_fireplace.overlord.entity.EntityArmyMember;
import the_fireplace.overlord.tools.Augment;

import javax.annotation.Nonnull;

/**
 * @author The_Fireplace
 */
public class AugmentJitters extends Augment {
    @Override
    public void onStrike(@Nonnull EntityArmyMember attacker, @Nonnull Entity entityAttacked) {

    }

    @Nonnull
    @Override
    public String augmentId() {
        return "jitters";
    }

    @Override
    public void onEntityTick(@Nonnull EntityArmyMember entity) {
        if(entity.motionY < 0 && entity.onGround){
            entity.motionY = -entity.motionY;
        }
    }
}
