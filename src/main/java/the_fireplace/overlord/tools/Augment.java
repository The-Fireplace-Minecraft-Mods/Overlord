package the_fireplace.overlord.tools;

import com.sun.istack.internal.NotNull;
import net.minecraft.entity.Entity;
import the_fireplace.overlord.entity.EntityArmyMember;

/**
 * @author The_Fireplace
 */
public abstract class Augment {
    public abstract void onEntityTick(EntityArmyMember entity);

    public abstract void onStrike(EntityArmyMember attacker, Entity entityAttacked);

    /**
     * To be used for easy cross-mod compatibility. Rather than having to check for a specific augment, you can check the augment's ID.
     */
    @NotNull
    public abstract String augmentId();
}
