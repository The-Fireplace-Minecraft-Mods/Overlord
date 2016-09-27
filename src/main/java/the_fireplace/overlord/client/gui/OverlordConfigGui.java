package the_fireplace.overlord.client.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import the_fireplace.overlord.Overlord;

@SideOnly(Side.CLIENT)
public class OverlordConfigGui extends GuiConfig {

    public OverlordConfigGui(GuiScreen parentScreen) {
        super(parentScreen, new ConfigElement(Overlord.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(), Overlord.MODID, false,
                false, GuiConfig.getAbridgedConfigPath(Overlord.config.toString()));
    }

}