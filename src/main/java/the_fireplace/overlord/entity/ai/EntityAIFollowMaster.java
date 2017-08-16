package the_fireplace.overlord.entity.ai;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import the_fireplace.overlord.entity.EntityArmyMember;

/**
 * @author The_Fireplace
 */
public class EntityAIFollowMaster extends EntityAIBase {
	private final EntityArmyMember armyMember;
	private EntityLivingBase theOwner;
	World theWorld;
	private final double followSpeed;
	private final PathNavigate armyMemberPathfinder;
	private int timeToRecalcPath;
	float maxDist;
	float minDist;
	private float oldWaterCost;

	public EntityAIFollowMaster(EntityArmyMember armyMember, double followSpeedIn, float minDistIn, float maxDistIn) {
		this.armyMember = armyMember;
		this.theWorld = armyMember.world;
		this.followSpeed = followSpeedIn;
		this.armyMemberPathfinder = armyMember.getNavigator();
		this.minDist = minDistIn;
		this.maxDist = maxDistIn;
		this.setMutexBits(3);

		if (!(armyMember.getNavigator() instanceof PathNavigateGround))
			throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	@Override
	public boolean shouldExecute() {
		EntityLivingBase owner = this.armyMember.getOwner();

		if (owner == null || (owner instanceof EntityPlayer && ((EntityPlayer) owner).isSpectator()) || this.armyMember.getDistanceSqToEntity(owner) < (double) (this.minDist * this.minDist) || armyMember.getAttackTarget() != null)
			return false;
		else {
			this.theOwner = owner;
			return true;
		}
	}

	@Override
	public boolean shouldContinueExecuting() {
		return !this.armyMemberPathfinder.noPath() && armyMember.getAttackTarget() == null && this.armyMember.getDistanceSqToEntity(this.theOwner) > (double) (this.maxDist * this.maxDist);
	}

	@Override
	public void startExecuting() {
		this.timeToRecalcPath = 0;
		this.oldWaterCost = this.armyMember.getPathPriority(PathNodeType.WATER);
		this.armyMember.setPathPriority(PathNodeType.WATER, 0.0F);
	}

	@Override
	public void resetTask() {
		this.theOwner = null;
		this.armyMemberPathfinder.clearPathEntity();
		this.armyMember.setPathPriority(PathNodeType.WATER, this.oldWaterCost);
	}

	private boolean isEmptyBlock(BlockPos pos) {
		IBlockState iblockstate = this.theWorld.getBlockState(pos);
		return iblockstate.getMaterial() == Material.AIR;
	}

	@Override
	public void updateTask() {
		this.armyMember.getLookHelper().setLookPositionWithEntity(this.theOwner, 10.0F, (float) this.armyMember.getVerticalFaceSpeed());

		if (--this.timeToRecalcPath <= 0) {
			this.timeToRecalcPath = 10;

			if (!this.armyMemberPathfinder.tryMoveToEntityLiving(this.theOwner, this.followSpeed)) {
				if (!this.armyMember.getLeashed() && armyMember.fallDistance <= 0) {
					if (this.armyMember.getDistanceSqToEntity(this.theOwner) >= 144.0D) {
						int ownerX = MathHelper.floor(this.theOwner.posX) - 2;
						int ownerY = MathHelper.floor(this.theOwner.getEntityBoundingBox().minY);
						int ownerZ = MathHelper.floor(this.theOwner.posZ) - 2;

						for (int xOffset = 0; xOffset <= 4; ++xOffset) {
							for (int zOffset = 0; zOffset <= 4; ++zOffset) {
								if ((xOffset < 1 || zOffset < 1 || xOffset > 3 || zOffset > 3) && this.theWorld.getBlockState(new BlockPos(ownerX + xOffset, ownerY - 1, ownerZ + zOffset)).isOpaqueCube() && this.isEmptyBlock(new BlockPos(ownerX + xOffset, ownerY, ownerZ + zOffset)) && this.isEmptyBlock(new BlockPos(ownerX + xOffset, ownerY + 1, ownerZ + zOffset))) {
									this.armyMember.setLocationAndAngles((double) ((float) (ownerX + xOffset) + 0.5F), (double) ownerY, (double) ((float) (ownerZ + zOffset) + 0.5F), this.armyMember.rotationYaw, this.armyMember.rotationPitch);
									this.armyMemberPathfinder.clearPathEntity();
									return;
								}
							}
						}
					}
				}
			}
		}
	}
}