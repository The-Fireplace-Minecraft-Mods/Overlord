package the_fireplace.overlord.tools;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import the_fireplace.overlord.Overlord;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author The_Fireplace
 */
@SideOnly(Side.CLIENT)
public final class SkinTools {
	public static final File cachedir = new File(Minecraft.getMinecraft().mcDataDir, "cachedImages/skins/");
	public static final HashMap<String, BufferedImage> skins = Maps.newHashMap();
	public static final HashMap<BufferedImage, DynamicTexture> skintextures = Maps.newHashMap();
	public static final ArrayList<File> nonexistants = Lists.newArrayList();

	public static boolean cacheSkin(String username) {
		Overlord.logTrace("Attempting to cache skin for " + username);
		try {
			File file = new File(cachedir, username + ".png");
			URL url = new URL(String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", username));
			InputStream is = url.openStream();
			if (file.exists())
				file.delete();
			file.createNewFile();
			OutputStream os = new FileOutputStream(file);

			byte[] b = new byte[2048];
			int length;

			while ((length = is.read(b)) != -1) {
				os.write(b, 0, length);
			}
			is.close();
			os.close();
			BufferedImage skin = ImageIO.read(file);
			if (skin.getHeight() < 63)
				convertOldSkin(file);
			return true;
		} catch (Exception e) {
			Overlord.logWarn(e.getLocalizedMessage());
			return false;
		}
	}

	public static void convertOldSkin(File file) throws IOException {
		Overlord.logTrace("Converting " + file + " to 64x64 skin.");
		BufferedImage newSkin = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
		BufferedImage original = new BufferedImage(64, 32, BufferedImage.TYPE_INT_ARGB);
		BufferedImage current = ImageIO.read(file);
		original.getGraphics().drawImage(current, 0, 0, 64, 32, 0, 0, 64, 32, null);
		newSkin.getGraphics().drawImage(original, 0, 0, 64, 64, 0, 0, 64, 64, null);
		BufferedImage[] skinparts = skinParts(current);
		newSkin.getGraphics().drawImage(skinparts[0], 0, 0, 64, 64, -32, -52, (-32) + (64), (-52) + (64), null);
		newSkin.getGraphics().drawImage(skinparts[1], 0, 0, 64, 64, -44, -52, (-44) + (64), (-52) + (64), null);
		newSkin.getGraphics().drawImage(skinparts[2], 0, 0, 64, 64, -36, -48, (-36) + (64), (-48) + (64), null);
		newSkin.getGraphics().drawImage(skinparts[3], 0, 0, 64, 64, -40, -48, -40 + 64, -48 + 64, null);
		newSkin.getGraphics().drawImage(skinparts[4], 0, 0, 64, 64, -16, -52, -16 + 64, -52 + 64, null);
		newSkin.getGraphics().drawImage(skinparts[5], 0, 0, 64, 64, -28, -52, -28 + 64, -52 + 64, null);
		newSkin.getGraphics().drawImage(skinparts[6], 0, 0, 64, 64, -20, -48, -20 + 64, -48 + 64, null);
		newSkin.getGraphics().drawImage(skinparts[7], 0, 0, 64, 64, -24, -48, -24 + 64, -48 + 64, null);
		ImageIO.write(newSkin, "PNG", file);
	}

	public static BufferedImage[] skinParts(BufferedImage inputSkin) {
		BufferedImage arm_front = new BufferedImage(12, 12, BufferedImage.TYPE_INT_ARGB);
		arm_front.getGraphics().drawImage(inputSkin, 0, 0, 12, 12, 40, 20, (40) + (12), (20) + (12), null);
		AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
		tx.translate(-arm_front.getWidth(null), 0);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		arm_front = op.filter(arm_front, null);

		BufferedImage arm_back = new BufferedImage(4, 12, BufferedImage.TYPE_INT_ARGB);
		arm_back.getGraphics().drawImage(inputSkin, 0, 0, 4, 12, 52, 20, (52) + (4), (20) + (12), null);
		AffineTransform txab = AffineTransform.getScaleInstance(-1, 1);
		txab.translate(-arm_back.getWidth(null), 0);
		AffineTransformOp opab = new AffineTransformOp(txab, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		arm_back = opab.filter(arm_back, null);

		BufferedImage arm_top = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);
		arm_top.getGraphics().drawImage(inputSkin, 0, 0, 4, 4, 44, 16, (44) + (4), (16) + (4), null);
		AffineTransform txat = AffineTransform.getScaleInstance(-1, 1);
		txat.translate(-arm_top.getWidth(null), 0);
		AffineTransformOp opat = new AffineTransformOp(txat, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		arm_top = opat.filter(arm_top, null);

		BufferedImage arm_bottom = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);
		arm_bottom.getGraphics().drawImage(inputSkin, 0, 0, 4, 4, 48, 16, (48) + (4), (16) + (4), null);
		arm_bottom = opat.filter(arm_bottom, null);

		BufferedImage leg_front = new BufferedImage(12, 12, BufferedImage.TYPE_INT_ARGB);
		leg_front.getGraphics().drawImage(inputSkin, 0, 0, 12, 12, 0, 20, 12, (20) + (12), null);
		leg_front = op.filter(leg_front, null);

		BufferedImage leg_back = new BufferedImage(4, 12, BufferedImage.TYPE_INT_ARGB);
		leg_back.getGraphics().drawImage(inputSkin, 0, 0, 4, 12, 12, 20, (12) + (4), (20) + (12), null);
		leg_back = opab.filter(leg_back, null);

		BufferedImage leg_top = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);
		leg_top.getGraphics().drawImage(inputSkin, 0, 0, 4, 4, 4, 16, (4) + (4), (16) + (4), null);
		leg_top = opat.filter(leg_top, null);

		BufferedImage leg_bottom = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);
		leg_bottom.getGraphics().drawImage(inputSkin, 0, 0, 4, 4, 8, 16, (8) + (4), (16) + (4), null);
		leg_bottom = opab.filter(leg_bottom, null);

		return new BufferedImage[]{arm_front, arm_back, arm_top, arm_bottom, leg_front, leg_back, leg_top, leg_bottom};
	}
}
