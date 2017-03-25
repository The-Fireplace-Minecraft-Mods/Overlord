package the_fireplace.overlord.client.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.config.ConfigValues;
import the_fireplace.overlord.entity.EntitySkeletonWarrior;
import the_fireplace.overlord.tools.SkinTools;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * @author The_Fireplace
 */
@SuppressWarnings("unchecked")
public class LayerSkinsuit implements LayerRenderer<EntitySkeletonWarrior> {
    private final RenderLivingBase<?> renderer;
    private ModelSkeletonWarrior model;
    private boolean nospam = false;

    public LayerSkinsuit(RenderLivingBase<?> renderer)
    {
        this.renderer = renderer;
        this.model = new ModelSkeletonWarrior(0.25F, true, true, 2);
    }

    @Override
    public void doRenderLayer(EntitySkeletonWarrior skeleton, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
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
                    boolean flag = false;
                    if(!skinFile.exists())
                        if(!SkinTools.nonexistants.contains(skinFile)) {
                            if (!SkinTools.cacheSkin(skeleton.getSkinsuitName())) {
                                SkinTools.nonexistants.add(skinFile);
                                flag = true;
                            }
                        }else{
                            flag = true;
                        }
                    if(!flag) {
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
                        if(!nospam) {
                            Overlord.logInfo("Spammy error:");
                            Overlord.logError(e.getLocalizedMessage());
                            Overlord.logInfo("This is most likely repeatedly happening, but has only been logged once.");
                            nospam = true;
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
