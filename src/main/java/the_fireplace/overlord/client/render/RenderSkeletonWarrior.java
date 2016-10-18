package the_fireplace.overlord.client.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.entity.EntitySkeletonWarrior;
import the_fireplace.overlord.network.PacketDispatcher;
import the_fireplace.overlord.network.packets.RequestAugmentMessage;

/**
 * @author The_Fireplace
 */
@SideOnly(Side.CLIENT)
public class RenderSkeletonWarrior extends RenderBiped<EntitySkeletonWarrior>
{
    public static final ResourceLocation SKELETON_TEXTURES = new ResourceLocation("textures/entity/skeleton/skeleton.png");
    public static final ResourceLocation WITHER_SKELETON_TEXTURES = new ResourceLocation("textures/entity/skeleton/wither_skeleton.png");
    public static final ResourceLocation IRON_SKELETON_TEXTURES = new ResourceLocation(Overlord.MODID, "textures/entity/iron_skeleton.png");
    public static final ResourceLocation OBSIDIAN_SKELETON_TEXTURES = new ResourceLocation(Overlord.MODID, "textures/entity/obsidian_skeleton.png");
    public static final ResourceLocation ANVIL_SKELETON_TEXTURES = new ResourceLocation(Overlord.MODID, "textures/entity/anvil_skeleton.png");

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
            }
        }
        return SKELETON_TEXTURES;
    }
}