

package dev.the_fireplace.overlord.client.model.tile;

import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.blockentity.CasketBlockEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class CasketBlockEntityRenderer extends BlockEntityRenderer<CasketBlockEntity> {
	private final ModelPart bone2;
	private final ModelPart cube_r1;
	private final ModelPart cube_r2;
	private final ModelPart cube_r3;
	private final ModelPart cube_r4;
	private final ModelPart bone3;
	private final ModelPart cube_r5;
	private final ModelPart cube_r6;
	private final ModelPart cube_r7;
	private final ModelPart cube_r8;
	public CasketBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		super(blockEntityRenderDispatcher);
		int textureWidth = 128;
		int textureHeight = 128;
		bone2 = new ModelPart(textureWidth, textureHeight, 0, 0);
		bone2.setPivot(-0.1562F, 19.5F, 3.264F);
		bone2.setTextureOffset(0, 0).addCuboid(-4.8438F, -2.5F, -11.264F, 10.0F, 7.0F, 26.0F, 0.0F, false);
		bone2.setTextureOffset(0, 34).addCuboid(-5.8438F, -2.5F, -7.264F, 1.0F, 7.0F, 10.0F, 0.0F, false);
		bone2.setTextureOffset(0, 0).addCuboid(5.1562F, -2.5F, -7.264F, 1.0F, 7.0F, 10.0F, 0.0F, false);

		cube_r1 = new ModelPart(textureWidth, textureHeight, 0, 0);
		cube_r1.setPivot(-2.5961F, 0.5F, 6.0069F);
		bone2.addChild(cube_r1);
		setRotationAngle(cube_r1, 0.0F, 0.0873F, 0.0F);
		cube_r1.setTextureOffset(53, 14).addCuboid(-3.0F, -3.0F, -11.5F, 1.0F, 7.0F, 20.0F, 0.0F, false);

		cube_r2 = new ModelPart(textureWidth, textureHeight, 0, 0);
		cube_r2.setPivot(-5.3583F, 1.0F, -7.4218F);
		bone2.addChild(cube_r2);
		setRotationAngle(cube_r2, 0.0F, -0.3054F, 0.0F);
		cube_r2.setTextureOffset(0, 62).addCuboid(-0.5F, -3.5F, -3.5F, 1.0F, 7.0F, 6.0F, 0.0F, false);

		cube_r3 = new ModelPart(textureWidth, textureHeight, 0, 0);
		cube_r3.setPivot(4.4162F, 0.5F, -8.0748F);
		bone2.addChild(cube_r3);
		setRotationAngle(cube_r3, 0.0F, 0.3054F, 0.0F);
		cube_r3.setTextureOffset(23, 62).addCuboid(0.5F, -3.0F, -2.5F, 1.0F, 7.0F, 6.0F, 0.0F, false);

		cube_r4 = new ModelPart(textureWidth, textureHeight, 0, 0);
		cube_r4.setPivot(2.952F, 0.5F, 5.5088F);
		bone2.addChild(cube_r4);
		setRotationAngle(cube_r4, 0.0F, -0.0873F, 0.0F);
		cube_r4.setTextureOffset(53, 53).addCuboid(2.0F, -3.0F, -11.0F, 1.0F, 7.0F, 20.0F, 0.0F, false);

		bone3 = new ModelPart(textureWidth, textureHeight, 0, 0);
		bone3.setPivot(-0.1562F, 12.5F, 3.264F);
		bone3.setTextureOffset(0, 34).addCuboid(-4.8438F, 3.5F, -11.264F, 10.0F, 1.0F, 26.0F, 0.0F, false);
		bone3.setTextureOffset(47, 0).addCuboid(-5.8438F, 2.5F, -6.264F, 12.0F, 1.0F, 3.0F, 0.0F, false);
		bone3.setTextureOffset(47, 47).addCuboid(-5.8438F, 2.5F, 7.736F, 12.0F, 1.0F, 3.0F, 0.0F, false);
		bone3.setTextureOffset(68, 0).addCuboid(-5.8438F, 3.5F, -7.264F, 1.0F, 1.0F, 10.0F, 0.0F, false);
		bone3.setTextureOffset(47, 5).addCuboid(5.1562F, 3.5F, -7.264F, 1.0F, 1.0F, 10.0F, 0.0F, false);

		cube_r5 = new ModelPart(textureWidth, textureHeight, 0, 0);
		cube_r5.setPivot(-2.5961F, 0.5F, 6.0069F);
		bone3.addChild(cube_r5);
		setRotationAngle(cube_r5, 0.0F, 0.0873F, 0.0F);
		cube_r5.setTextureOffset(0, 62).addCuboid(-3.0F, 3.0F, -11.5F, 1.0F, 1.0F, 20.0F, 0.0F, false);

		cube_r6 = new ModelPart(textureWidth, textureHeight, 0, 0);
		cube_r6.setPivot(-5.3583F, 1.0F, -7.4218F);
		bone3.addChild(cube_r6);
		setRotationAngle(cube_r6, 0.0F, -0.3054F, 0.0F);
		cube_r6.setTextureOffset(0, 18).addCuboid(-0.5F, 2.5F, -3.5F, 1.0F, 1.0F, 6.0F, 0.0F, false);

		cube_r7 = new ModelPart(textureWidth, textureHeight, 0, 0);
		cube_r7.setPivot(4.4162F, 0.5F, -8.0748F);
		bone3.addChild(cube_r7);
		setRotationAngle(cube_r7, 0.0F, 0.3054F, 0.0F);
		cube_r7.setTextureOffset(47, 17).addCuboid(0.5F, 3.0F, -2.5F, 1.0F, 1.0F, 6.0F, 0.0F, false);

		cube_r8 = new ModelPart(textureWidth, textureHeight, 0, 0);
		cube_r8.setPivot(2.952F, 0.5F, 5.5088F);
		bone3.addChild(cube_r8);
		setRotationAngle(cube_r8, 0.0F, -0.0873F, 0.0F);
		cube_r8.setTextureOffset(23, 64).addCuboid(2.0F, 3.0F, -11.0F, 1.0F, 1.0F, 20.0F, 0.0F, false);
	}

	@Override
	public void render(CasketBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		if (blockEntity.getWorld() != null) {
			light = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().up());
		}
		SpriteIdentifier spriteIdentifier = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, new Identifier(Overlord.MODID, "block/casket"));
		VertexConsumer vertexConsumer = spriteIdentifier.getVertexConsumer(vertexConsumers, RenderLayer::getEntityCutout);
		//setRotationAngle(bone2, 0, 0, (float) Math.PI);
		//setRotationAngle(bone3, 0, 0, (float) Math.PI);
		bone2.render(matrices, vertexConsumer, light, overlay);
		bone3.render(matrices, vertexConsumer, light, overlay);
	}

	public void setRotationAngle(ModelPart bone, float x, float y, float z) {
		bone.pitch = x;
		bone.yaw = y;
		bone.roll = z;
	}
}