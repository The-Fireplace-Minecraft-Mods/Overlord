package the_fireplace.skeletonwars.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import the_fireplace.skeletonwars.client.gui.GuiSkeletonMaker;
import the_fireplace.skeletonwars.container.ContainerSkeletonMaker;
import the_fireplace.skeletonwars.entity.EntitySkeletonWarrior;
import the_fireplace.skeletonwars.tileentity.TileEntitySkeletonMaker;

/**
 * @author The_Fireplace
 */
public class SSGuiHandler implements IGuiHandler {
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity entity = world.getTileEntity(new BlockPos(x, y, z));
        switch (ID) {
            case 0:
                if (entity != null && entity instanceof TileEntitySkeletonMaker) {
                    return new ContainerSkeletonMaker(player.inventory, (TileEntitySkeletonMaker) entity);
                } else {
                    if(world.getEntityByID(ID) != null){
                        if(world.getEntityByID(ID) instanceof EntitySkeletonWarrior){
                            //return new ContainerSkeletonWarrior(player.inventory, );
                        }
                    }
                    return null;
                }
            default:
                return null;
        }
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity entity = world.getTileEntity(new BlockPos(x, y, z));
        switch (ID) {
            case 0:
                if (entity != null && entity instanceof TileEntitySkeletonMaker) {
                    return new GuiSkeletonMaker(player.inventory, (TileEntitySkeletonMaker) entity);
                } else {
                    if(world.getEntityByID(ID) != null){
                        if(world.getEntityByID(ID) instanceof EntitySkeletonWarrior){
                            //return new GuiSkeleton(player.inventory, );
                        }
                    }
                    return null;
                }
            default:
                return null;
        }
    }
}
