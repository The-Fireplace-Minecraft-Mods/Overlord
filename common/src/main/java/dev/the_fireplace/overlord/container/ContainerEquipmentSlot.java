package dev.the_fireplace.overlord.container;

import com.mojang.datafixers.util.Pair;
import dev.the_fireplace.overlord.OverlordConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import static net.minecraft.world.inventory.InventoryMenu.*;

public class ContainerEquipmentSlot extends Slot
{
    public static final ResourceLocation EMPTY_WEAPON_SLOT_TEXTURE = new ResourceLocation(OverlordConstants.MODID, "item/empty_weapon_slot");
    public static final ResourceLocation EMPTY_SHIELD_SLOT_TEXTURE = InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD;
    private static final ResourceLocation[] EMPTY_ARMOR_SLOT_TEXTURES = new ResourceLocation[]{EMPTY_ARMOR_SLOT_BOOTS, EMPTY_ARMOR_SLOT_LEGGINGS, EMPTY_ARMOR_SLOT_CHESTPLATE, EMPTY_ARMOR_SLOT_HELMET};
    private static final ResourceLocation[] EMPTY_HAND_SLOT_TEXTURES = new ResourceLocation[]{EMPTY_WEAPON_SLOT_TEXTURE, EMPTY_SHIELD_SLOT_TEXTURE};

    private final EquipmentSlot equipmentSlot;

    public ContainerEquipmentSlot(EquipmentSlot equipmentSlot, Container inventory, int invSlot, int xPosition, int yPosition) {
        super(inventory, invSlot, xPosition, yPosition);
        this.equipmentSlot = equipmentSlot;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return equipmentSlot == getPreferredEquipmentSlot(stack);
    }

    private EquipmentSlot getPreferredEquipmentSlot(ItemStack stack) {
        //TODO put ammo in the off hand
        return Mob.getEquipmentSlotForItem(stack);
    }

    @Override
    public boolean mayPickup(Player playerEntity) {
        ItemStack itemStack = this.getItem();
        return (itemStack.isEmpty() || playerEntity.isCreative() || !EnchantmentHelper.hasBindingCurse(itemStack))
            && super.mayPickup(playerEntity);
    }

    @Override
    public int getMaxStackSize() {
        return equipmentSlot.getType() == EquipmentSlot.Type.ARMOR
            ? 1
            : super.getMaxStackSize();
    }

    @Override
    public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
        ResourceLocation emptySlotTexture = equipmentSlot.getType() == EquipmentSlot.Type.ARMOR
            ? EMPTY_ARMOR_SLOT_TEXTURES[equipmentSlot.getIndex()]
            : EMPTY_HAND_SLOT_TEXTURES[equipmentSlot.getIndex()];

        return Pair.of(BLOCK_ATLAS, emptySlotTexture);
    }
}
