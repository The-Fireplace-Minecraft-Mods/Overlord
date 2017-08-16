package the_fireplace.overlord.entity.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.BlockPos;
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

	@Override
	public boolean shouldExecute() {
		if (this.mayNotUpdate && this.entity.getRNG().nextInt(this.executionChance) != 0)
			return false;

		Vec3d vec3d = RandomPositionGenerator.findRandomTarget(this.entity, 10, 7);
		int wanderTargetAttempts = 0;
		while (wanderTargetAttempts < 10 && (vec3d == null || !entity.isWithinHomeDistanceFromPosition(new BlockPos(vec3d)))) {
			vec3d = RandomPositionGenerator.findRandomTarget(this.entity, 12, 8);
			wanderTargetAttempts++;
		}

		if (vec3d == null || wanderTargetAttempts >= 10)
			return false;
		else {
			this.xPosition = vec3d.x;
			this.yPosition = vec3d.y;
			this.zPosition = vec3d.z;
			this.mayNotUpdate = true;
			return true;
		}
	}

	@Override
	public boolean shouldContinueExecuting() {
		return !this.entity.getNavigator().noPath();
	}

	@Override
	public void startExecuting() {
		this.entity.getNavigator().tryMoveToXYZ(this.xPosition, this.yPosition, this.zPosition, this.speed);
	}
}
