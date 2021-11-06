package dev.the_fireplace.overlord.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin(PlayerEntityModel.class)
public interface PlayerEntityModelAccessor
{
    @Accessor
    @Mutable
    void setLeftSleeve(ModelPart leftSleeve);

    @Accessor
    @Mutable
    void setRightSleeve(ModelPart rightSleeve);

    @Accessor
    @Mutable
    void setLeftPants(ModelPart leftPantLeg);

    @Accessor
    @Mutable
    void setRightPants(ModelPart rightPantLeg);

    @Accessor
    @Mutable
    void setJacket(ModelPart jacket);
}
