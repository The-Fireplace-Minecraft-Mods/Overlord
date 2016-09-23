package the_fireplace.skeletonwars.client.render;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import the_fireplace.skeletonwars.entity.EntitySkeletonWarrior;

/**
 * @author The_Fireplace
 */
public class SkeletonWarriorRenderFactory implements IRenderFactory<EntitySkeletonWarrior> {
    @Override
    public Render createRenderFor(RenderManager manager) {
        return new RenderSkeletonWarrior(manager);
    }
}
