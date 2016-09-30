package the_fireplace.overlord.client.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import the_fireplace.overlord.entity.EntitySkeletonWarrior;

/**
 * @author The_Fireplace
 */
@SideOnly(Side.CLIENT)
public class RenderSkeletonWarrior extends RenderBiped<EntitySkeletonWarrior>
{
    private static final ResourceLocation SKELETON_TEXTURES = new ResourceLocation("textures/entity/skeleton/skeleton.png");

    //TODO: Figure out why normal skeletons render from further away than Skeleton Warriors
    public RenderSkeletonWarrior(RenderManager renderManagerIn)
    {
        super(renderManagerIn, new ModelSkeletonWarrior(), 0.5F);
        this.addLayer(new LayerHeldItem(this));
        this.addLayer(new LayerBipedArmor(this)
        {
            @Override
            protected void initArmor()
            {
                this.modelLeggings = new ModelSkeletonWarrior(0.5F, true);
                this.modelArmor = new ModelSkeletonWarrior(1.0F, true);
            }
        });
        //this.addLayer(new LayerSkeletonType(this));
    }

    /**
     * Allows the render to do state modifications necessary before the model is rendered.
     */
    /*protected void preRenderCallback(EntitySkeletonWarrior entitylivingbaseIn, float partialTickTime)
    {
        if (entitylivingbaseIn.func_189771_df() == SkeletonType.WITHER)
        {
            GlStateManager.scale(1.2F, 1.2F, 1.2F);
        }
    }*/

    @Override
    public void transformHeldFull3DItemLayer()
    {
        GlStateManager.translate(0.09375F, 0.1875F, 0.0F);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    @Override
    protected ResourceLocation getEntityTexture(EntitySkeletonWarrior entity)
    {
        return SKELETON_TEXTURES;
    }
}