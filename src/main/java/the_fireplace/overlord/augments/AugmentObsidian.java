package the_fireplace.overlord.augments;

import net.minecraft.entity.Entity;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import the_fireplace.overlord.entity.EntityArmyMember;
import the_fireplace.overlord.tools.Augment;

import javax.annotation.Nonnull;

/**
 * @author The_Fireplace
 */
public class AugmentObsidian extends Augment {
    @Nonnull
    @Override
    public String augmentId() {
        return "obsidian";
    }

    @Override
    public void onStrike(@Nonnull EntityArmyMember attacker, @Nonnull Entity entityAttacked) {

    }

    @Override
    public void onEntityTick(@Nonnull EntityArmyMember entity) {
        if(entity.getActivePotionEffect(MobEffects.FIRE_RESISTANCE) == null)
            entity.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 120));
        if(entity.getActivePotionEffect(MobEffects.SLOWNESS) == null)
            entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 120));
        if(entity.getActivePotionEffect(MobEffects.RESISTANCE) == null)
            entity.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 120));
    }
}
