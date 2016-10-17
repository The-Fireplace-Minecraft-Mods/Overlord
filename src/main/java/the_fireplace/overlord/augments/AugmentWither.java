package the_fireplace.overlord.augments;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import the_fireplace.overlord.entity.EntityArmyMember;
import the_fireplace.overlord.tools.Augment;

/**
 * @author The_Fireplace
 */
public class AugmentWither extends Augment {
    @Override
    public void onStrike(EntityArmyMember attacker, Entity entityAttacked) {
        if(entityAttacked instanceof EntityLivingBase)
        if(((EntityLivingBase)entityAttacked).getActivePotionEffect(MobEffects.WITHER) == null)
            ((EntityLivingBase)entityAttacked).addPotionEffect(new PotionEffect(MobEffects.WITHER, 200));
    }

    @Override
    public void onEntityTick(EntityArmyMember entity) {
        if(entity.isBurning() && entity.getActivePotionEffect(MobEffects.FIRE_RESISTANCE) == null)
            entity.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 120));
    }
}
