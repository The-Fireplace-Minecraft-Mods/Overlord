package the_fireplace.overlord.client;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.io.IOUtils;
import the_fireplace.overlord.Overlord;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * @author The_Fireplace
 */
@Mod.EventBusSubscriber(Side.CLIENT)
public final class ClientEvents {
	private static Random rand = new Random();
	private static final ResourceLocation SPLASH_TEXTS = new ResourceLocation("texts/splashes.txt");
	public static int splashOffsetCount = 0;
	public static final int finalSplashOffsetCount;
	private static final List<String> mySplashes = Lists.newArrayList(
			"Milk is good for the skeletons!",
			"Spooked solid!",
			"Do you have a moment to talk about our lord and savior, Skeletor?"
	);

	static {
		if (Calendar.getInstance().get(Calendar.MONTH) == Calendar.OCTOBER && Calendar.getInstance().get(Calendar.DATE) == 31)
			mySplashes.add("Skeletons dressed up as humans");
		if (Calendar.getInstance().get(Calendar.MONTH) == Calendar.SEPTEMBER && Calendar.getInstance().get(Calendar.DATE) == 15)
			mySplashes.add("now my brother, papyrus...");
		splashOffsetCount += mySplashes.size();

		//Using this system allows other mods using the system to know how many mod-added splashes there are. Not perfect, but Forge doesn't have a system in place, so this will have to do.
		try{
			File file = new File(".splashes");
			if(file.exists()) {
				byte[] encoded = Files.readAllBytes(file.toPath());
				try {
					splashOffsetCount += Integer.parseInt(new String(encoded, "UTF-8"));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
				if(!file.delete())
					Overlord.logWarn("Splashes file could not be deleted");
			}
			file.createNewFile();
			file.deleteOnExit();
			FileWriter fw = new FileWriter(file);
			fw.write(String.valueOf(splashOffsetCount));
			fw.close();
		}catch(IOException e){
			Overlord.logWarn(e.getLocalizedMessage());
		}
		finalSplashOffsetCount = splashOffsetCount;
	}

	@SubscribeEvent
	public static void screenload(GuiScreenEvent.InitGuiEvent event) {
		if (event.getGui() instanceof GuiMainMenu) {
			IResource iresource = null;
			try {
				List<String> defaultSplashes = Lists.newArrayList();
				iresource = Minecraft.getMinecraft().getResourceManager().getResource(SPLASH_TEXTS);
				BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8));
				String s;

				while ((s = bufferedreader.readLine()) != null) {
					s = s.trim();

					if (!s.isEmpty()) {
						defaultSplashes.add(s);
					}
				}

				int splashNum = rand.nextInt(defaultSplashes.size() + finalSplashOffsetCount);

				if (splashNum >= defaultSplashes.size()+finalSplashOffsetCount-mySplashes.size())
					ReflectionHelper.setPrivateValue(GuiMainMenu.class, (GuiMainMenu) event.getGui(), mySplashes.get(splashNum - (defaultSplashes.size()+finalSplashOffsetCount-mySplashes.size())), "splashText", "field_73975_c");
			} catch (IOException e) {
				Overlord.logWarn(e.getLocalizedMessage());
			} finally {
				IOUtils.closeQuietly(iresource);
			}
		}
	}

	@SubscribeEvent
	public static void modelRegister(ModelRegistryEvent e){
		Overlord.registerItemRenders();
	}
}
