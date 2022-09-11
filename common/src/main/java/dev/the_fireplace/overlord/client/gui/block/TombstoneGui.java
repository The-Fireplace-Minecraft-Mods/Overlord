package dev.the_fireplace.overlord.client.gui.block;

import com.google.inject.Injector;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import dev.the_fireplace.lib.api.network.injectables.PacketSender;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.blockentity.TombstoneBlockEntity;
import dev.the_fireplace.overlord.network.ServerboundPackets;
import dev.the_fireplace.overlord.network.client.builder.SaveTombstoneBufferBuilder;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class TombstoneGui extends Screen
{
    private final TombstoneBlockEntity tombstone;
    private final PacketSender packetSender;
    private final ServerboundPackets serverboundPackets;
    private final SaveTombstoneBufferBuilder saveTombstoneBufferBuilder;
    private int ticksSinceOpened;
    private TextFieldHelper selectionManager;

    public TombstoneGui(TombstoneBlockEntity tombstone) {
        super(new TranslatableComponent("gui.overlord.tombstone.edit"));
        this.tombstone = tombstone;
        Injector injector = OverlordConstants.getInjector();
        this.packetSender = injector.getInstance(PacketSender.class);
        this.serverboundPackets = injector.getInstance(ServerboundPackets.class);
        this.saveTombstoneBufferBuilder = injector.getInstance(SaveTombstoneBufferBuilder.class);
    }

    @Override
    protected void init() {
        assert this.minecraft != null;
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.addRenderableWidget(new Button(this.width / 2 - 100, this.height / 4 + 120, 200, 20, new TranslatableComponent("gui.done"), (buttonWidget) -> {
            this.finishEditing();
        }));
        this.selectionManager = new TextFieldHelper(
            this.tombstone::getNameText,
            this.tombstone::setNameText,
            TextFieldHelper.createClipboardGetter(this.minecraft),
            TextFieldHelper.createClipboardSetter(this.minecraft),
            (string) -> this.minecraft.font.width(string) <= 90 || string.length() <= 16
        );
    }

    @Override
    public void removed() {
        //noinspection ConstantConditions
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
        packetSender.sendToServer(
            serverboundPackets.saveTombstone(),
            saveTombstoneBufferBuilder.build(
                tombstone.getBlockPos(),
                tombstone.getNameText()
            )
        );
    }

    @Override
    public void tick() {
        ++this.ticksSinceOpened;
        if (!this.tombstone.getType().isValid(this.tombstone.getBlockState())) {
            this.finishEditing();
        }
    }

    private void finishEditing() {
        this.tombstone.setChanged();
        this.minecraft.setScreen(null);
    }

    @Override
    public boolean charTyped(char chr, int keyCode) {
        this.selectionManager.charTyped(chr);
        return true;
    }

    @Override
    public void onClose() {
        this.finishEditing();
        super.onClose();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 265) {
            this.selectionManager.setCursorToEnd();
            return true;
        } else if (keyCode != 264 && keyCode != 257 && keyCode != 335) {
            return this.selectionManager.keyPressed(keyCode) || super.keyPressed(keyCode, scanCode, modifiers);
        } else {
            this.selectionManager.setCursorToEnd();
            return true;
        }
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float delta) {
        Lighting.setupForFlatItems();
        this.renderBackground(matrixStack);
        int textColor = 0xFFFFFF;
        drawCenteredString(matrixStack, this.font, this.title.getContents(), this.width / 2, 40, textColor);
        matrixStack.pushPose();
        matrixStack.translate(this.width / 2.0, 0.0D, 50.0D);
        float scale = 93.75F;
        matrixStack.scale(scale, -scale, scale);
        matrixStack.translate(0.0D, -1.3125D, 0.0D);
        BlockState blockState = this.tombstone.getBlockState();

        boolean isCursorVisible = this.ticksSinceOpened / 6 % 2 == 0;
        matrixStack.pushPose();
        float backgroundItemScale = 3.5f;
        matrixStack.scale(backgroundItemScale, backgroundItemScale, -backgroundItemScale);
        MultiBufferSource.BufferSource immediate = this.minecraft.renderBuffers().bufferSource();
        int light = 0xF000F0;
        this.minecraft.getItemRenderer().renderStatic(
            new ItemStack(blockState.getBlock()),
            ItemTransforms.TransformType.FIXED,
            light,
            OverlayTexture.NO_OVERLAY,
            matrixStack,
            immediate,
            0
        );

        matrixStack.popPose();
        matrixStack.translate(0.0D, 0.3333333432674408D, 0.046666666865348816D);
        float textScale = 0.010416667F;
        matrixStack.scale(textScale, -textScale, textScale);

        String string = this.tombstone.getNameText();

        Matrix4f matrix4f = matrixStack.last().pose();
        int selectionStart = this.selectionManager.getCursorPos();
        int selectionEnd = this.selectionManager.getSelectionPos();
        int directionMultiplier = this.minecraft.font.isBidirectional() ? -1 : 1;
        int cursorY = 5;

        int u;
        int cursorX;
        if (!string.isEmpty()) {
            float x = (float) (-this.minecraft.font.width(string) / 2);
            int y = 5;
            this.minecraft.font.drawInBatch(string, x, y, textColor, false, matrix4f, immediate, false, 0, light);
            if (selectionStart >= 0 && isCursorVisible) {
                u = this.minecraft.font.width(string.substring(0, Math.min(selectionStart, string.length())));
                cursorX = (u - this.minecraft.font.width(string) / 2) * directionMultiplier;
                if (selectionStart >= string.length()) {
                    this.minecraft.font.drawInBatch("_", (float) cursorX, (float) cursorY, textColor, false, matrix4f, immediate, false, 0, light);
                }
            }
        }

        immediate.endBatch();

        if (!string.isEmpty() && selectionStart >= 0) {
            int t = this.minecraft.font.width(string.substring(0, Math.min(selectionStart, string.length())));
            u = (t - this.minecraft.font.width(string) / 2) * directionMultiplier;
            if (isCursorVisible && selectionStart < string.length()) {
                int var34 = cursorY - 1;
                int var10003 = u + 1;
                fill(matrixStack, u, var34, var10003, cursorY + 9, -16777216 | textColor);
            }

            if (selectionEnd != selectionStart) {
                cursorX = Math.min(selectionStart, selectionEnd);
                int w = Math.max(selectionStart, selectionEnd);
                int x = (this.minecraft.font.width(string.substring(0, cursorX)) - this.minecraft.font.width(string) / 2) * directionMultiplier;
                int y = (this.minecraft.font.width(string.substring(0, w)) - this.minecraft.font.width(string) / 2) * directionMultiplier;
                int z = Math.min(x, y);
                int aa = Math.max(x, y);
                Tesselator tessellator = Tesselator.getInstance();
                BufferBuilder bufferBuilder = tessellator.getBuilder();
                RenderSystem.disableTexture();
                RenderSystem.enableColorLogicOp();
                RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
                bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
                float var35 = (float) z;
                bufferBuilder.vertex(matrix4f, var35, (float) (cursorY + 9), 0.0F).color(0, 0, 255, 255).endVertex();
                var35 = (float) aa;
                bufferBuilder.vertex(matrix4f, var35, (float) (cursorY + 9), 0.0F).color(0, 0, 255, 255).endVertex();
                bufferBuilder.vertex(matrix4f, (float) aa, (float) cursorY, 0.0F).color(0, 0, 255, 255).endVertex();
                bufferBuilder.vertex(matrix4f, (float) z, (float) cursorY, 0.0F).color(0, 0, 255, 255).endVertex();
                bufferBuilder.end();
                BufferUploader.end(bufferBuilder);
                RenderSystem.disableColorLogicOp();
                RenderSystem.enableTexture();
            }
        }

        matrixStack.popPose();
        matrixStack.pushPose();
        matrixStack.translate(0, 0, 25);
        Lighting.setupFor3DItems();
        super.render(matrixStack, mouseX, mouseY, delta);
        matrixStack.popPose();
    }
}
