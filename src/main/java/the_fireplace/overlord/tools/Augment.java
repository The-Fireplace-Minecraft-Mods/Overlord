package the_fireplace.overlord.tools;

import net.minecraft.entity.Entity;
import the_fireplace.overlord.entity.EntityArmyMember;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author The_Fireplace
 */
@ParametersAreNonnullByDefault
public abstract class Augment {
    public abstract void onEntityTick(EntityArmyMember entity);

    public abstract void onStrike(EntityArmyMember attacker, Entity entityAttacked);

    /**
     * To be used for easy cross-mod compatibility. Rather than having to check for a specific augment, you can check the augment's ID.
     */
    @Nonnull
    public abstract String augmentId();
}
