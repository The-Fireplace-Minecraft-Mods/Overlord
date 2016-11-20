package the_fireplace.overlord.client.render;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import the_fireplace.overlord.entity.EntitySkeletonWarrior;

/**
 * @author The_Fireplace
 */
public class SkeletonWarriorRenderFactory implements IRenderFactory<EntitySkeletonWarrior> {
    @SuppressWarnings("unchecked")
    @Override
    public Render createRenderFor(RenderManager manager) {
        return new RenderSkeletonWarrior(manager);
    }
}
