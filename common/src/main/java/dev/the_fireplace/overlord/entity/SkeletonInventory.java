package dev.the_fireplace.overlord.entity;

import com.google.common.collect.ImmutableList;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.advancement.OverlordCriterions;
import dev.the_fireplace.overlord.domain.world.ItemDropper;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class SkeletonInventory implements Container, Nameable
{
    public final NonNullList<ItemStack> main;
    public final NonNullList<ItemStack> armor;
    public final NonNullList<ItemStack> mainHand;
    public final NonNullList<ItemStack> offHand;
    public final OwnedSkeletonEntity skeleton;
    private final List<NonNullList<ItemStack>> combinedInventory;

    public static final int HELMET_SLOT = 39;
    public static final int ARMOR_SLOT = 38;
    public static final int LEGGINGS_SLOT = 37;
    public static final int BOOTS_SLOT = 36;
    public static final int MAIN_HAND_SLOT = 40;
    public static final int OFF_HAND_SLOT = 41;
    private int changeCount;

    public SkeletonInventory(OwnedSkeletonEntity skeleton) {
        this.main = NonNullList.withSize(36, ItemStack.EMPTY);
        this.armor = NonNullList.withSize(4, ItemStack.EMPTY);
        this.mainHand = NonNullList.withSize(1, ItemStack.EMPTY);
        this.offHand = NonNullList.withSize(1, ItemStack.EMPTY);
        this.combinedInventory = ImmutableList.of(this.main, this.armor, this.mainHand, this.offHand);
        this.skeleton = skeleton;
    }

    public ItemStack getMainHandStack() {
        return mainHand.get(0);
    }

    private boolean canStackAddMore(ItemStack existingStack, ItemStack stack) {
        return !existingStack.isEmpty() && this.areItemsEqual(existingStack, stack) && existingStack.isStackable() && existingStack.getCount() < existingStack.getMaxStackSize() && existingStack.getCount() < this.getMaxStackSize();
    }

    private boolean areItemsEqual(ItemStack stack1, ItemStack stack2) {
        return stack1.getItem() == stack2.getItem() && ItemStack.tagMatches(stack1, stack2);
    }

    public int getEmptySlot() {
        for (int i = 0; i < this.main.size(); ++i) {
            if (this.main.get(i).isEmpty()) {
                return i;
            }
        }

        return -1;
    }

    public int findSlotIndex(ItemStack itemStack) {
        for (int i = 0; i < this.main.size(); ++i) {
            ItemStack itemStack2 = this.main.get(i);
            if (!this.main.get(i).isEmpty() && this.areItemsEqual(itemStack, this.main.get(i)) && !this.main.get(i).isDamaged() && !itemStack2.isEnchanted() && !itemStack2.hasCustomHoverName()) {
                return i;
            }
        }

        return -1;
    }

    public int method_7369(Predicate<ItemStack> predicate, int i) {
        int j = 0;

        int k;
        for (k = 0; k < this.getContainerSize(); ++k) {
            ItemStack itemStack = this.getItem(k);
            if (!itemStack.isEmpty() && predicate.test(itemStack)) {
                int l = i <= 0 ? itemStack.getCount() : Math.min(i - j, itemStack.getCount());
                j += l;
                if (i != 0) {
                    itemStack.shrink(l);
                    if (itemStack.isEmpty()) {
                        this.setItem(k, ItemStack.EMPTY);
                    }

                    if (i > 0 && j >= i) {
                        return j;
                    }
                }
            }
        }

        return j;
    }

    private int addStack(ItemStack stack) {
        int i = this.getOccupiedSlotWithRoomForStack(stack);
        if (i == -1) {
            i = this.getEmptySlot();
        }

        return i == -1 ? stack.getCount() : this.addStack(i, stack);
    }

    private int addStack(int slot, ItemStack stack) {
        Item item = stack.getItem();
        int stackSize = stack.getCount();
        ItemStack itemStack = this.getItem(slot);
        if (itemStack.isEmpty()) {
            itemStack = new ItemStack(item, 0);
            if (stack.hasTag()) {
                assert stack.getTag() != null;
                itemStack.setTag(stack.getTag().copy());
            }

            this.setItem(slot, itemStack);
        }

        int maxInsertableAmount = Math.min(stackSize, itemStack.getMaxStackSize() - itemStack.getCount());

        if (maxInsertableAmount > this.getMaxStackSize() - itemStack.getCount()) {
            maxInsertableAmount = this.getMaxStackSize() - itemStack.getCount();
        }

        if (maxInsertableAmount != 0) {
            stackSize -= maxInsertableAmount;
            itemStack.grow(maxInsertableAmount);
            triggerAdvancementCheck(itemStack, getEquipmentTypeByIndex(slot));
            itemStack.setPopTime(5);
        }
        return stackSize;
    }

    public int getOccupiedSlotWithRoomForStack(ItemStack stack) {
        if (this.canStackAddMore(this.getItem(MAIN_HAND_SLOT), stack)) {
            return MAIN_HAND_SLOT;
        } else if (this.canStackAddMore(this.getItem(OFF_HAND_SLOT), stack)) {
            return OFF_HAND_SLOT;
        } else {
            for (int i = 0; i < this.main.size(); ++i) {
                if (this.canStackAddMore(this.main.get(i), stack)) {
                    return i;
                }
            }

            return -1;
        }
    }

    public void tickItems() {
        for (NonNullList<ItemStack> itemStacks : this.combinedInventory) {
            for (int i = 0; i < itemStacks.size(); ++i) {
                if (!itemStacks.get(i).isEmpty()) {
                    itemStacks.get(i).inventoryTick(this.skeleton.level, this.skeleton, i, MAIN_HAND_SLOT == i);
                }
            }
        }
    }

    public boolean insertStack(ItemStack stack) {
        return this.insertStack(-1, stack);
    }

    public boolean insertStack(int slot, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        try {
            if (stack.isDamaged()) {
                if (slot == -1) {
                    slot = this.getEmptySlot();
                }

                if (slot >= 0) {
                    ItemStack copy = stack.copy();
                    this.main.set(slot, copy);
                    triggerAdvancementCheck(copy, getEquipmentTypeByIndex(slot));
                    this.main.get(slot).setPopTime(5);
                    stack.setCount(0);
                    return true;
                } else {
                    return false;
                }
            } else {
                int i;
                do {
                    i = stack.getCount();
                    if (slot == -1) {
                        stack.setCount(this.addStack(stack));
                    } else {
                        stack.setCount(this.addStack(slot, stack));
                    }
                } while (!stack.isEmpty() && stack.getCount() < i);

                return stack.getCount() < i;
            }
        } catch (Throwable t) {
            CrashReport crashReport = CrashReport.forThrowable(t, "Adding item to inventory");
            CrashReportCategory crashReportSection = crashReport.addCategory("Item being added");
            crashReportSection.setDetail("Item ID", Item.getId(stack.getItem()));
            crashReportSection.setDetail("Item data", stack.getDamageValue());
            crashReportSection.setDetail("Item name", () -> stack.getHoverName().getString());
            throw new ReportedException(crashReport);
        }
    }

    public void offerOrDrop(Level world, ItemStack stack) {
        if (world.isClientSide) {
            return;
        }
        while (!stack.isEmpty()) {
            int i = this.getOccupiedSlotWithRoomForStack(stack);
            if (i == -1) {
                i = this.getEmptySlot();
            }

            if (i == -1) {
                OverlordConstants.getInjector().getInstance(ItemDropper.class).dropItem(stack, this.skeleton);
                break;
            }

            int j = stack.getMaxStackSize() - this.getItem(i).getCount();
            this.insertStack(i, stack.split(j));
        }
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        Pair<NonNullList<ItemStack>, Integer> listSlot = getInternalInventoryAndSlot(slot);
        NonNullList<ItemStack> list = listSlot.getKey();
        slot = listSlot.getValue();

        return list != null && !list.get(slot).isEmpty() ? ContainerHelper.removeItem(list, slot, amount) : ItemStack.EMPTY;
    }

    public void removeOne(ItemStack stack) {
        for (NonNullList<ItemStack> defaultedList : this.combinedInventory) {
            for (int i = 0; i < defaultedList.size(); ++i) {
                if (defaultedList.get(i) == stack) {
                    defaultedList.set(i, ItemStack.EMPTY);
                    break;
                }
            }
        }
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        Pair<NonNullList<ItemStack>, Integer> listSlot = getInternalInventoryAndSlot(slot);
        NonNullList<ItemStack> defaultedList = listSlot.getKey();
        slot = listSlot.getValue();

        if (defaultedList != null && !defaultedList.get(slot).isEmpty()) {
            ItemStack itemStack = defaultedList.get(slot);
            defaultedList.set(slot, ItemStack.EMPTY);
            return itemStack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void setItem(int slotIndex, ItemStack stack) {
        Pair<NonNullList<ItemStack>, Integer> listSlot = getInternalInventoryAndSlot(slotIndex);
        NonNullList<ItemStack> internalInventory = listSlot.getKey();
        int slotWithinInternalInventory = listSlot.getValue();

        if (internalInventory != null) {
            internalInventory.set(slotWithinInternalInventory, stack);
            triggerAdvancementCheck(stack, getEquipmentTypeByIndex(slotIndex));
        }
    }

    private Pair<NonNullList<ItemStack>, Integer> getInternalInventoryAndSlot(int slotIndex) {
        NonNullList<ItemStack> defaultedList = null;

        NonNullList<ItemStack> defaultedList2;
        for (Iterator<NonNullList<ItemStack>> var4 = this.combinedInventory.iterator(); var4.hasNext(); slotIndex -= defaultedList2.size()) {
            defaultedList2 = var4.next();
            if (slotIndex < defaultedList2.size()) {
                defaultedList = defaultedList2;
                break;
            }
        }
        return Pair.of(defaultedList, slotIndex);
    }

    public float getBlockBreakingSpeed(BlockState block) {
        return this.getMainHandStack().getDestroySpeed(block);
    }

    public ListTag serialize(ListTag tag) {
        int k;
        CompoundTag compoundTag3;
        for (k = 0; k < this.main.size(); ++k) {
            if (!this.main.get(k).isEmpty()) {
                compoundTag3 = new CompoundTag();
                compoundTag3.putByte("Slot", (byte) k);
                this.main.get(k).save(compoundTag3);
                tag.add(compoundTag3);
            }
        }

        for (k = 0; k < this.armor.size(); ++k) {
            if (!this.armor.get(k).isEmpty()) {
                compoundTag3 = new CompoundTag();
                compoundTag3.putByte("Slot", (byte) (k + 100));
                this.armor.get(k).save(compoundTag3);
                tag.add(compoundTag3);
            }
        }

        for (k = 0; k < this.mainHand.size(); ++k) {
            if (!this.mainHand.get(k).isEmpty()) {
                compoundTag3 = new CompoundTag();
                compoundTag3.putByte("Slot", (byte) (k + 150));
                this.mainHand.get(k).save(compoundTag3);
                tag.add(compoundTag3);
            }
        }

        for (k = 0; k < this.offHand.size(); ++k) {
            if (!this.offHand.get(k).isEmpty()) {
                compoundTag3 = new CompoundTag();
                compoundTag3.putByte("Slot", (byte) (k + 200));
                this.offHand.get(k).save(compoundTag3);
                tag.add(compoundTag3);
            }
        }

        return tag;
    }

    public void deserialize(ListTag tag) {
        this.main.clear();
        this.armor.clear();
        this.mainHand.clear();
        this.offHand.clear();

        for (int i = 0; i < tag.size(); ++i) {
            CompoundTag compoundTag = tag.getCompound(i);
            int j = compoundTag.getByte("Slot") & 255;
            ItemStack itemStack = ItemStack.of(compoundTag);
            if (!itemStack.isEmpty()) {
                if (j < this.main.size()) {
                    this.main.set(j, itemStack);
                } else if (j >= 100 && j < this.armor.size() + 100) {
                    this.armor.set(j - 100, itemStack);
                } else if (j >= 150 && j < this.mainHand.size() + 150) {
                    this.mainHand.set(j - 150, itemStack);
                } else if (j >= 200 && j < this.offHand.size() + 200) {
                    this.offHand.set(j - 200, itemStack);
                }
            }
        }

    }

    @Override
    public int getContainerSize() {
        return this.main.size() + this.armor.size() + this.mainHand.size() + this.offHand.size();
    }

    @Override
    public boolean isEmpty() {
        Iterator<ItemStack> var1 = this.main.iterator();

        ItemStack itemStack3;
        do {
            if (!var1.hasNext()) {
                var1 = this.armor.iterator();

                do {
                    if (!var1.hasNext()) {
                        var1 = this.offHand.iterator();

                        do {
                            if (!var1.hasNext()) {
                                return true;
                            }

                            itemStack3 = var1.next();
                        } while (itemStack3.isEmpty());

                        return false;
                    }

                    itemStack3 = var1.next();
                } while (itemStack3.isEmpty());

                return false;
            }

            itemStack3 = var1.next();
        } while (itemStack3.isEmpty());

        return false;
    }

    @Override
    public ItemStack getItem(int slot) {
        Pair<NonNullList<ItemStack>, Integer> listSlot = getInternalInventoryAndSlot(slot);
        NonNullList<ItemStack> list = listSlot.getKey();
        slot = listSlot.getValue();

        return list == null ? ItemStack.EMPTY : list.get(slot);
    }

    @Override
    public Component getName() {
        return new TranslatableComponent("container.inventory");
    }

    public boolean isUsingEffectiveTool(BlockState blockState) {
        return this.getMainHandStack().isCorrectToolForDrops(blockState);
    }

    public ItemStack getArmorStack(int slot) {
        return this.armor.get(slot);
    }

    public void damageArmor(float armorDamage) {
        if (armorDamage > 0.0F) {
            armorDamage /= 4.0F;
            if (armorDamage < 1.0F) {
                armorDamage = 1.0F;
            }

            for (ItemStack itemStack : this.armor) {
                if (itemStack.getItem() instanceof ArmorItem) {
                    itemStack.hurtAndBreak((int) armorDamage, this.skeleton, (skeleton) -> {
                        //TODO make sure this works
                        skeleton.broadcastBreakEvent(EquipmentSlot.values()[2 + this.armor.indexOf(itemStack)]);
                    });
                }
            }
        }
    }

    public void dropAll() {
        for (NonNullList<ItemStack> itemStacks : this.combinedInventory) {
            for (int i = 0; i < itemStacks.size(); ++i) {
                ItemStack itemStack = itemStacks.get(i);
                if (!itemStack.isEmpty()) {
                    OverlordConstants.getInjector().getInstance(ItemDropper.class).dropItem(itemStack, this.skeleton);
                    itemStacks.set(i, ItemStack.EMPTY);
                }
            }
        }
    }

    @Override
    public void setChanged() {
        ++this.changeCount;
    }

    public int getChangeCount() {
        return this.changeCount;
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.skeleton.isRemoved()) {
            return false;
        } else {
            return player.distanceToSqr(this.skeleton) <= 8.0D;
        }
    }

    public boolean contains(ItemStack stack) {
        for (NonNullList<ItemStack> itemStacks : this.combinedInventory) {
            for (ItemStack itemStack : itemStacks) {
                if (!itemStack.isEmpty() && itemStack.sameItem(stack)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean contains(Tag<Item> tag) {
        for (NonNullList<ItemStack> itemStacks : this.combinedInventory) {
            for (ItemStack itemStack : itemStacks) {
                if (!itemStack.isEmpty() && itemStack.is(tag)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void clone(SkeletonInventory other) {
        for (int i = 0; i < this.getContainerSize(); ++i) {
            this.setItem(i, other.getItem(i));
        }
    }

    @Override
    public void clearContent() {
        for (NonNullList<ItemStack> itemStacks : this.combinedInventory) {
            itemStacks.clear();
        }
    }

    @Nullable
    public EquipmentSlot getEquipmentTypeByIndex(int slotIndex) {
        return switch (slotIndex) {
            case MAIN_HAND_SLOT -> EquipmentSlot.MAINHAND;
            case OFF_HAND_SLOT -> EquipmentSlot.OFFHAND;
            case HELMET_SLOT -> EquipmentSlot.HEAD;
            case ARMOR_SLOT -> EquipmentSlot.CHEST;
            case LEGGINGS_SLOT -> EquipmentSlot.LEGS;
            case BOOTS_SLOT -> EquipmentSlot.FEET;
            default -> null;
        };
    }

    protected void triggerAdvancementCheck(ItemStack addedStack, @Nullable EquipmentSlot equipmentSlot) {
        if (this.skeleton.getOwner() instanceof ServerPlayer player) {
            OverlordCriterions.SKELETON_INVENTORY_CHANGED.trigger(player, this, addedStack, equipmentSlot);
        }
    }
}
