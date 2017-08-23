package the_fireplace.overlord.client.gui;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.config.ConfigValues;
import the_fireplace.overlord.container.ContainerSkeletonMaker;
import the_fireplace.overlord.network.PacketDispatcher;
import the_fireplace.overlord.network.packets.CreateSkeletonMessage;
import the_fireplace.overlord.tileentity.TileEntitySkeletonMaker;

import java.awt.*;
import java.util.UUID;

/**
 * @author The_Fireplace
 */
public class GuiSkeletonMaker extends GuiContainer {
	public static final ResourceLocation texture = new ResourceLocation(Overlord.MODID, "textures/gui/skeleton_maker.png");
	public static final ResourceLocation overlords_seal_texture = new ResourceLocation(Overlord.MODID, "textures/items/overlords_seal.png");
	public static final ResourceLocation milk_texture = new ResourceLocation("textures/items/bucket_milk.png");
	public static final ResourceLocation skinsuit_texture = new ResourceLocation(Overlord.MODID, "textures/items/skinsuit.png");
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
		playerUsing = invPlayer.player;
	}

	@Override
	public void initGui() {
		ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
		width = res.getScaledWidth();
		height = res.getScaledHeight();
		guiLeft = (width - xSize) / 2;
		guiTop = (height - ySize) / 2;
		this.buttonList.clear();
		this.buttonList.add(createSkeleton = new GuiButton(0, guiLeft + 49, guiTop + 60, 60, 20, I18n.format("skeleton_maker.create")));
		createSkeleton.enabled = false;
		super.initGui();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GL11.glColor4f(1F, 1F, 1F, 1F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
		if (te.getMilk() > 0) {
			int j = te.getMilk();
			this.drawTexturedModalRect(guiLeft + 121, guiTop + (j == 1 ? 56 : 33), 176, j == 1 ? 23 : 0, 28, j == 1 ? 23 : 46);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		this.drawCenteredString(fontRenderer, getWarning(), xSize / 2, -10, Color.PINK.getRGB());
		if (te.getStackInSlot(0).isEmpty()) {
			GlStateManager.enableBlend();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 0.25F);
			Minecraft.getMinecraft().getTextureManager().bindTexture(overlords_seal_texture);
			drawModalRectWithCustomSizedTexture(96, 6, 0, 0, 16, 16, 16, 16);
			GlStateManager.resetColor();
			GlStateManager.disableBlend();
		}
		if (te.getStackInSlot(4).isEmpty()) {
			GlStateManager.enableBlend();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 0.25F);
			Minecraft.getMinecraft().getTextureManager().bindTexture(milk_texture);
			drawModalRectWithCustomSizedTexture(154, 34, 0, 0, 16, 16, 16, 16);
			GlStateManager.resetColor();
			GlStateManager.disableBlend();
		}
		if (te.getStackInSlot(12).isEmpty()) {
			GlStateManager.enableBlend();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 0.25F);
			Minecraft.getMinecraft().getTextureManager().bindTexture(skinsuit_texture);
			drawModalRectWithCustomSizedTexture(6, 6, 0, 0, 16, 16, 16, 16);
			GlStateManager.resetColor();
			GlStateManager.disableBlend();
		}
		if (ConfigValues.SUFFOCATIONWARNING)
			if (te.getWorld().getBlockState(te.getPos().up(2)).getMaterial() != Material.AIR)
				this.drawCenteredString(fontRenderer, I18n.format("skeleton_maker.warning.suffocation"), xSize / 2, -20, Color.PINK.getRGB());
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	public void updateScreen() {
		createSkeleton.enabled = te.canSpawnSkeleton();
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.enabled) {
			if (button.id == 0) {
				PacketDispatcher.sendToServer(new CreateSkeletonMessage(te.getPos()));
			}
		}
	}

	private String getWarning() {
		if (te.getStackInSlot(0).isEmpty()) {
			return I18n.format("skeleton_maker.warning.unclaimed");
		} else {
			if (te.getStackInSlot(0).getTagCompound() == null) {
				return I18n.format("skeleton_maker.warning.unclaimed");
			} else {
				if (te.getStackInSlot(0).getTagCompound().getString("Owner").isEmpty()) {
					return I18n.format("skeleton_maker.warning.unclaimed");
				} else {
					EntityPlayer player = te.getWorld().getPlayerEntityByUUID(UUID.fromString(te.getStackInSlot(0).getTagCompound().getString("Owner")));
					if (player != null && player.equals(playerUsing)) {
						return "";
					} else {
						return I18n.format("skeleton_maker.warning.notmine", te.getStackInSlot(0).getTagCompound().getString("OwnerName"));
					}
				}
			}
		}
	}
}
