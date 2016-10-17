package the_fireplace.overlord.client.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
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
    public static final ResourceLocation SKELETON_TEXTURES = new ResourceLocation("textures/entity/skeleton/skeleton.png");
    public static final ResourceLocation WITHER_SKELETON_TEXTURES = new ResourceLocation("textures/entity/skeleton/wither_skeleton.png");

    public RenderSkeletonWarrior(RenderManager renderManagerIn)
    {
        super(renderManagerIn, new ModelSkeletonWarrior(), 0.5F);
        this.addLayer(new LayerBipedArmor(this)
        {
            @Override
            protected void initArmor()
            {
                this.modelLeggings = new ModelSkeletonWarrior(0.5F, true, false, 1);
                this.modelArmor = new ModelSkeletonWarrior(1.0F, true, false, 1);
            }
        });
        this.addLayer(new LayerSkinsuit(this));
    }

    @Override
    public void transformHeldFull3DItemLayer()
    {
        GlStateManager.translate(0.09375F, 0.1875F, 0.0F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntitySkeletonWarrior entity)
    {
        if(entity.getAugment() != null)
            if(entity.getAugment().augmentId().equals("wither"))
                return WITHER_SKELETON_TEXTURES;
        return SKELETON_TEXTURES;
    }
}