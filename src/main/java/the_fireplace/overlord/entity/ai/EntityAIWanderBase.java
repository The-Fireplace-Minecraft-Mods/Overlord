package the_fireplace.overlord.entity.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.Vec3d;
import the_fireplace.overlord.entity.EntityArmyMember;

/**
 * @author The_Fireplace
 */
public class EntityAIWanderBase extends EntityAIBase {
	private final EntityArmyMember entity;
	private double xPosition;
	private double yPosition;
	private double zPosition;
	private final double speed;
	private int executionChance;
	private boolean mayNotUpdate = true;

	public EntityAIWanderBase(EntityArmyMember creatureIn, double speedIn) {
		this(creatureIn, speedIn, 80);
	}

	public EntityAIWanderBase(EntityArmyMember creatureIn, double speedIn, int chance) {
		this.entity = creatureIn;
		this.speed = speedIn;
		this.executionChance = chance;
		this.setMutexBits(1);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	@Override
	public boolean shouldExecute() {
		if (this.mayNotUpdate) {
			if (this.entity.getRNG().nextInt(this.executionChance) != 0) {
				return false;
			}
		}

		Vec3d vec3d = RandomPositionGenerator.findRandomTarget(this.entity, 10, 7);
		int attempts = 0;
		while (attempts < 10 && entity.getHomePosition().getDistance(vec3d != null ? (int) vec3d.x: (int) entity.posX, vec3d != null ? (int) vec3d.y : (int) entity.posY, vec3d != null ? (int) vec3d.z : (int) entity.posZ) > entity.getMaximumHomeDistance()) {
			vec3d = RandomPositionGenerator.findRandomTarget(this.entity, 12, 8);
			attempts++;
		}

		if (vec3d == null || attempts >= 10) {
			return false;
		} else {
			this.xPosition = vec3d.x;
			this.yPosition = vec3d.y;
			this.zPosition = vec3d.z;
			this.mayNotUpdate = true;
			return true;
		}
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	@Override
	public boolean shouldContinueExecuting() {
		return !this.entity.getNavigator().noPath();
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	@Override
	public void startExecuting() {
		this.entity.getNavigator().tryMoveToXYZ(this.xPosition, this.yPosition, this.zPosition, this.speed);
	}
}
