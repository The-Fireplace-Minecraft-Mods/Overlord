package the_fireplace.overlord.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.network.PacketDispatcher;
import the_fireplace.overlord.network.packets.UpdateArmyMessage;

import java.awt.*;

/**
 * @author The_Fireplace
 */
public class GuiRing extends GuiScreen {
    public static final ResourceLocation texture = new ResourceLocation(Overlord.MODID, "textures/gui/ring.png");
    protected int xSize = 176;
    protected int ySize = 96;
    protected int guiLeft;
    protected int guiTop;

    public GuiRing(){
        guiLeft = (width - xSize) / 2;
        guiTop = (height - ySize) / 2;
    }

    @Override
    public void initGui() {
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        width = res.getScaledWidth();
        height = res.getScaledHeight();
        guiLeft = (width - xSize) / 2;
        guiTop = (height - ySize) / 2;
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(2, guiLeft+5, guiTop+22, 81, 20, I18n.format("skeleton.mode.aggressive")));
        this.buttonList.add(new GuiButton(1, guiLeft+5, guiTop+46, 81, 20, I18n.format("skeleton.mode.defensive")));
        this.buttonList.add(new GuiButton(0, guiLeft+5, guiTop+70, 81, 20, I18n.format("skeleton.mode.passive")));
        this.buttonList.add(new GuiButton(3, guiLeft+90, guiTop+22, 81, 20, I18n.format("skeleton.mode.stationed")));
        this.buttonList.add(new GuiButton(4, guiLeft+90, guiTop+46, 81, 20, I18n.format("skeleton.mode.follower")));
        this.buttonList.add(new GuiButton(5, guiLeft+90, guiTop+70, 81, 20, I18n.format("skeleton.mode.base")));
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
        this.drawCenteredString(fontRendererObj, I18n.format("overlord.attack_modes"), guiLeft+45, guiTop+12, -1);
        this.drawCenteredString(fontRendererObj, I18n.format("overlord.movement_modes"), guiLeft+130, guiTop+12, -1);
        this.drawCenteredString(fontRendererObj, I18n.format("overlords_seal.warning"), guiLeft+(xSize/2), guiTop-10, Color.PINK.getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.enabled) {
            PacketDispatcher.sendToServer(new UpdateArmyMessage(button.id));
        }
    }

    @Override
    public boolean doesGuiPauseGame(){
        return false;
    }
}
