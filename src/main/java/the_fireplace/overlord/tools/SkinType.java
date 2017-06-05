package the_fireplace.overlord.tools;

import com.google.common.collect.Maps;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.ArrayUtils;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.items.ItemSkinsuit;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.LinkedHashMap;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SkinType {
    private static final LinkedHashMap<SkinType, ResourceLocation> skintypes = Maps.newLinkedHashMap();

    public static final SkinType NONE = new SkinType(new ResourceLocation("missing"));
    public static final SkinType PLAYER = new SkinType(SkinTools.STEVE);
    public static final SkinType MUMMY = new SkinType(new ResourceLocation("textures/entity/zombie/husk.png"));

    public SkinType(ResourceLocation texLoc){
        if(!skintypes.containsKey(this))
            skintypes.put(this, texLoc);
        else
            Overlord.logError("Skin type "+this.toString()+" is already registered! Skipping...");
    }

    public final boolean isNone(){
        return this.equals(NONE);
    }

    public boolean protectsFromSun(){
        return !this.isNone();
    }

    public final int ordinal(){
        return ArrayUtils.indexOf(skintypes.keySet().toArray(), this);
    }

    public static final SkinType get(int index){
        return (SkinType)skintypes.keySet().toArray()[index];
    }

    public final ResourceLocation getTexture(){
        return skintypes.get(this);
    }

    public static final SkinType getSkinTypeFromStack(ItemStack stack){
        if(!stack.isEmpty() && stack.getItem() instanceof ItemSkinsuit)
            return ((ItemSkinsuit)stack.getItem()).getType();
        return SkinType.NONE;
    }
}
