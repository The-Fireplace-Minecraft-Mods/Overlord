package the_fireplace.overlord.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import the_fireplace.overlord.entity.EntityConvertedSkeleton;
import the_fireplace.overlord.network.PacketDispatcher;
import the_fireplace.overlord.network.packets.RequestAugmentMessage;

import javax.annotation.Nonnull;

import java.util.Random;

import static the_fireplace.overlord.client.render.RenderSkeletonWarrior.*;

/**
 * @author The_Fireplace
 */
@SideOnly(Side.CLIENT)
public class RenderConvertedSkeleton extends RenderBiped<EntityConvertedSkeleton>
{
    static Random random = new Random();
    EntityItem entityItem;

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

    @Override
    public void doRender(@Nonnull EntityConvertedSkeleton entityConvertedSkeleton, double x, double y, double z, float entityYaw, float partialTicks)
    {
        super.doRender(entityConvertedSkeleton, x, y, z, entityYaw, partialTicks);

        //Render item//TODO: Move this to a seperate file to avoid having this code multiple times.
        if(entityItem == null) {
            entityItem = new EntityItem(entityConvertedSkeleton.world, x, y + entityConvertedSkeleton.height + 0.25F, z, entityConvertedSkeleton.getAugmentDisplayStack());
            entityItem.setNoDespawn();
        }
        entityItem.setLocationAndAngles(x, y+entityConvertedSkeleton.height+0.25F, z, 0, 0);
        entityItem.setEntityItemStack(entityConvertedSkeleton.getAugmentDisplayStack());

        ItemStack itemstack = entityItem.getEntityItem();
        int i = itemstack.isEmpty() ? 187 : Item.getIdFromItem(itemstack.getItem()) + itemstack.getMetadata();
        random.setSeed((long)i);

        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        this.renderManager.renderEngine.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);

        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.pushMatrix();
        IBakedModel ibakedmodel = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(itemstack, entityItem.world, null);
        int j = this.transformModelCount(entityItem, x, y, z, partialTicks, ibakedmodel);
        boolean flag1 = ibakedmodel.isGui3d();

        if (!flag1)
        {
            float f3 = -0.0F * (float)(j - 1) * 0.5F;
            float f4 = -0.0F * (float)(j - 1) * 0.5F;
            float f5 = -0.09375F * (float)(j - 1) * 0.5F;
            GlStateManager.translate(f3, f4, f5);
        }

        GlStateManager.translate(0, entityConvertedSkeleton.height+0.1F, 0);

        for (int k = 0; k < j; ++k)
        {
            if (flag1)
            {
                GlStateManager.pushMatrix();

                if (k > 0)
                {
                    float f7 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float f9 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float f6 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    GlStateManager.translate(f7, f9, f6);
                }

                ibakedmodel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(ibakedmodel, ItemCameraTransforms.TransformType.GROUND, false);
                Minecraft.getMinecraft().getRenderItem().renderItem(itemstack, ibakedmodel);
                GlStateManager.popMatrix();
            }
            else
            {
                GlStateManager.pushMatrix();

                if (k > 0)
                {
                    float f8 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                    float f10 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                    GlStateManager.translate(f8, f10, 0.0F);
                }

                ibakedmodel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(ibakedmodel, ItemCameraTransforms.TransformType.GROUND, false);
                Minecraft.getMinecraft().getRenderItem().renderItem(itemstack, ibakedmodel);
                GlStateManager.popMatrix();
                GlStateManager.translate(0.0F, 0.0F, 0.09375F);
            }
        }

        if (this.renderOutlines)
        {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        this.renderManager.renderEngine.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
    }

    private int transformModelCount(EntityItem itemIn, double p_177077_2_, double p_177077_4_, double p_177077_6_, float p_177077_8_, IBakedModel p_177077_9_)
    {
        ItemStack itemstack = itemIn.getEntityItem();
        Item item = itemstack.getItem();

        if (item == null)
        {
            return 0;
        }
        else
        {
            boolean flag = p_177077_9_.isGui3d();
            int i = this.getModelCount(itemstack);
            float f2 = p_177077_9_.getItemCameraTransforms().getTransform(ItemCameraTransforms.TransformType.GROUND).scale.y;
            GlStateManager.translate((float)p_177077_2_, (float)p_177077_4_ + 0.25F * f2, (float)p_177077_6_);

            if (flag || this.renderManager.options != null)
            {
                float f3 = (((float)itemIn.getAge() + p_177077_8_) / 20.0F + itemIn.hoverStart) * (180F / (float)Math.PI);
                GlStateManager.rotate(f3, 0.0F, 1.0F, 0.0F);
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            return i;
        }
    }

    protected int getModelCount(ItemStack stack)
    {
        int i = 1;

        if (stack.getCount() > 48)
        {
            i = 5;
        }
        else if (stack.getCount() > 32)
        {
            i = 4;
        }
        else if (stack.getCount() > 16)
        {
            i = 3;
        }
        else if (stack.getCount() > 1)
        {
            i = 2;
        }

        return i;
    }
}