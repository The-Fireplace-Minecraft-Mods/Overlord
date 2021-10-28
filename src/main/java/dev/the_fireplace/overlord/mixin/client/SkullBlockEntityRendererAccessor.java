package dev.the_fireplace.overlord.mixin.client;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.model.SkullEntityModel;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Environment(EnvType.CLIENT)
@Mixin(SkullBlockEntityRenderer.class)
public interface SkullBlockEntityRendererAccessor
{
    @Accessor
    static Map<SkullBlock.SkullType, SkullEntityModel> getMODELS() {
        throw new IllegalStateException("Mixin failed!");
    }

    @Invoker
    static RenderLayer callMethod_3578(SkullBlock.SkullType skullType, @Nullable GameProfile gameProfile) {
        throw new IllegalStateException("Mixin failed!");
    }
}
