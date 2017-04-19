package the_fireplace.overlord.client.render;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import the_fireplace.overlord.entity.EntityBabySkeleton;

/**
 * @author The_Fireplace
 */
public class BabySkeletonRenderFactory implements IRenderFactory<EntityBabySkeleton> {
    @Override
    public Render<EntityBabySkeleton> createRenderFor(RenderManager manager) {
        return new RenderBabySkeleton(manager);
    }
}
