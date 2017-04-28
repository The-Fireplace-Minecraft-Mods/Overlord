package the_fireplace.overlord.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import the_fireplace.overlord.CommonProxy;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.client.render.*;
import the_fireplace.overlord.entity.EntityBabySkeleton;
import the_fireplace.overlord.entity.EntityConvertedSkeleton;
import the_fireplace.overlord.entity.EntityCuringSkeleton;
import the_fireplace.overlord.entity.EntitySkeletonWarrior;
import the_fireplace.overlord.entity.projectile.EntityMilkBottle;
import the_fireplace.overlord.tools.SkinTools;

import javax.annotation.Nonnull;
import java.io.File;

/**
 * @author The_Fireplace
 */
public class ClientProxy extends CommonProxy {
    @Override
    public String translateToLocal(@Nonnull String u, Object... args){
        return I18n.format(u, args);
    }

    @Override
    public EntityPlayer getPlayerEntity(MessageContext ctx) {
        return (ctx.side.isClient() ? Minecraft.getMinecraft().player : super.getPlayerEntity(ctx));
    }

    @Override
    public void registerClient(){
        Overlord.instance.registerItemRenders();
        RenderingRegistry.registerEntityRenderingHandler(EntitySkeletonWarrior.class, new SkeletonWarriorRenderFactory());
        RenderingRegistry.registerEntityRenderingHandler(EntityBabySkeleton.class, new BabySkeletonRenderFactory());
        RenderingRegistry.registerEntityRenderingHandler(EntityMilkBottle.class, new MilkBottleRenderFactory());
        RenderingRegistry.registerEntityRenderingHandler(EntityConvertedSkeleton.class, new ConvertedSkeletonRenderFactory());
        RenderingRegistry.registerEntityRenderingHandler(EntityCuringSkeleton.class, new CuringSkeletonRenderFactory());
        if(SkinTools.cachedir.exists()){
            if(SkinTools.cachedir.listFiles().length > 0)
            for(File file:SkinTools.cachedir.listFiles()){
                if(file.getName().contains(".png"))
                    SkinTools.cacheSkin(file.getName().replace(".png",""));
            }
        }
        MinecraftForge.EVENT_BUS.register(new ClientEvents());
    }
}
