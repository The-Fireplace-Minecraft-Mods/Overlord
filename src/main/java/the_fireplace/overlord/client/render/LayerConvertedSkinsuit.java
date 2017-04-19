package the_fireplace.overlord.client.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.config.ConfigValues;
import the_fireplace.overlord.entity.EntityConvertedSkeleton;
import the_fireplace.overlord.tools.SkinTools;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * @author The_Fireplace
 */
public class LayerConvertedSkinsuit implements LayerRenderer<EntityConvertedSkeleton> {
    private final RenderLivingBase<?> renderer;
    private ModelConvertedSkeleton model;
    private boolean logErr = true;

    public LayerConvertedSkinsuit(RenderLivingBase<?> renderer)
    {
        this.renderer = renderer;
        this.model = new ModelConvertedSkeleton(0.25F, true, true, 2);
    }

    @Override
    public void doRenderLayer(@Nonnull EntityConvertedSkeleton skeleton, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if(skeleton.hasSkinsuit()){
            GlStateManager.enableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
            this.model.setModelAttributes(this.renderer.getMainModel());
            this.model.setLivingAnimations(skeleton, limbSwing, limbSwingAmount, partialTicks);
            if(!skeleton.getSkinsuitName().isEmpty()) {
                try {
                    if(!SkinTools.cachedir.exists())
                        if(!SkinTools.cachedir.mkdirs())
                            Overlord.logError("Skin cache directory creation failed.");
                    File skinFile = new File(SkinTools.cachedir, skeleton.getSkinsuitName() + ".png");
                    boolean skinExists = true;
                    if(!skinFile.exists())
                        if(!SkinTools.nonexistants.contains(skinFile)) {
                            if (!SkinTools.cacheSkin(skeleton.getSkinsuitName())) {
                                SkinTools.nonexistants.add(skinFile);
                                skinExists = false;
                            }
                        }else{
                            skinExists = false;
                        }
                    if(skinExists) {
                        BufferedImage img;
                        if (SkinTools.skins.get(skeleton.getSkinsuitName()) != null)
                            img = SkinTools.skins.get(skeleton.getSkinsuitName());
                        else {
                            img = ImageIO.read(skinFile);
                            SkinTools.skins.put(skeleton.getSkinsuitName(), img);
                        }
                        if (((img.getRGB(54, 21) >> 24) & 0xff) == 0)
                            model.smallSkinsuitArms = true;
                        DynamicTexture texture;
                        if (SkinTools.skintextures.get(img) != null)
                            texture = SkinTools.skintextures.get(img);
                        else {
                            texture = new DynamicTexture(img);
                            SkinTools.skintextures.put(img, texture);
                        }
                        this.renderer.bindTexture(this.renderer.getRenderManager().renderEngine.getDynamicTextureLocation(Overlord.MODID, texture));
                    }else{
                        this.renderer.bindTexture(SkinTools.STEVE);
                    }
                }catch(Exception e){
                    this.renderer.bindTexture(SkinTools.STEVE);
                    if(logErr) {
                        Overlord.logInfo("Spammy error:");
                        Overlord.logError(e.getLocalizedMessage());
                        Overlord.logInfo("This is most likely repeatedly happening, but has only been logged once.");
                        logErr = false;
                    }
                }
            }else
                this.renderer.bindTexture(SkinTools.STEVE);
            if(ConfigValues.GHOSTLYSKINS) {
                GlStateManager.enableBlend();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
            }
            model.render(skeleton, limbSwing, limbSwingAmount, partialTicks, netHeadYaw, headPitch, scale);
            if(ConfigValues.GHOSTLYSKINS) {
                GlStateManager.resetColor();
                GlStateManager.disableBlend();
            }
            GlStateManager.disableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
    }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
