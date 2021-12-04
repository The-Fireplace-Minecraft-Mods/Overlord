package dev.the_fireplace.overlord.client.gui.squad;

import dev.the_fireplace.overlord.client.gui.PartialScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableText;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ItemSelectionScreenPart implements PartialScreen
{
    private static final int DISTANCE_BETWEEN_ITEMS = 2;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final Collection<ItemStack> items;

    private final List<ItemButtonWidget> itemWidgets = new ArrayList<>();
    private byte itemPage = 0;
    private ButtonWidget previousButton;
    private ButtonWidget nextButton;
    private ItemStack selectedStack = ItemStack.EMPTY;

    public ItemSelectionScreenPart(int x, int y, int width, int height, Collection<ItemStack> items) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.items = items;
        createWidgets();
        setItemVisibility();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Element & Drawable & Selectable> List<T> getChildren() {
        List<T> children = new ArrayList<>(itemWidgets.size() + 2);
        children.addAll((Collection<? extends T>) itemWidgets);
        children.add((T) nextButton);
        children.add((T) previousButton);

        return children;
    }

    private void createWidgets() {
        previousButton = new ButtonWidget(x, y, this.width / 2 - 2, 20, new TranslatableText("gui.overlord.create_squad.previous"), buttonWidget -> {
            if (--this.itemPage <= 0) {
                buttonWidget.active = false;
            }
            if (!nextButton.active) {
                nextButton.active = true;
            }
            setItemVisibility();
        });
        previousButton.active = false;
        nextButton = new ButtonWidget(x + width / 2 + 4, y, this.width / 2 - 6, 20, new TranslatableText("gui.overlord.create_squad.next"), buttonWidget -> {
            if (++this.itemPage >= getPageCount() - 1) {
                buttonWidget.active = false;
            }
            if (!previousButton.active) {
                previousButton.active = true;
            }
            setItemVisibility();
        });
        createItemWidgets();
        nextButton.active = itemWidgets.size() > getItemsPerPage();
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
                    this.selectedStack = item;
                    updateActiveItem();
                }
            );
            this.itemWidgets.add(itemButtonWidget);
            columnIndex++;
            if (columnIndex % displayedColumnCount == 0) {
                rowIndex++;
            }
        }
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

    private void setItemVisibility() {
        for (int index = 0; index < itemWidgets.size(); index++) {
            int itemPage = index / getItemsPerPage();
            itemWidgets.get(index).visible = itemPage == this.itemPage;
        }
    }

    private int getPageCount() {
        int itemCount = itemWidgets.size();
        int itemsPerPage = getItemsPerPage();
        return itemCount / itemsPerPage + (itemCount % itemsPerPage == 0 ? 0 : 1);
    }

    private void updateActiveItem() {
        for (ItemButtonWidget itemWidget : itemWidgets) {
            itemWidget.notifyOfActiveStack(this.selectedStack);
        }
    }

    public ItemStack getSelectedStack() {
        return selectedStack;
    }
}
