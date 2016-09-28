package the_fireplace.overlord.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.network.PacketDispatcher;
import the_fireplace.overlord.network.packets.UpdateArmyMessage;

/**
 * @author The_Fireplace
 */
public class GuiRing extends GuiScreen {
    public static final ResourceLocation texture = new ResourceLocation(Overlord.MODID, "textures/gui/ring.png");
    protected int xSize = 175;
    protected int ySize = 95;
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
        this.buttonList.add(new GuiButton(2, guiLeft+5, guiTop+20, 75, 20, "skeleton.mode.aggressive"));
        this.buttonList.add(new GuiButton(1, guiLeft+5, guiTop+44, 75, 20, "skeleton.mode.defensive"));
        this.buttonList.add(new GuiButton(0, guiLeft+5, guiTop+70, 75, 20, "skeleton.mode.passive"));
        this.buttonList.add(new GuiButton(3, guiLeft+84, guiTop+20, 75, 20, "skeleton.mode.stationed"));
        this.buttonList.add(new GuiButton(4, guiLeft+84, guiTop+44, 75, 20, "skeleton.mode.follower"));
        this.buttonList.add(new GuiButton(5, guiLeft+84, guiTop+70, 75, 20, "skeleton.mode.base"));
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
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.enabled) {
            PacketDispatcher.sendToServer(new UpdateArmyMessage(button.id));
        }
    }
}
