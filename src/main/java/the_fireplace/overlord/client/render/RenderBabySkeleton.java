package the_fireplace.overlord.client.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerElytra;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import the_fireplace.overlord.entity.EntityBabySkeleton;

import javax.annotation.Nonnull;

/**
 * @author The_Fireplace
 */
@SideOnly(Side.CLIENT)
public class RenderBabySkeleton extends RenderLiving<EntityBabySkeleton> {
	private static final ResourceLocation SKELETON_TEXTURES = new ResourceLocation("textures/entity/skeleton/skeleton.png");

	public RenderBabySkeleton(RenderManager renderManagerIn, ModelBabySkeleton modelBipedIn) {
		super(renderManagerIn, modelBipedIn, 0.5F);
		this.addLayer(new LayerCustomHead(modelBipedIn.bipedHead));
		this.addLayer(new LayerElytra(this));
		this.addLayer(new LayerBabyHeldItem(this));
		this.addLayer(new LayerSkinsuit(this, new ModelBabySkeleton(0.0F, true, true, 2)));
		this.addLayer(new LayerBipedArmor(this) {
			@Override
			protected void initArmor() {
				this.modelLeggings = new ModelBabySkeleton(0.5F, true, false, 1);
				this.modelArmor = new ModelBabySkeleton(1.0F, true, false, 1);
			}
		});
	}

	@Override
	public void transformHeldFull3DItemLayer() {
		GlStateManager.translate(0.09375F, 0.1875F, 0.0F);
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
	 */
	@Override
	protected ResourceLocation getEntityTexture(@Nonnull EntityBabySkeleton entity) {
		return SKELETON_TEXTURES;
	}
}