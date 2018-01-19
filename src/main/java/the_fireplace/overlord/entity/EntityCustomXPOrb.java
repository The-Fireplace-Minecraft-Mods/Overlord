package the_fireplace.overlord.entity;

import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.util.ArrayList;

public class EntityCustomXPOrb extends EntityXPOrb {
	private static final ArrayList<Class<? extends Entity>> recievers = Lists.newArrayList();
	
	static {
		recievers.add(EntityPlayer.class);
		recievers.add(EntitySkeletonWarrior.class);
	}
	
	private Entity closestXPReciever;
	private int xpTargetColor;
	
	public EntityCustomXPOrb(World worldIn, double x, double y, double z, int expValue) {
		super(worldIn, x, y, z, expValue);
	}

	public EntityCustomXPOrb(World worldIn) {
		super(worldIn);
	}
	
	@Override
	public void onUpdate()
	{
		if(ticksExisted % 2 == 0)
			super.onUpdate();
		else {

			this.prevPosX = this.posX;
			this.prevPosY = this.posY;
			this.prevPosZ = this.posZ;

			if (!this.hasNoGravity())
				this.motionY -= 0.029999999329447746D;

			if (this.xpTargetColor < this.xpColor - 20 + this.getEntityId() % 100) {
				if (this.closestXPReciever == null || this.closestXPReciever.getDistanceSqToEntity(this) > 64.0D) {
					//Identify the closest entity capable of recieving the XP
					this.closestXPReciever = null;
					for (Entity entity : this.world.getEntitiesInAABBexcluding(this, getEntityBoundingBox().grow(8), input -> {
						for (Class<? extends Entity> c : recievers)
							if (input != null && input.getClass().isAssignableFrom(c))
								return true;
						return false;
					})) {
						if (closestXPReciever == null || getDistanceSqToEntity(closestXPReciever) > getDistanceSqToEntity(entity))
							closestXPReciever = entity;
					}
				}

				this.xpTargetColor = this.xpColor;
			}

			if (this.closestXPReciever != null && this.closestXPReciever instanceof EntityPlayer && ((EntityPlayer) this.closestXPReciever).isSpectator())
				this.closestXPReciever = null;

			if (this.closestXPReciever != null) {
				double d1 = (this.closestXPReciever.posX - this.posX) / 8.0D;
				double d2 = (this.closestXPReciever.posY + (double) this.closestXPReciever.getEyeHeight() / 2.0D - this.posY) / 8.0D;
				double d3 = (this.closestXPReciever.posZ - this.posZ) / 8.0D;
				double d4 = Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);
				double d5 = 1.0D - d4;

				if (d5 > 0.0D) {
					d5 = d5 * d5;
					this.motionX += d1 / d4 * d5 * 0.1D;
					this.motionY += d2 / d4 * d5 * 0.1D;
					this.motionZ += d3 / d4 * d5 * 0.1D;
				}
			}

			this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
		}
	}
}
