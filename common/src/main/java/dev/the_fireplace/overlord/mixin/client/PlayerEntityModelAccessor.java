package dev.the_fireplace.overlord.mixin.client;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerModel.class)
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
