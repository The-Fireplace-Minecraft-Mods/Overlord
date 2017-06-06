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
public class AugmentOverclock extends Augment {
	@Nonnull
	@Override
	public String augmentId() {
		return "overclock";
	}

	@Override
	public void onStrike(@Nonnull EntityArmyMember attacker, @Nonnull Entity entityAttacked) {

	}

	@Override
	public void onEntityTick(@Nonnull EntityArmyMember entity) {
		if (entity.getActivePotionEffect(MobEffects.HASTE) == null)
			entity.addPotionEffect(new PotionEffect(MobEffects.HASTE, 120));
		if (entity.getActivePotionEffect(MobEffects.WEAKNESS) == null)
			entity.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 120));
	}
}
