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
import the_fireplace.overlord.network.packets.RequestAugmentMessage;
import the_fireplace.overlord.network.packets.SetSquadMessage;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author The_Fireplace
 */
public class GuiSkeleton extends GuiContainer {
    public static final ResourceLocation texture = new ResourceLocation(Overlord.MODID, "textures/gui/skeleton.png");
    private EntitySkeletonWarrior entity;

    private GuiButton attackMode;
    private byte attackModeTimer;
    private GuiButton movementMode;
    private byte movementModeTimer;
    private ArrayList<String> squads;
    private int squadIndex;

    public GuiSkeleton(InventoryPlayer inventorySlotsIn, EntitySkeletonWarrior warrior, ArrayList<String> squads) {
        super(new ContainerSkeleton(inventorySlotsIn, warrior));
        this.entity=warrior;
        xSize = 175;
        ySize = 165;
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        width = res.getScaledWidth();
        height = res.getScaledHeight();
        guiLeft = (width - xSize) / 2;
        guiTop = (height - ySize) / 2;
        this.squads = squads;
        squadIndex = squads.indexOf(warrior.getSquad());
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
        this.buttonList.add(new GuiButton(2, guiLeft+25, guiTop+23, 20, 20, "<-"));
        this.buttonList.add(new GuiButton(3, guiLeft+94, guiTop+23, 20, 20, "->"));
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
            }else if(button.id == 2){
                if(squadIndex < 0)
                    squadIndex = squads.size() - 1;
                else
                    squadIndex--;
            }else if(button.id == 3){
                if(squadIndex >= squads.size() - 1)
                    squadIndex = -1;
                else
                    squadIndex++;
            }
            if(squadIndex > -1)
                PacketDispatcher.sendToServer(new SetSquadMessage(entity, squads.get(squadIndex)));
            else
                PacketDispatcher.sendToServer(new SetSquadMessage(entity, ""));
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
        this.drawTexturedModalRect(guiLeft+47, guiTop+8, 0, 166, (int)(entity.getHealth()/entity.getMaxHealth()*90), 5);
        this.drawTexturedModalRect(guiLeft+47, guiTop+18, 0, 171, (int)(entity.getXP()/Math.pow(2, entity.getLevel())*90), 5);
    }


    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.drawCenteredString(fontRenderer, Math.round(entity.getHealth())+"/"+Math.round(entity.getMaxHealth()), 90, 4, Color.RED.getRGB());
        this.drawCenteredString(fontRenderer, String.valueOf(entity.getLevel()), 90, 20, Color.GREEN.getRGB());
        this.drawCenteredString(fontRenderer, squadIndex != -1 ? squads.get(squadIndex) : I18n.format("overlord.no_squad"), 70, 30, -1);
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
        if(!this.entity.isEntityAlive() || this.entity.isDead)
            this.mc.player.closeScreen();
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

    @Override
    public void onGuiClosed(){
        if(entity.world.isRemote)
            PacketDispatcher.sendToServer(new RequestAugmentMessage(entity));
        super.onGuiClosed();
    }
}
