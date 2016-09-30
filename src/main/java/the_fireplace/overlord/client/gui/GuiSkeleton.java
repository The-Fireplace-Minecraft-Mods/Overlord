package the_fireplace.overlord.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.container.ContainerSkeleton;
import the_fireplace.overlord.entity.EntitySkeletonWarrior;
import the_fireplace.overlord.network.PacketDispatcher;
import the_fireplace.overlord.network.packets.AttackModeMessage;
import the_fireplace.overlord.network.packets.MovementModeMessage;

/**
 * @author The_Fireplace
 */
public class GuiSkeleton extends GuiContainer {//TODO: Add health and milk bars, both styled like the player XP bar.
    public static final ResourceLocation texture = new ResourceLocation(Overlord.MODID, "textures/gui/skeleton.png");
    private EntitySkeletonWarrior entity;

    private GuiButton attackMode;
    private byte attackModeTimer;
    private GuiButton movementMode;
    private byte movementModeTimer;

    public GuiSkeleton(InventoryPlayer inventorySlotsIn, EntitySkeletonWarrior warrior) {
        super(new ContainerSkeleton(inventorySlotsIn, warrior));
        this.entity=warrior;
        xSize = 175;
        ySize = 165;
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        width = res.getScaledWidth();
        height = res.getScaledHeight();
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
        this.buttonList.add(attackMode = new GuiButton(0, guiLeft+47, guiTop+43, 66, 20, "You should not see this"));
        this.buttonList.add(movementMode = new GuiButton(1, guiLeft+47, guiTop+63, 66, 20, "You should not see this"));
        setAttackModeText();
        setMovementModeText();
        super.initGui();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.enabled) {
            if(button.id == 0){
                PacketDispatcher.sendToServer(new AttackModeMessage(entity));
                setAttackModeText();
                scheduleAttackModeTextUpdate();
            }else if(button.id == 1){
                PacketDispatcher.sendToServer(new MovementModeMessage(entity));
                setMovementModeText();
                scheduleMovementModeTextUpdate();
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
    }

    @Override
    public void updateScreen()
    {
        if(attackModeTimer > 0){
            attackModeTimer--;
        }
        if(attackModeTimer == 1){
            setAttackModeText();
        }
        if(movementModeTimer > 0){
            movementModeTimer--;
        }
        if(movementModeTimer == 1){
            setMovementModeText();
        }
        super.updateScreen();
    }

    public void setAttackModeText(){
        byte b=entity.getAttackMode();
        switch(b){
            case 0:
                attackMode.displayString=I18n.format("skeleton.mode.passive");
                break;
            case 2:
                attackMode.displayString=I18n.format("skeleton.mode.aggressive");
                break;
            case 1:
            default:
                attackMode.displayString=I18n.format("skeleton.mode.defensive");
        }
    }

    public void setMovementModeText(){
        byte b=entity.getMovementMode();
        switch(b){
            case 0:
                movementMode.displayString=I18n.format("skeleton.mode.stationed");
                break;
            case 2:
                movementMode.displayString=I18n.format("skeleton.mode.base");
                break;
            case 1:
            default:
                movementMode.displayString=I18n.format("skeleton.mode.follower");
        }
    }

    public void scheduleAttackModeTextUpdate(){
        attackModeTimer = 5;
    }

    public void scheduleMovementModeTextUpdate(){
        movementModeTimer = 5;
    }
}
