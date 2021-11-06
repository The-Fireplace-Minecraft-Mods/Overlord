package dev.the_fireplace.overlord.container;

import com.mojang.datafixers.util.Pair;
import dev.the_fireplace.overlord.Overlord;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

import static net.minecraft.screen.PlayerScreenHandler.*;

public class ContainerEquipmentSlot extends Slot
{
    public static final Identifier EMPTY_WEAPON_SLOT_TEXTURE = new Identifier(Overlord.MODID, "item/empty_weapon_slot");
    public static final Identifier EMPTY_SHIELD_SLOT_TEXTURE = PlayerScreenHandler.EMPTY_OFFHAND_ARMOR_SLOT;
    private static final Identifier[] EMPTY_ARMOR_SLOT_TEXTURES = new Identifier[]{EMPTY_BOOTS_SLOT_TEXTURE, EMPTY_LEGGINGS_SLOT_TEXTURE, EMPTY_CHESTPLATE_SLOT_TEXTURE, EMPTY_HELMET_SLOT_TEXTURE};
    private static final Identifier[] EMPTY_HAND_SLOT_TEXTURES = new Identifier[]{EMPTY_WEAPON_SLOT_TEXTURE, EMPTY_SHIELD_SLOT_TEXTURE};

    private final EquipmentSlot equipmentSlot;

    public ContainerEquipmentSlot(EquipmentSlot equipmentSlot, Inventory inventory, int invSlot, int xPosition, int yPosition) {
        super(inventory, invSlot, xPosition, yPosition);
        this.equipmentSlot = equipmentSlot;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return equipmentSlot == getPreferredEquipmentSlot(stack);
    }

    private EquipmentSlot getPreferredEquipmentSlot(ItemStack stack) {
        //TODO put ammo in the off hand
        return MobEntity.getPreferredEquipmentSlot(stack);
    }

    @Override
    public boolean canTakeItems(PlayerEntity playerEntity) {
        ItemStack itemStack = this.getStack();
        return (itemStack.isEmpty() || playerEntity.isCreative() || !EnchantmentHelper.hasBindingCurse(itemStack))
            && super.canTakeItems(playerEntity);
    }

    @Override
    public int getMaxItemCount() {
        return equipmentSlot.getType() == EquipmentSlot.Type.ARMOR
            ? 1
            : super.getMaxItemCount();
    }

    @Environment(EnvType.CLIENT)
    @Override
    public Pair<Identifier, Identifier> getBackgroundSprite() {
        Identifier emptySlotTexture = equipmentSlot.getType() == EquipmentSlot.Type.ARMOR
            ? EMPTY_ARMOR_SLOT_TEXTURES[equipmentSlot.getEntitySlotId()]
            : EMPTY_HAND_SLOT_TEXTURES[equipmentSlot.getEntitySlotId()];

        return Pair.of(BLOCK_ATLAS_TEXTURE, emptySlotTexture);
    }
}
