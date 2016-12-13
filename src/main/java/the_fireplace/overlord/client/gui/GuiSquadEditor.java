package the_fireplace.overlord.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import the_fireplace.overlord.network.PacketDispatcher;
import the_fireplace.overlord.network.packets.UpdateSquadsMessage;
import the_fireplace.overlord.tools.Squads;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * @author The_Fireplace
 */
public class GuiSquadEditor extends GuiScreen {
    public static final ResourceLocation texture = new ResourceLocation("textures/gui/book.png");
    protected int xSize = 146;
    protected int ySize = 180;
    protected int guiLeft;
    protected int guiTop;
    private String uuid;

    private GuiTextField one;
    private GuiTextField two;
    private GuiTextField three;
    private GuiTextField four;
    private GuiTextField five;

    public GuiSquadEditor(String uuid){
        guiLeft = (width - xSize) / 2;
        guiTop = (height - ySize) / 2;
        this.uuid=uuid;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        width = res.getScaledWidth();
        height = res.getScaledHeight();
        guiLeft = (width - xSize) / 2;
        guiTop = (height - ySize) / 2;
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, guiLeft+8, guiTop+144, 60, 20, I18n.format("gui.cancel")));
        this.buttonList.add(new GuiButton(1, guiLeft+73, guiTop+144, 60, 20, I18n.format("gui.save")));
        one = new GuiTextField(2, fontRendererObj, guiLeft+15, guiTop+20, 120, 20);
        two = new GuiTextField(3, fontRendererObj, guiLeft+15, guiTop+43, 120, 20);
        three = new GuiTextField(4, fontRendererObj, guiLeft+15, guiTop+66, 120, 20);
        four = new GuiTextField(5, fontRendererObj, guiLeft+15, guiTop+89, 120, 20);
        five = new GuiTextField(6, fontRendererObj, guiLeft+15, guiTop+112, 120, 20);
        ArrayList<String> squads = Squads.getInstance().getSquadsFor(UUID.fromString(uuid));
        if(!squads.isEmpty()){
            if(squads.size() > 0)
                one.setText(squads.get(0));
            if(squads.size() > 1)
                two.setText(squads.get(1));
            if(squads.size() > 2)
                three.setText(squads.get(2));
            if(squads.size() > 3)
                four.setText(squads.get(3));
            if(squads.size() > 4)
                five.setText(squads.get(4));
        }
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 20, 1, this.xSize, this.ySize);
        this.drawCenteredString(fontRendererObj, I18n.format("overlord.squad_editor"), guiLeft+xSize/2, guiTop+8, 0);
        one.drawTextBox();
        two.drawTextBox();
        three.drawTextBox();
        four.drawTextBox();
        five.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void drawCenteredString(FontRenderer fontRendererIn, String text, int x, int y, int color)
    {
        fontRendererIn.drawString(text, (x - fontRendererIn.getStringWidth(text) / 2), y, color);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.enabled) {
            if(button.id == 0){
                this.mc.player.closeScreen();
            }
            if(button.id == 1){
                ArrayList<String> names = new ArrayList();
                names.add(one.getText());
                names.add(two.getText());
                names.add(three.getText());
                names.add(four.getText());
                names.add(five.getText());
                PacketDispatcher.sendToServer(new UpdateSquadsMessage(names));
                this.mc.player.closeScreen();
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame(){
        return false;
    }

    @Override
    public void updateScreen()
    {
        this.one.updateCursorCounter();
        this.two.updateCursorCounter();
        this.three.updateCursorCounter();
        this.four.updateCursorCounter();
        this.five.updateCursorCounter();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        one.mouseClicked(mouseX, mouseY, mouseButton);
        two.mouseClicked(mouseX, mouseY, mouseButton);
        three.mouseClicked(mouseX, mouseY, mouseButton);
        four.mouseClicked(mouseX, mouseY, mouseButton);
        five.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void keyTyped(char character, int keyIndex) {
         if (keyIndex == Keyboard.KEY_BACK) {
            if (one.isFocused() && one.getText().length() > 0) {
                one.setText(one.getText().substring(0, one.getText().length() - 1));
            } else if (two.isFocused() && two.getText().length() > 0) {
                two.setText(two.getText().substring(0, two.getText().length() - 1));
            } else if (three.isFocused() && three.getText().length() > 0) {
                three.setText(three.getText().substring(0, three.getText().length() - 1));
            } else if (four.isFocused() && four.getText().length() > 0) {
                four.setText(four.getText().substring(0, four.getText().length() - 1));
            } else if (five.isFocused() && five.getText().length() > 0) {
                five.setText(five.getText().substring(0, five.getText().length() - 1));
            }
        } else if (character != 0 && Character.isLetter(character)) {
            if (one.isFocused() && one.getText().length() < 11) {
                one.setText(one.getText() + character);
            } else if (two.isFocused() && two.getText().length() < 11) {
                two.setText(two.getText() + character);
            } else if (three.isFocused() && three.getText().length() < 11) {
                three.setText(three.getText() + character);
            } else if (four.isFocused() && four.getText().length() < 11) {
                four.setText(four.getText() + character);
            } else if (five.isFocused() && four.getText().length() < 11) {
                five.setText(five.getText() + character);
            }
        }
    }
}
