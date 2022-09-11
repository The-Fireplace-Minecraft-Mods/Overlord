package dev.the_fireplace.overlord.client.gui.squad;

import dev.the_fireplace.overlord.client.gui.PartialScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class ItemSelectionScreenPart implements PartialScreen
{
    private static final int DISTANCE_BETWEEN_ITEMS = 2;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final Collection<ItemStack> items;
    private final State state;

    private final List<ItemButtonWidget> itemWidgets = new ArrayList<>();
    private Button previousButton;
    private Button nextButton;

    public ItemSelectionScreenPart(int x, int y, int width, int height, Collection<ItemStack> items, State state) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.items = items;
        this.state = state;
        createWidgets();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends GuiEventListener & Widget & NarratableEntry> List<T> getChildren() {
        List<T> children = new ArrayList<>(itemWidgets.size() + 2);
        children.addAll((Collection<? extends T>) itemWidgets);
        children.add((T) nextButton);
        children.add((T) previousButton);

        return children;
    }

    private void createWidgets() {
        createItemWidgets();
        calculateStartingPage();
        updateItemVisibility();
        createPageChangeButtons();
    }

    private void createItemWidgets() {
        itemWidgets.clear();
        int columnIndex = 0;
        int rowIndex = 0;
        int widgetSize = getItemWidgetSize();
        int displayedColumnCount = getColumnCount();
        int displayedRowCount = getRowCount();
        for (ItemStack item : items) {
            int column = columnIndex % displayedColumnCount;
            int row = rowIndex % displayedRowCount;
            int widgetX = x + (column * (widgetSize + DISTANCE_BETWEEN_ITEMS));
            int widgetY = y + 24 + (row * (widgetSize + DISTANCE_BETWEEN_ITEMS));
            ItemButtonWidget itemButtonWidget = new ItemButtonWidget(
                widgetX,
                widgetY,
                widgetSize,
                widgetSize,
                item,
                itemWidget -> {
                    this.state.setSelectedStack(item);
                    notifyChildrenOfItem();
                }
            );
            this.itemWidgets.add(itemButtonWidget);
            columnIndex++;
            if (columnIndex % displayedColumnCount == 0) {
                rowIndex++;
            }
        }
        notifyChildrenOfItem();
    }

    private int getItemAreaHeight() {
        return this.height - 24;
    }

    private int getItemAreaWidth() {
        return this.width;
    }

    private int getItemWidgetSize() {
        return 20;
    }

    private int getColumnCount() {
        return Math.max(1, getItemAreaWidth() / (getItemWidgetSize() + DISTANCE_BETWEEN_ITEMS));
    }

    private int getRowCount() {
        return Math.max(1, getItemAreaHeight() / (getItemWidgetSize() + DISTANCE_BETWEEN_ITEMS));
    }

    private int getItemsPerPage() {
        return getColumnCount() * getRowCount();
    }

    private void updateItemVisibility() {
        for (int index = 0; index < itemWidgets.size(); index++) {
            int itemPage = index / getItemsPerPage();
            itemWidgets.get(index).visible = itemPage == this.state.currentPage;
        }
    }

    private int getPageCount() {
        int itemCount = itemWidgets.size();
        int itemsPerPage = getItemsPerPage();
        return itemCount / itemsPerPage + (itemCount % itemsPerPage == 0 ? 0 : 1);
    }

    private void calculateStartingPage() {
        if (this.state.selectedStack.isEmpty()) {
            this.state.currentPage = 0;
            return;
        }
        int widgetIndex = 0;
        for (ItemButtonWidget itemWidget : this.itemWidgets) {
            if (ItemStack.matches(itemWidget.stack, this.state.selectedStack)) {
                this.state.currentPage = (byte) (widgetIndex / getItemsPerPage());
                return;
            }
            widgetIndex++;
        }
        this.state.currentPage = 0;
    }

    private void createPageChangeButtons() {
        previousButton = new Button(x, y, this.width / 2 - 2, 20, new TranslatableComponent("gui.overlord.create_squad.previous"), buttonWidget -> {
            this.state.currentPage--;
            updatePageChangeButtonUsability();
            updateItemVisibility();
        });
        nextButton = new Button(x + width / 2 + 4, y, this.width / 2 - 6, 20, new TranslatableComponent("gui.overlord.create_squad.next"), buttonWidget -> {
            this.state.currentPage++;
            updatePageChangeButtonUsability();
            updateItemVisibility();
        });
        updatePageChangeButtonUsability();
    }

    private void updatePageChangeButtonUsability() {
        this.previousButton.active = this.state.currentPage > 0;
        this.nextButton.active = this.state.currentPage < getPageCount() - 1;
    }

    private void notifyChildrenOfItem() {
        for (ItemButtonWidget itemWidget : itemWidgets) {
            itemWidget.notifyOfActiveStack(this.state.selectedStack);
        }
    }

    static class State
    {
        private final Consumer<ItemStack> onChanged;
        private byte currentPage = 0;
        private ItemStack selectedStack;

        public State(ItemStack selectedStack, Consumer<ItemStack> onChanged) {
            this.selectedStack = selectedStack;
            this.onChanged = onChanged;
        }

        public void setSelectedStack(ItemStack selectedStack) {
            this.selectedStack = selectedStack;
            this.onChanged.accept(selectedStack);
        }

        public ItemStack getStack() {
            return selectedStack;
        }
    }
}
