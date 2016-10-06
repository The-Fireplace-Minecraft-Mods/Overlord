package the_fireplace.overlord.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.entity.EntitySkeletonWarrior;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * @author The_Fireplace
 */
public class LayerSkinsuit implements LayerRenderer<EntitySkeletonWarrior> {
    public static final File cachedir = new File(Minecraft.getMinecraft().mcDataDir, "cachedImages/skins/");
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
            this.model.setModelAttributes(this.renderer.getMainModel());
            this.model.setLivingAnimations(skeleton, limbSwing, limbSwingAmount, partialTicks);
            if(!skeleton.getSkinsuitName().equals("")) {
                try {
                    DynamicTexture texture = new DynamicTexture(ImageIO.read(new File(cachedir.getCanonicalPath(), skeleton.getSkinsuitName()+".png")));
                    this.renderer.bindTexture(this.renderer.getRenderManager().renderEngine.getDynamicTextureLocation(Overlord.MODID, texture));
                }catch(Exception e){
                    try{
                        cacheSkin(skeleton.getSkinsuitName());
                        DynamicTexture texture = new DynamicTexture(ImageIO.read(new File(cachedir.getCanonicalPath(), skeleton.getSkinsuitName()+".png")));
                        this.renderer.bindTexture(this.renderer.getRenderManager().renderEngine.getDynamicTextureLocation(Overlord.MODID, texture));
                    }catch(Exception e2){
                        this.renderer.bindTexture(new ResourceLocation("textures/entity/steve.png"));
                        if(!nospam)
                            e2.printStackTrace();
                        nospam = true;
                    }
                }
            }else
                this.renderer.bindTexture(new ResourceLocation("textures/entity/steve.png"));
            GlStateManager.enableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
            model.render(skeleton, limbSwing, limbSwingAmount, partialTicks, netHeadYaw, headPitch, scale);
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }

    public static void cacheSkin(String username) throws Exception{
        if(!cachedir.exists())
            if(!cachedir.mkdirs())
                System.out.println("Skin cache directory creation failed.");
        File file = new File(cachedir, username+".png");
        URL url = new URL(String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", username));
        InputStream is = url.openStream();
        if(file.exists())
            file.delete();
        file.createNewFile();
        OutputStream os = new FileOutputStream(file);

        byte[] b = new byte[2048];
        int length;

        while((length = is.read(b)) != -1){
            os.write(b, 0, length);
        }
        is.close();
        os.close();
    }
}
