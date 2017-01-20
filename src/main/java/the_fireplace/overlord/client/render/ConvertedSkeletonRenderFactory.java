package the_fireplace.overlord.client.render;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import the_fireplace.overlord.entity.EntityConvertedSkeleton;

/**
 * @author The_Fireplace
 */
public class ConvertedSkeletonRenderFactory implements IRenderFactory<EntityConvertedSkeleton> {
    @SuppressWarnings("unchecked")
    @Override
    public Render createRenderFor(RenderManager manager) {
        return new RenderConvertedSkeleton(manager);
    }
}