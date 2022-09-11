package dev.the_fireplace.overlord.mixin.client;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.world.level.block.SkullBlock;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(SkullBlockRenderer.class)
public interface SkullBlockEntityRendererAccessor
{
    @Accessor
    static Map<SkullBlock.Type, SkullModel> getMODEL_BY_TYPE() {
        throw new IllegalStateException("Mixin failed!");
    }

    @Invoker
    static RenderType callGetRenderType(SkullBlock.Type skullType, @Nullable GameProfile gameProfile) {
        throw new IllegalStateException("Mixin failed!");
    }
}
