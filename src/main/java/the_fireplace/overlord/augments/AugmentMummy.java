package the_fireplace.overlord.augments;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import the_fireplace.overlord.entity.EntityArmyMember;
import the_fireplace.overlord.tools.Augment;

import javax.annotation.Nonnull;

/**
 * @author The_Fireplace
 */
public class AugmentMummy extends Augment {
	@Override
	public void onStrike(@Nonnull EntityArmyMember attacker, @Nonnull Entity entityAttacked) {
		if (entityAttacked instanceof EntityLivingBase)
			if (((EntityLivingBase) entityAttacked).getActivePotionEffect(MobEffects.HUNGER) == null)
				((EntityLivingBase) entityAttacked).addPotionEffect(new PotionEffect(MobEffects.HUNGER, (int) (140 * attacker.world.getDifficultyForLocation(new BlockPos(attacker)).getAdditionalDifficulty())));
	}

	@Nonnull
	@Override
	public String augmentId() {
		return "mummy";
	}

	@Override
	public void onEntityTick(@Nonnull EntityArmyMember armyMember) {
		for (Entity entity : armyMember.world.getEntitiesWithinAABBExcludingEntity(armyMember, armyMember.getEntityBoundingBox().grow(5)))
			if (entity instanceof EntityLivingBase && entity instanceof IEntityOwnable && ((IEntityOwnable) entity).getOwnerId() != null && ((IEntityOwnable) entity).getOwnerId().equals(armyMember.getOwnerId()) && ((EntityLivingBase) entity).getActivePotionEffect(MobEffects.RESISTANCE) == null)
				((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 120));
	}
}
