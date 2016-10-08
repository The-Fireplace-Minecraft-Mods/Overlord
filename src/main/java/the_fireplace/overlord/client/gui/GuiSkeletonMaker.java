package the_fireplace.overlord.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.container.ContainerSkeletonMaker;
import the_fireplace.overlord.network.packets.CreateSkeletonMessage;
import the_fireplace.overlord.network.PacketDispatcher;
import the_fireplace.overlord.tileentity.TileEntitySkeletonMaker;

import java.awt.*;
import java.util.UUID;

/**
 * @author The_Fireplace
 */
public class GuiSkeletonMaker extends GuiContainer {
    public static final ResourceLocation texture = new ResourceLocation(Overlord.MODID, "textures/gui/skeleton_maker.png");
    private TileEntitySkeletonMaker te;
    private EntityPlayer playerUsing;

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
        playerUsing=invPlayer.player;
    }

    @Override
    public void initGui() {
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        width = res.getScaledWidth();
        height = res.getScaledHeight();
        guiLeft = (width - xSize) / 2;
        guiTop = (height - ySize) / 2;
        this.buttonList.clear();
        this.buttonList.add(createSkeleton = new GuiButton(0, guiLeft+49, guiTop+60, 60, 20, I18n.format("skeleton_maker.create")));
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
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
        this.drawCenteredString(fontRendererObj, getWarning(), xSize/2, -10, Color.PINK.getRGB());
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

    private boolean isButtonEnabled() {
        if (te.getStackInSlot(1) == null || te.getStackInSlot(2) == null || te.getStackInSlot(3) == null)
            return false;
        return te.getMilk() >= 2 && !(te.getStackInSlot(1).stackSize < 64 || te.getStackInSlot(2).stackSize < 64 || te.getStackInSlot(3).stackSize < 64);
    }

    private String getWarning(){
        if(te.getStackInSlot(0) == null){
            return I18n.format("skeleton_maker.warning.unclaimed");
        }else{
            if(te.getStackInSlot(0).getTagCompound() == null){
                return I18n.format("skeleton_maker.warning.unclaimed");
            }else{
                if(te.getStackInSlot(0).getTagCompound().getString("Owner").isEmpty()){
                    return I18n.format("skeleton_maker.warning.unclaimed");
                }else{
                    if(te.getWorld().getPlayerEntityByUUID(UUID.fromString(te.getStackInSlot(0).getTagCompound().getString("Owner"))).equals(playerUsing)){
                        return "";
                    }else{
                        return I18n.format("skeleton_maker.warning.notmine", te.getStackInSlot(0).getTagCompound().getString("OwnerName"));
                    }
                }
            }
        }
    }
}
