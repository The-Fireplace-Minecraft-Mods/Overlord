package the_fireplace.overlord.client.render;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSkeleton;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import the_fireplace.overlord.entity.EntityCuringSkeleton;

/**
 * @author The_Fireplace
 */
public class CuringSkeletonRenderFactory implements IRenderFactory<EntityCuringSkeleton> {
	@SuppressWarnings("unchecked")
	@Override
	public Render createRenderFor(RenderManager manager) {
		return new RenderSkeleton(manager);
	}
}
