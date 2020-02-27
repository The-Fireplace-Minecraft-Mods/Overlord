package the_fireplace.overlord.fabric.entity;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.tag.Tag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Nameable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class SkeletonInventory implements Inventory, Nameable {
    public final DefaultedList<ItemStack> main;
    public final DefaultedList<ItemStack> armor;
    public final DefaultedList<ItemStack> mainHand;
    public final DefaultedList<ItemStack> offHand;
    public final OwnedSkeletonEntity skeleton;
    private final List<DefaultedList<ItemStack>> combinedInventory;
    public static final int MAIN_HAND_SLOT = 40;
    public static final int OFF_HAND_SLOT = 41;
    private int changeCount;

    public SkeletonInventory(OwnedSkeletonEntity skeleton) {
        this.main = DefaultedList.ofSize(36, ItemStack.EMPTY);
        this.armor = DefaultedList.ofSize(4, ItemStack.EMPTY);
        this.mainHand = DefaultedList.ofSize(1, ItemStack.EMPTY);
        this.offHand = DefaultedList.ofSize(1, ItemStack.EMPTY);
        this.combinedInventory = ImmutableList.of(this.main, this.armor, this.mainHand, this.offHand);
        this.skeleton = skeleton;
    }

    public ItemStack getMainHandStack() {
        return mainHand.get(0);
    }

    private boolean canStackAddMore(ItemStack existingStack, ItemStack stack) {
        return !existingStack.isEmpty() && this.areItemsEqual(existingStack, stack) && existingStack.isStackable() && existingStack.getCount() < existingStack.getMaxCount() && existingStack.getCount() < this.getInvMaxStackAmount();
    }

    private boolean areItemsEqual(ItemStack stack1, ItemStack stack2) {
        return stack1.getItem() == stack2.getItem() && ItemStack.areTagsEqual(stack1, stack2);
    }

    public int getEmptySlot() {
        for(int i = 0; i < this.main.size(); ++i)
            if (this.main.get(i).isEmpty())
                return i;

        return -1;
    }

    public int findSlotIndex(ItemStack itemStack) {
        for(int i = 0; i < this.main.size(); ++i) {
            ItemStack itemStack2 = this.main.get(i);
            if (!this.main.get(i).isEmpty() && this.areItemsEqual(itemStack, this.main.get(i)) && !this.main.get(i).isDamaged() && !itemStack2.hasEnchantments() && !itemStack2.hasCustomName()) {
                return i;
            }
        }

        return -1;
    }

    public int method_7369(Predicate<ItemStack> predicate, int i) {
        int j = 0;

        int k;
        for(k = 0; k < this.getInvSize(); ++k) {
            ItemStack itemStack = this.getInvStack(k);
            if (!itemStack.isEmpty() && predicate.test(itemStack)) {
                int l = i <= 0 ? itemStack.getCount() : Math.min(i - j, itemStack.getCount());
                j += l;
                if (i != 0) {
                    itemStack.decrement(l);
                    if (itemStack.isEmpty())
                        this.setInvStack(k, ItemStack.EMPTY);

                    if (i > 0 && j >= i)
                        return j;
                }
            }
        }

        return j;
    }

    private int addStack(ItemStack stack) {
        int i = this.getOccupiedSlotWithRoomForStack(stack);
        if (i == -1)
            i = this.getEmptySlot();

        return i == -1 ? stack.getCount() : this.addStack(i, stack);
    }

    private int addStack(int slot, ItemStack stack) {
        Item item = stack.getItem();
        int i = stack.getCount();
        ItemStack itemStack = this.getInvStack(slot);
        if (itemStack.isEmpty()) {
            itemStack = new ItemStack(item, 0);
            if (stack.hasTag()) {
                assert stack.getTag() != null;
                itemStack.setTag(stack.getTag().copy());
            }

            this.setInvStack(slot, itemStack);
        }

        int j = i;
        if (i > itemStack.getMaxCount() - itemStack.getCount())
            j = itemStack.getMaxCount() - itemStack.getCount();

        if (j > this.getInvMaxStackAmount() - itemStack.getCount())
            j = this.getInvMaxStackAmount() - itemStack.getCount();

        if (j != 0) {
            i -= j;
            itemStack.increment(j);
            itemStack.setCooldown(5);
        }
        return i;
    }

    public int getOccupiedSlotWithRoomForStack(ItemStack stack) {
        if (this.canStackAddMore(this.getInvStack(MAIN_HAND_SLOT), stack)) {
            return MAIN_HAND_SLOT;
        } else if (this.canStackAddMore(this.getInvStack(OFF_HAND_SLOT), stack)) {
            return OFF_HAND_SLOT;
        } else {
            for(int i = 0; i < this.main.size(); ++i)
                if (this.canStackAddMore(this.main.get(i), stack))
                    return i;

            return -1;
        }
    }

    public void tickItems() {
        for (DefaultedList<ItemStack> itemStacks : this.combinedInventory)
            for (int i = 0; i < itemStacks.size(); ++i)
                if (!itemStacks.get(i).isEmpty())
                    itemStacks.get(i).inventoryTick(this.skeleton.world, this.skeleton, i, MAIN_HAND_SLOT == i);
    }

    public boolean insertStack(ItemStack stack) {
        return this.insertStack(-1, stack);
    }

    public boolean insertStack(int slot, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        } else {
            try {
                if (stack.isDamaged()) {
                    if (slot == -1)
                        slot = this.getEmptySlot();

                    if (slot >= 0) {
                        this.main.set(slot, stack.copy());
                        this.main.get(slot).setCooldown(5);
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
                    } while(!stack.isEmpty() && stack.getCount() < i);

                    return stack.getCount() < i;
                }
            } catch (Throwable t) {
                CrashReport crashReport = CrashReport.create(t, "Adding item to inventory");
                CrashReportSection crashReportSection = crashReport.addElement("Item being added");
                crashReportSection.add("Item ID", Item.getRawId(stack.getItem()));
                crashReportSection.add("Item data", stack.getDamage());
                crashReportSection.add("Item name", () -> stack.getName().getString());
                throw new CrashException(crashReport);
            }
        }
    }

    public void offerOrDrop(World world, ItemStack stack) {
        if (!world.isClient) {
            while(!stack.isEmpty()) {
                int i = this.getOccupiedSlotWithRoomForStack(stack);
                if (i == -1)
                    i = this.getEmptySlot();

                if (i == -1) {
                    this.skeleton.dropItem(stack, false);
                    break;
                }

                int j = stack.getMaxCount() - this.getInvStack(i).getCount();
                this.insertStack(i, stack.split(j));
            }
        }
    }

    @Override
    public ItemStack takeInvStack(int slot, int amount) {
        Pair<DefaultedList<ItemStack>, Integer> listSlot = getInvAndSlot(slot);
        DefaultedList<ItemStack> list = listSlot.getKey();
        slot = listSlot.getValue();

        return list != null && !list.get(slot).isEmpty() ? Inventories.splitStack(list, slot, amount) : ItemStack.EMPTY;
    }

    public void removeOne(ItemStack stack) {
        for (DefaultedList<ItemStack> defaultedList : this.combinedInventory) {
            for (int i = 0; i < defaultedList.size(); ++i) {
                if (defaultedList.get(i) == stack) {
                    defaultedList.set(i, ItemStack.EMPTY);
                    break;
                }
            }
        }
    }

    @Override
    public ItemStack removeInvStack(int slot) {
        Pair<DefaultedList<ItemStack>, Integer> listSlot = getInvAndSlot(slot);
        DefaultedList<ItemStack> defaultedList = listSlot.getKey();
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
    public void setInvStack(int slot, ItemStack stack) {
        Pair<DefaultedList<ItemStack>, Integer> listSlot = getInvAndSlot(slot);
        DefaultedList<ItemStack> defaultedList = listSlot.getKey();
        slot = listSlot.getValue();

        if (defaultedList != null)
            defaultedList.set(slot, stack);
    }

    private Pair<DefaultedList<ItemStack>, Integer> getInvAndSlot(int slotIndex) {
        DefaultedList<ItemStack> defaultedList = null;

        DefaultedList<ItemStack> defaultedList2;
        for(Iterator<DefaultedList<ItemStack>> var4 = this.combinedInventory.iterator(); var4.hasNext(); slotIndex -= defaultedList2.size()) {
            defaultedList2 = var4.next();
            if (slotIndex < defaultedList2.size()) {
                defaultedList = defaultedList2;
                break;
            }
        }
        return Pair.of(defaultedList, slotIndex);
    }

    public float getBlockBreakingSpeed(BlockState block) {
        return this.getMainHandStack().getMiningSpeed(block);
    }

    public ListTag serialize(ListTag tag) {
        int k;
        CompoundTag compoundTag3;
        for(k = 0; k < this.main.size(); ++k) {
            if (!this.main.get(k).isEmpty()) {
                compoundTag3 = new CompoundTag();
                compoundTag3.putByte("Slot", (byte)k);
                this.main.get(k).toTag(compoundTag3);
                tag.add(compoundTag3);
            }
        }

        for(k = 0; k < this.armor.size(); ++k) {
            if (!this.armor.get(k).isEmpty()) {
                compoundTag3 = new CompoundTag();
                compoundTag3.putByte("Slot", (byte)(k + 100));
                this.armor.get(k).toTag(compoundTag3);
                tag.add(compoundTag3);
            }
        }

        for(k = 0; k < this.mainHand.size(); ++k) {
            if (!this.mainHand.get(k).isEmpty()) {
                compoundTag3 = new CompoundTag();
                compoundTag3.putByte("Slot", (byte)(k + 150));
                this.mainHand.get(k).toTag(compoundTag3);
                tag.add(compoundTag3);
            }
        }

        for(k = 0; k < this.offHand.size(); ++k) {
            if (!this.offHand.get(k).isEmpty()) {
                compoundTag3 = new CompoundTag();
                compoundTag3.putByte("Slot", (byte)(k + 200));
                this.offHand.get(k).toTag(compoundTag3);
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

        for(int i = 0; i < tag.size(); ++i) {
            CompoundTag compoundTag = tag.getCompound(i);
            int j = compoundTag.getByte("Slot") & 255;
            ItemStack itemStack = ItemStack.fromTag(compoundTag);
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
    public int getInvSize() {
        return this.main.size() + this.armor.size() + this.mainHand.size() + this.offHand.size();
    }

    @Override
    public boolean isInvEmpty() {
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
                        } while(itemStack3.isEmpty());

                        return false;
                    }

                    itemStack3 = var1.next();
                } while(itemStack3.isEmpty());

                return false;
            }

            itemStack3 = var1.next();
        } while(itemStack3.isEmpty());

        return false;
    }

    @Override
    public ItemStack getInvStack(int slot) {
        Pair<DefaultedList<ItemStack>, Integer> listSlot = getInvAndSlot(slot);
        DefaultedList<ItemStack> list = listSlot.getKey();
        slot = listSlot.getValue();

        return list == null ? ItemStack.EMPTY : list.get(slot);
    }

    @Override
    public Text getName() {
        return new TranslatableText("container.inventory");
    }

    public boolean isUsingEffectiveTool(BlockState blockState) {
        return this.getMainHandStack().isEffectiveOn(blockState);
    }

    public ItemStack getArmorStack(int slot) {
        return this.armor.get(slot);
    }

    public void damageArmor(float armor) {
        if (armor > 0.0F) {
            armor /= 4.0F;
            if (armor < 1.0F) {
                armor = 1.0F;
            }

            for (ItemStack itemStack : this.armor) {
                if (itemStack.getItem() instanceof ArmorItem) {
                    itemStack.damage((int) armor, this.skeleton, (skeleton) -> {
                        //TODO update clients that the armor broke if needed
                    });
                }
            }

        }
    }

    public void dropAll() {
        for (DefaultedList<ItemStack> itemStacks : this.combinedInventory) {
            for (int i = 0; i < itemStacks.size(); ++i) {
                ItemStack itemStack = itemStacks.get(i);
                if (!itemStack.isEmpty()) {
                    this.skeleton.dropItem(itemStack, true, false);
                    itemStacks.set(i, ItemStack.EMPTY);
                }
            }
        }
    }

    @Override
    public void markDirty() {
        ++this.changeCount;
    }

    public int getChangeCount() {
        return this.changeCount;
    }

    @Override
    public boolean canPlayerUseInv(PlayerEntity player) {
        if (this.skeleton.removed)
            return false;
        else
            return player.squaredDistanceTo(this.skeleton) <= 8.0D;
    }

    public boolean contains(ItemStack stack) {
        for (DefaultedList<ItemStack> itemStacks : this.combinedInventory)
            for (ItemStack itemStack : itemStacks)
                if (!itemStack.isEmpty() && itemStack.isItemEqualIgnoreDamage(stack))
                    return true;

        return false;
    }

    public boolean contains(Tag<Item> tag) {
        for (DefaultedList<ItemStack> itemStacks : this.combinedInventory)
            for (ItemStack itemStack : itemStacks)
                if (!itemStack.isEmpty() && tag.contains(itemStack.getItem()))
                    return true;

        return false;
    }

    public void clone(SkeletonInventory other) {
        for(int i = 0; i < this.getInvSize(); ++i)
            this.setInvStack(i, other.getInvStack(i));
    }

    @Override
    public void clear() {
        for (DefaultedList<ItemStack> itemStacks : this.combinedInventory)
            itemStacks.clear();
    }
}
