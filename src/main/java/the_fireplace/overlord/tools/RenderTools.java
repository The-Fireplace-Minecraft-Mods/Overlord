package the_fireplace.overlord.tools;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

/**
 * @author The_Fireplace
 */
@SideOnly(Side.CLIENT)
public final class RenderTools {
	private static Random random = new Random();
	private static EntityItem entityItem;

	public static void renderItemStackOnEntity(Entity targetEntity, ItemStack stack, Render render, float partialTicks, double x, double y, double z, double offsetX, double offsetY, double offsetZ) {
		if (entityItem == null || entityItem.world != targetEntity.world) {
			entityItem = new EntityItem(targetEntity.world, x, y, z, stack);
			entityItem.setNoDespawn();
		}
		entityItem.setLocationAndAngles(x, y, z, targetEntity.rotationYaw, targetEntity.rotationPitch);
		entityItem.setItem(stack);

		ItemStack itemstack = entityItem.getItem();
		int i = itemstack.isEmpty() ? 187 : Item.getIdFromItem(itemstack.getItem()) + itemstack.getMetadata();
		random.setSeed((long) i);

		render.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		render.getRenderManager().renderEngine.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);

		GlStateManager.enableRescaleNormal();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.enableBlend();
		RenderHelper.enableStandardItemLighting();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.pushMatrix();
		IBakedModel ibakedmodel = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(itemstack, entityItem.world, null);
		int j = transformModelCount(entityItem, x, y, z, partialTicks, ibakedmodel, render.getRenderManager());
		boolean flag1 = ibakedmodel.isGui3d();

		if (!flag1) {
			float f3 = -0.0F * (float) (j - 1) * 0.5F;
			float f4 = -0.0F * (float) (j - 1) * 0.5F;
			float f5 = -0.09375F * (float) (j - 1) * 0.5F;
			GlStateManager.translate(f3, f4, f5);
		}

		GlStateManager.translate(offsetX, offsetY, offsetZ);

		for (int k = 0; k < j; ++k) {
			if (flag1) {
				GlStateManager.pushMatrix();

				if (k > 0) {
					float f7 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
					float f9 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
					float f6 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
					GlStateManager.translate(f7, f9, f6);
				}

				ibakedmodel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(ibakedmodel, ItemCameraTransforms.TransformType.GROUND, false);
				Minecraft.getMinecraft().getRenderItem().renderItem(itemstack, ibakedmodel);
				GlStateManager.popMatrix();
			} else {
				GlStateManager.pushMatrix();

				if (k > 0) {
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

		GlStateManager.popMatrix();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableBlend();
		render.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		render.getRenderManager().renderEngine.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
	}

	public static void renderItemStackOverEntity(Entity targetEntity, ItemStack stack, Render render, float partialTicks, double x, double y, double z) {
		renderItemStackOnEntity(targetEntity, stack, render, partialTicks, x, y, z, 0, targetEntity.height + 0.1F, 0);
	}

	private static int transformModelCount(EntityItem itemIn, double trX, double trY, double trZ, float partialTicks, IBakedModel model, RenderManager rm) {
		ItemStack itemstack = itemIn.getItem();
		Item item = itemstack.getItem();

		if (item == null) {
			return 0;
		} else {
			boolean flag = model.isGui3d();
			float f2 = model.getItemCameraTransforms().getTransform(ItemCameraTransforms.TransformType.GROUND).scale.y;
			GlStateManager.translate((float) trX, (float) trY + 0.25F * f2, (float) trZ);

			if (flag || rm.options != null) {
				float f3 = (((float) itemIn.getAge() + partialTicks) / 20.0F + itemIn.hoverStart) * (180F / (float) Math.PI);
				GlStateManager.rotate(f3, 0.0F, 1.0F, 0.0F);
			}

			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			return 1;
		}
	}
}
