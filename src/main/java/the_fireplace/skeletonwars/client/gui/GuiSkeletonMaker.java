package the_fireplace.skeletonwars.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import the_fireplace.skeletonwars.SkeletonWars;
import the_fireplace.skeletonwars.container.ContainerSkeletonMaker;
import the_fireplace.skeletonwars.network.CreateSkeletonMessage;
import the_fireplace.skeletonwars.network.PacketDispatcher;
import the_fireplace.skeletonwars.tileentity.TileEntitySkeletonMaker;

/**
 * @author The_Fireplace
 */
public class GuiSkeletonMaker extends GuiContainer {
    public static final ResourceLocation texture = new ResourceLocation(SkeletonWars.MODID, "textures/gui/skeleton_maker.png");
    private TileEntitySkeletonMaker te;

    private GuiButton createSkeleton;

    public GuiSkeletonMaker(InventoryPlayer invPlayer, TileEntitySkeletonMaker entity) {
        super(new ContainerSkeletonMaker(invPlayer, entity));
        xSize = 175;
        ySize = 165;
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        width = res.getScaledWidth();
        height = res.getScaledHeight();
        guiLeft = (width - xSize) / 2;
        guiTop = (height - ySize) / 2;
        te = entity;
    }

    @Override
    public void initGui() {
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        width = res.getScaledWidth();
        height = res.getScaledHeight();
        guiLeft = (width - xSize) / 2;
        guiTop = (height - ySize) / 2;
        this.buttonList.clear();
        this.buttonList.add(createSkeleton = new GuiButton(0, guiLeft+49, guiTop+61, 60, 18, I18n.format("skeleton_maker.create")));
        createSkeleton.enabled=false;
        super.initGui();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
        if(te.getMilk() > 0){
            int j = te.getMilk();
            this.drawTexturedModalRect(guiLeft + 121, guiTop + (j == 1 ? 56 : 33), 176, j == 1 ? 23 : 0, 28, j == 1 ? 23 : 46);
        }
    }

    @Override
    public void updateScreen()
    {
        createSkeleton.enabled = isButtonEnabled();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.enabled) {
            if(button.id == 0){
                PacketDispatcher.sendToServer(new CreateSkeletonMessage(te.getPos()));
            }
        }
    }

    private boolean isButtonEnabled(){
        if(te.getStackInSlot(1) == null || te.getStackInSlot(2) == null || te.getStackInSlot(3) == null)
            return false;
        if(te.getMilk() < 2)
            return false;
        if(te.getStackInSlot(1).stackSize < 64 || te.getStackInSlot(2).stackSize < 64 || te.getStackInSlot(3).stackSize < 64)
            return false;
        return true;
    }
}
