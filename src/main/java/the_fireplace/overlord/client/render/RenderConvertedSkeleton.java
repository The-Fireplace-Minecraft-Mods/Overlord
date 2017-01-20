package the_fireplace.overlord.client.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import the_fireplace.overlord.entity.EntityConvertedSkeleton;
import the_fireplace.overlord.network.PacketDispatcher;
import the_fireplace.overlord.network.packets.RequestAugmentMessage;

import javax.annotation.Nonnull;

import static the_fireplace.overlord.client.render.RenderSkeletonWarrior.*;

/**
 * @author The_Fireplace
 */
@SideOnly(Side.CLIENT)
public class RenderConvertedSkeleton extends RenderBiped<EntityConvertedSkeleton>
{
    public RenderConvertedSkeleton(RenderManager renderManagerIn)
    {
        super(renderManagerIn, new ModelConvertedSkeleton(), 0.5F);
        this.addLayer(new LayerConvertedSkinsuit(this));
        this.addLayer(new LayerBipedArmor(this)
        {
            @Override
            protected void initArmor()
            {
                this.modelLeggings = new ModelSkeletonWarrior(0.5F, true, false, 1);
                this.modelArmor = new ModelSkeletonWarrior(1.0F, true, false, 1);
            }
        });
    }

    @Override
    public void transformHeldFull3DItemLayer()
    {
        GlStateManager.translate(0.09375F, 0.1875F, 0.0F);
    }

    @Override
    protected ResourceLocation getEntityTexture(@Nonnull EntityConvertedSkeleton entity)
    {
        if(!entity.cachedClientAugment)
            PacketDispatcher.sendToServer(new RequestAugmentMessage(entity));
        if(entity.getAugment() != null) {
            switch(entity.getAugment().augmentId()) {
                case "wither":
                    return WITHER_SKELETON_TEXTURES;
                case "iron":
                    return IRON_SKELETON_TEXTURES;
                case "iron_anvil":
                    return ANVIL_SKELETON_TEXTURES;
                case "obsidian":
                    return OBSIDIAN_SKELETON_TEXTURES;
                case "pulsatingbrainstone":
                    return BRAINSTONE_SKELETON_TEXTURES;
            }
        }
        return SKELETON_TEXTURES;
    }
}