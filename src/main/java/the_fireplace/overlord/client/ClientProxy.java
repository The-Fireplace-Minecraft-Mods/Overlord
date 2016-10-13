package the_fireplace.overlord.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import the_fireplace.overlord.CommonProxy;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.client.render.LayerSkinsuit;
import the_fireplace.overlord.client.render.SkeletonWarriorRenderFactory;
import the_fireplace.overlord.entity.EntitySkeletonWarrior;

import java.io.File;

/**
 * @author The_Fireplace
 */
public class ClientProxy extends CommonProxy {
    @SuppressWarnings("ConfusingArgumentToVarargsMethod")
    @Override
    public String translateToLocal(String u, String... args){
        return I18n.format(u, args);
    }

    @Override
    public EntityPlayer getPlayerEntity(MessageContext ctx) {
        return (ctx.side.isClient() ? Minecraft.getMinecraft().thePlayer : super.getPlayerEntity(ctx));
    }

    @Override
    public void registerClient(){
        Overlord.instance.registerItemRenders();
        RenderingRegistry.registerEntityRenderingHandler(EntitySkeletonWarrior.class, new SkeletonWarriorRenderFactory());
        if(LayerSkinsuit.cachedir.exists()){
            if(LayerSkinsuit.cachedir.listFiles().length > 0)
            for(File file:LayerSkinsuit.cachedir.listFiles()){
                if(file.getName().contains(".png"))
                    LayerSkinsuit.cacheSkin(file.getName().replace(".png",""));
            }
        }
    }
}
