package the_fireplace.overlord.tools;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;

/**
 * @author The_Fireplace
 */
public final class BPTools {
    public static EntityArrow setEnchantEffects(EntityArrow arrow, EntityLivingBase entity, float distanceFactor){
        int i = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.POWER, entity);
        int j = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.PUNCH, entity);
        DifficultyInstance difficultyinstance = entity.world.getDifficultyForLocation(new BlockPos(entity));
        arrow.setDamage((double)(distanceFactor * 2.0F) + entity.getRNG().nextGaussian() * 0.25D + (double)((float)entity.world.getDifficulty().getDifficultyId() * 0.11F));

        if (i > 0)
        {
            arrow.setDamage(arrow.getDamage() + (double)i * 0.5D + 0.5D);
        }

        if (j > 0)
        {
            arrow.setKnockbackStrength(j);
        }

        boolean flag = entity.isBurning() && difficultyinstance.isHard() && entity.getRNG().nextBoolean();
        flag = flag || EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.FLAME, entity) > 0;

        if (flag)
        {
            arrow.setFire(100);
        }
        return arrow;
    }
}
