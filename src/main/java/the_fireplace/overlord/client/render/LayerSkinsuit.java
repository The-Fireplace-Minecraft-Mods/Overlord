package the_fireplace.overlord.client.render;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.entity.EntityLivingBase;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.config.ConfigValues;
import the_fireplace.overlord.tools.ISkinsuitWearer;
import the_fireplace.overlord.tools.SkinTools;
import the_fireplace.overlord.tools.SkinType;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * @author The_Fireplace
 */
@SuppressWarnings("unchecked")
public class LayerSkinsuit implements LayerRenderer<EntityLivingBase> {
	private final RenderLivingBase<?> renderer;
	private ModelBiped model;
	private boolean logErr = true;

	public LayerSkinsuit(RenderLivingBase<?> renderer, ModelBiped model) {
		this.renderer = renderer;
		this.model = model;
	}

	@Override
	public void doRenderLayer(@Nonnull EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		if (!(entity instanceof ISkinsuitWearer))
			return;
		ISkinsuitWearer skin = (ISkinsuitWearer) entity;
		if (!skin.getSkinType().isNone()) {
			GlStateManager.enableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
			this.model.setModelAttributes(this.renderer.getMainModel());
			this.model.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);
			if (skin.getSkinType().equals(SkinType.PLAYER)) {
				if (!skin.getSkinName().isEmpty()) {
					try {
						if (!SkinTools.cachedir.exists())
							if (!SkinTools.cachedir.mkdirs())
								Overlord.logError("Skin cache directory creation failed.");
						File skinFile = new File(SkinTools.cachedir, skin.getSkinName() + ".png");
						boolean skinExists = true;
						if (!skinFile.exists())
							if (!SkinTools.nonexistants.contains(skinFile)) {
								if (!SkinTools.cacheSkin(skin.getSkinName())) {
									SkinTools.nonexistants.add(skinFile);
									skinExists = false;
								}
							} else {
								skinExists = false;
							}
						if (skinExists) {
							BufferedImage img;
							if (SkinTools.skins.get(skin.getSkinName()) != null)
								img = SkinTools.skins.get(skin.getSkinName());
							else {
								img = ImageIO.read(skinFile);
								SkinTools.skins.put(skin.getSkinName(), img);
							}
							//if (((img.getRGB(54, 21) >> 24) & 0xff) == 0)
								//TODO: Small arms
							DynamicTexture texture;
							if (SkinTools.skintextures.get(img) != null)
								texture = SkinTools.skintextures.get(img);
							else {
								texture = new DynamicTexture(img);
								SkinTools.skintextures.put(img, texture);
							}
							this.renderer.bindTexture(this.renderer.getRenderManager().renderEngine.getDynamicTextureLocation(Overlord.MODID, texture));
						} else {
							this.renderer.bindTexture(skin.getSkinType().getTexture());
						}
					} catch (Exception e) {
						this.renderer.bindTexture(skin.getSkinType().getTexture());
						if (logErr) {
							Overlord.logInfo("Spammy error:");
							Overlord.logError(e.getLocalizedMessage());
							Overlord.logInfo("This is most likely repeatedly happening, but has only been logged once.");
							logErr = false;
						}
					}
				} else
					this.renderer.bindTexture(skin.getSkinType().getTexture());
			} else
				this.renderer.bindTexture(skin.getSkinType().getTexture());
			if (ConfigValues.GHOSTLYSKINS) {
				GlStateManager.enableBlend();
				GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
			}
			model.render(entity, limbSwing, limbSwingAmount, partialTicks, netHeadYaw, headPitch, scale);
			if (ConfigValues.GHOSTLYSKINS) {
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
