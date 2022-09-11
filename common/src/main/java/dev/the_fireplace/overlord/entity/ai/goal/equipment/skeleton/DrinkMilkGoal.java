package dev.the_fireplace.overlord.entity.ai.goal.equipment.skeleton;

import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.domain.entity.AnimatedMilkDrinker;
import dev.the_fireplace.overlord.domain.inventory.InventorySearcher;
import dev.the_fireplace.overlord.entity.ArmyEntity;
import dev.the_fireplace.overlord.entity.ai.goal.equipment.SwapEquipmentGoal;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.Predicate;

public class DrinkMilkGoal<T extends ArmyEntity & AnimatedMilkDrinker> extends SwapEquipmentGoal
{
    protected static final Predicate<ItemStack> IS_MILK = stack -> stack.getItem() == Items.MILK_BUCKET;
    protected final T armyEntity;
    protected final InventorySearcher inventorySearcher;
    protected int swapBackSlot;

    protected byte drinkingTicks;

    public DrinkMilkGoal(T armyEntity) {
        super(armyEntity);
        this.armyEntity = armyEntity;
        this.inventorySearcher = OverlordConstants.getInjector().getInstance(InventorySearcher.class);
    }

    @Override
    public boolean canUse() {
        return super.canUse()
            && inventorySearcher.hasSlotMatching(armyEntity.getInventory(), IS_MILK)
            && armyEntity.canDrinkMilk();
    }

    @Override
    public void start() {
        super.start();
        Integer milkSlot = this.inventorySearcher.getFirstSlotMatching(armyEntity.getInventory(), IS_MILK);
        if (milkSlot != null) {
            int offHandSlot = this.armyEntity.getOffHandSlot();
            Container inventory = this.armyEntity.getInventory();
            ItemStack milkStack = inventory.removeItemNoUpdate(milkSlot);
            ItemStack offHandStack = inventory.removeItemNoUpdate(offHandSlot);
            inventory.setItem(offHandSlot, milkStack);
            inventory.setItem(milkSlot, offHandStack);
            this.swapBackSlot = milkSlot;
            this.armyEntity.startDrinkingMilkAnimation();
            this.drinkingTicks = armyEntity.getEquipmentSwapTicks();
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.drinkingTicks > 0;
    }

    @Override
    public void tick() {
        if (--this.drinkingTicks > 0) {
            return;
        }

        this.armyEntity.completeDrinkingMilk();
        int offHandSlot = this.armyEntity.getOffHandSlot();
        Container inventory = this.armyEntity.getInventory();
        ItemStack milkStack = inventory.removeItemNoUpdate(offHandSlot);
        ItemStack offHandStack = inventory.removeItemNoUpdate(swapBackSlot);
        inventory.setItem(offHandSlot, offHandStack);
        milkStack.shrink(1);
        this.armyEntity.giveItemStack(milkStack.isEmpty() ? new ItemStack(Items.BUCKET) : milkStack);
    }
}
