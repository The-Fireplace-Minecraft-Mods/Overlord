package the_fireplace.overlord.client;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import the_fireplace.overlord.Overlord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * @author The_Fireplace
 */
public final class ClientEvents {
	private Random rand = new Random();
	private static final ResourceLocation SPLASH_TEXTS = new ResourceLocation("texts/splashes.txt");
	private final List<String> mySplashes = Lists.newArrayList(
			"Milk is good for the skeletons!",
			"Spooked solid!",
			"#covfefe!"
	);

	public ClientEvents() {
		if (Calendar.getInstance().get(Calendar.MONTH) == Calendar.OCTOBER && Calendar.getInstance().get(Calendar.DATE) == 31)
			mySplashes.add("Skeletons dressed up as humans");
		if (Calendar.getInstance().get(Calendar.MONTH) == Calendar.SEPTEMBER && Calendar.getInstance().get(Calendar.DATE) == 15)
			mySplashes.add("now my brother, papyrus...");
		if (Loader.isModLoaded("mechsoldiers"))
			mySplashes.add("I'm sorry, Dave. I'm afraid I can't do that.");
	}

	@SubscribeEvent
	public void screenload(GuiScreenEvent.InitGuiEvent event) {
		if (event.getGui() instanceof GuiMainMenu) {
			IResource iresource = null;
			try {
				List<String> defaultSplashes = Lists.newArrayList();
				iresource = Minecraft.getMinecraft().getResourceManager().getResource(SPLASH_TEXTS);
				BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(iresource.getInputStream(), Charsets.UTF_8));
				String s;

				while ((s = bufferedreader.readLine()) != null) {
					s = s.trim();

					if (!s.isEmpty()) {
						defaultSplashes.add(s);
					}
				}

				int splashNum = rand.nextInt(defaultSplashes.size() + mySplashes.size());

				if (splashNum >= defaultSplashes.size())
					ReflectionHelper.setPrivateValue(GuiMainMenu.class, (GuiMainMenu) event.getGui(), mySplashes.get(splashNum - defaultSplashes.size()), "splashText", "field_73975_c");
			} catch (IOException e) {
				Overlord.logWarn(e.getLocalizedMessage());
			} finally {
				IOUtils.closeQuietly(iresource);
			}
		}
	}
}
