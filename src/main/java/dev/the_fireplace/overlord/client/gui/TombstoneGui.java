package dev.the_fireplace.overlord.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.the_fireplace.annotateddi.api.DIContainer;
import dev.the_fireplace.overlord.blockentity.TombstoneBlockEntity;
import dev.the_fireplace.overlord.domain.network.ClientToServerPacketIDs;
import dev.the_fireplace.overlord.domain.network.client.SaveTombstoneBufferBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.SelectionManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Matrix4f;

@Environment(EnvType.CLIENT)
public class TombstoneGui extends Screen
{
    private final TombstoneBlockEntity tombstone;
    private int ticksSinceOpened;
    private SelectionManager selectionManager;

    private final ClientToServerPacketIDs clientToServerPacketIDs;
    private final SaveTombstoneBufferBuilder saveTombstoneBufferBuilder;

    public TombstoneGui(TombstoneBlockEntity tombstone) {
        super(new TranslatableText("gui.overlord.tombstone.edit"));
        this.tombstone = tombstone;
        this.clientToServerPacketIDs = DIContainer.get().getInstance(ClientToServerPacketIDs.class);
        this.saveTombstoneBufferBuilder = DIContainer.get().getInstance(SaveTombstoneBufferBuilder.class);
    }

    @Override
    protected void init() {
        assert this.client != null;
        this.client.keyboard.setRepeatEvents(true);
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120, 200, 20, new TranslatableText("gui.done"), (buttonWidget) -> {
            this.finishEditing();
        }));
        this.selectionManager = new SelectionManager(
            this.tombstone::getNameText,
            this.tombstone::setNameText,
            SelectionManager.makeClipboardGetter(this.client),
            SelectionManager.makeClipboardSetter(this.client),
            (string) -> this.client.textRenderer.getWidth(string) <= 90
        );
    }

    @Override
    public void removed() {
        //noinspection ConstantConditions
        this.client.keyboard.setRepeatEvents(false);
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.getNetworkHandler();
        if (clientPlayNetworkHandler != null) {
            clientPlayNetworkHandler.sendPacket(ClientPlayNetworking.createC2SPacket(
                clientToServerPacketIDs.saveTombstonePacketID(),
                saveTombstoneBufferBuilder.build(
                    tombstone.getPos(),
                    tombstone.getNameText()
                )
            ));
        }
    }

    @Override
    public void tick() {
        ++this.ticksSinceOpened;
        if (!this.tombstone.getType().supports(this.tombstone.getCachedState().getBlock())) {
            this.finishEditing();
        }
    }

    private void finishEditing() {
        this.tombstone.markDirty();
        this.client.openScreen(null);
    }

    @Override
    public boolean charTyped(char chr, int keyCode) {
        this.selectionManager.insert(chr);
        return true;
    }

    @Override
    public void onClose() {
        this.finishEditing();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 265) {
            this.selectionManager.putCursorAtEnd();
            return true;
        } else if (keyCode != 264 && keyCode != 257 && keyCode != 335) {
            return this.selectionManager.handleSpecialKey(keyCode) || super.keyPressed(keyCode, scanCode, modifiers);
        } else {
            this.selectionManager.putCursorAtEnd();
            return true;
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        DiffuseLighting.disableGuiDepthLighting();
        this.renderBackground(matrixStack);
        int textColor = 0xFFFFFF;
        drawCenteredText(matrixStack, this.textRenderer, this.title.asString(), this.width / 2, 40, textColor);
        matrixStack.push();
        matrixStack.translate(this.width / 2.0, 0.0D, 50.0D);
        float scale = 93.75F;
        matrixStack.scale(scale, -scale, scale);
        matrixStack.translate(0.0D, -1.3125D, 0.0D);
        BlockState blockState = this.tombstone.getCachedState();

        boolean isCursorVisible = this.ticksSinceOpened / 6 % 2 == 0;
        matrixStack.push();
        float backgroundItemScale = 3.5f;
        matrixStack.scale(backgroundItemScale, backgroundItemScale, -backgroundItemScale);
        VertexConsumerProvider.Immediate immediate = this.client.getBufferBuilders().getEntityVertexConsumers();
        int light = 0xF000F0;
        this.client.getItemRenderer().renderItem(
            new ItemStack(blockState.getBlock()),
            ModelTransformation.Mode.FIXED,
            light,
            OverlayTexture.DEFAULT_UV,
            matrixStack,
            immediate
        );

        matrixStack.pop();
        matrixStack.translate(0.0D, 0.3333333432674408D, 0.046666666865348816D);
        float textScale = 0.010416667F;
        matrixStack.scale(textScale, -textScale, textScale);

        String string = this.tombstone.getNameText();

        Matrix4f matrix4f = matrixStack.peek().getModel();
        int selectionStart = this.selectionManager.getSelectionStart();
        int selectionEnd = this.selectionManager.getSelectionEnd();
        int directionMultiplier = this.client.textRenderer.isRightToLeft() ? -1 : 1;
        int cursorY = 5;

        int u;
        int cursorX;
        if (!string.isEmpty()) {
            float x = (float) (-this.client.textRenderer.getWidth(string) / 2);
            int y = 5;
            this.client.textRenderer.draw(string, x, y, textColor, false, matrix4f, immediate, false, 0, light);
            if (selectionStart >= 0 && isCursorVisible) {
                u = this.client.textRenderer.getWidth(string.substring(0, Math.min(selectionStart, string.length())));
                cursorX = (u - this.client.textRenderer.getWidth(string) / 2) * directionMultiplier;
                if (selectionStart >= string.length()) {
                    this.client.textRenderer.draw("_", (float) cursorX, (float) cursorY, textColor, false, matrix4f, immediate, false, 0, light);
                }
            }
        }

        immediate.draw();

        if (!string.isEmpty() && selectionStart >= 0) {
            int t = this.client.textRenderer.getWidth(string.substring(0, Math.min(selectionStart, string.length())));
            u = (t - this.client.textRenderer.getWidth(string) / 2) * directionMultiplier;
            if (isCursorVisible && selectionStart < string.length()) {
                int var34 = cursorY - 1;
                int var10003 = u + 1;
                fill(matrixStack, u, var34, var10003, cursorY + 9, -16777216 | textColor);
            }

            if (selectionEnd != selectionStart) {
                cursorX = Math.min(selectionStart, selectionEnd);
                int w = Math.max(selectionStart, selectionEnd);
                int x = (this.client.textRenderer.getWidth(string.substring(0, cursorX)) - this.client.textRenderer.getWidth(string) / 2) * directionMultiplier;
                int y = (this.client.textRenderer.getWidth(string.substring(0, w)) - this.client.textRenderer.getWidth(string) / 2) * directionMultiplier;
                int z = Math.min(x, y);
                int aa = Math.max(x, y);
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferBuilder = tessellator.getBuffer();
                RenderSystem.disableTexture();
                RenderSystem.enableColorLogicOp();
                RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
                bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
                float var35 = (float) z;
                bufferBuilder.vertex(matrix4f, var35, (float) (cursorY + 9), 0.0F).color(0, 0, 255, 255).next();
                var35 = (float) aa;
                bufferBuilder.vertex(matrix4f, var35, (float) (cursorY + 9), 0.0F).color(0, 0, 255, 255).next();
                bufferBuilder.vertex(matrix4f, (float) aa, (float) cursorY, 0.0F).color(0, 0, 255, 255).next();
                bufferBuilder.vertex(matrix4f, (float) z, (float) cursorY, 0.0F).color(0, 0, 255, 255).next();
                bufferBuilder.end();
                BufferRenderer.draw(bufferBuilder);
                RenderSystem.disableColorLogicOp();
                RenderSystem.enableTexture();
            }
        }

        matrixStack.pop();
        DiffuseLighting.enableGuiDepthLighting();
        super.render(matrixStack, mouseX, mouseY, delta);
    }
}
