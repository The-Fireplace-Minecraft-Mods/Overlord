package the_fireplace.overlord.client.render;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import the_fireplace.overlord.entity.projectile.EntityMilkBottle;

/**
 * @author The_Fireplace
 */
public class MilkBottleRenderFactory implements IRenderFactory<EntityMilkBottle> {
	@Override
	public Render<? super EntityMilkBottle> createRenderFor(RenderManager manager) {
		return new RenderMilkBottle(manager);
	}
}