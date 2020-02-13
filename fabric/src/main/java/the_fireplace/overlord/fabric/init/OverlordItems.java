package the_fireplace.overlord.fabric.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static the_fireplace.overlord.fabric.Overlord.MODID;

public class OverlordItems {

    public static void registerItems() {

    }

    private static void registerItem(String path, Item item) {
        Registry.register(Registry.ITEM, new Identifier(MODID, path), item);
    }
}
