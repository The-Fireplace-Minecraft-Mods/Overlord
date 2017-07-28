package the_fireplace.overlord.advancements;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import the_fireplace.overlord.advancements.criterion.CriteriaSkeletonStatusUpdate;

import java.util.Map;

public final class CriterionRegistry {

	public static CriterionRegistry instance;

	public final CriteriaSkeletonStatusUpdate SKELETON_STATUS_UPDATE;

	public CriterionRegistry(){
		SKELETON_STATUS_UPDATE = register(new CriteriaSkeletonStatusUpdate());
		instance = this;
	}

	@SuppressWarnings("unchecked")
	public <T extends ICriterionTrigger> T register(T criterion){
		if (((Map<ResourceLocation, ICriterionTrigger<? >>)ReflectionHelper.getPrivateValue(CriteriaTriggers.class, null, "REGISTRY", "field_192139_s")).containsKey(criterion.getId()))
			throw new IllegalArgumentException("Duplicate criterion id " + criterion.getId());
		else
		{
			((Map<ResourceLocation, ICriterionTrigger<? >>)ReflectionHelper.getPrivateValue(CriteriaTriggers.class, null, "REGISTRY", "field_192139_s")).put(criterion.getId(), criterion);
			return criterion;
		}
	}
}
