package dev.the_fireplace.overlord.client.gui.rendertools;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;

public class DrawEntity
{
    public static void drawEntityFacingAway(int x, int y, int size, long startMillisecond, long currentMillisecond, LivingEntity entity) {
        boolean flutterCape = (currentMillisecond - startMillisecond) % 60 == 0;
        RandomSource random = entity.getRandom();
        double entityX = flutterCape ? entity.getX() + (random.nextFloat() * 2 - 1) / 16f : entity.getX();
        double entityZ = flutterCape ? entity.getZ() + (random.nextFloat() * 2 - 1) / 16f : entity.getZ();
        entity.setPosRaw(entityX, (currentMillisecond - startMillisecond) * 1.2, entityZ);

        PoseStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.pushPose();
        matrixStack.translate((double) x, (double) y, 1050.0D);
        matrixStack.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
        PoseStack matrixStack2 = new PoseStack();
        matrixStack2.translate(0.0D, 0.0D, 1000.0D);
        matrixStack2.scale((float) size, (float) size, (float) size);
        Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
        Quaternion quaternion2 = Vector3f.XP.rotationDegrees(0);
        quaternion.mul(quaternion2);
        matrixStack2.mulPose(quaternion);
        float h = entity.yBodyRot;
        float i = entity.getYRot();
        float j = entity.getXRot();
        float k = entity.yHeadRotO;
        float l = entity.yHeadRot;
        entity.yBodyRot = 0;
        entity.setYRot(entity.yBodyRot);
        entity.setXRot(0);
        entity.yHeadRot = entity.getYRot();
        entity.yHeadRotO = entity.getYRot();
        Lighting.setupForEntityInInventory();
        EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        quaternion2.conj();
        entityRenderDispatcher.overrideCameraOrientation(quaternion2);
        entityRenderDispatcher.setRenderShadow(false);
        MultiBufferSource.BufferSource immediate = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> {
            entityRenderDispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixStack2, immediate, 15728880);
        });
        immediate.endBatch();
        entityRenderDispatcher.setRenderShadow(true);
        entity.yBodyRot = h;
        entity.setYRot(i);
        entity.setXRot(j);
        entity.yHeadRotO = k;
        entity.yHeadRot = l;
        matrixStack.popPose();
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();
    }
}
