package the_fireplace.overlord.client.render;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import the_fireplace.overlord.entity.EntityBabySkeleton;

/**
 * @author The_Fireplace
 */
public class BabySkeletonRenderFactory implements IRenderFactory<EntityBabySkeleton> {
	@SuppressWarnings("unchecked")
	@Override
	public Render createRenderFor(RenderManager manager) {
		return new RenderBabySkeleton(manager, new ModelBabySkeleton());
	}
}
