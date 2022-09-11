package dev.the_fireplace.overlord.compat.rei;

import com.google.common.collect.Lists;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Set;

public final class SkeletonBuildingCategory implements DisplayCategory<SkeletonBuildingDisplay>
{
    @Override
    public Renderer getIcon() {
        return EntryStacks.of(Items.SKELETON_SKULL);
    }

    @Override
    public Component getTitle() {
        return new TranslatableComponent("gui.overlord.rei.skeleton_building");
    }

    @Override
    public CategoryIdentifier<SkeletonBuildingDisplay> getCategoryIdentifier() {
        return OverlordReiCategories.SKELETON_BUILDING_CATEGORY;
    }

    @Override
    public List<Widget> setupDisplay(SkeletonBuildingDisplay display, Rectangle bounds) {
        List<Widget> widgets = Lists.newArrayList();

        // The base background of the display
        // Please try to not remove this to preserve an uniform style to REI
        widgets.add(Widgets.createRecipeBase(bounds));

        int borderOffset = 4;
        int startX = bounds.getMinX() + borderOffset;
        int startY = bounds.getMinY() + borderOffset;
        int endX = bounds.getMaxX() - borderOffset;

        // Create input area
        int inputIndex = 0;
        for (int y = 0; y < 7; ++y) {
            int sectionXSlots = y < 3 ? 8 : 7;
            for (int x = 0; x < sectionXSlots; ++x) {
                Slot slot = Widgets.createSlot(new Point(startX + x * 18, startY + y * 18)).markInput();
                if (inputIndex < display.getInputEntries().size()) {
                    slot.entries(display.getInputEntries().get(inputIndex++));
                }
                widgets.add(slot);
            }
        }

        // Create byproduct area
        int byproductIndex = 0;
        for (int y = 0; y < 7; ++y) {
            int sectionXSlots = y < 3 ? 8 : 7;
            for (int x = sectionXSlots; x > 0; --x) {
                Slot slot = Widgets.createSlot(new Point(endX - x * 18, startY + y * 18)).markOutput();
                if (byproductIndex < display.getOutputEntries().size()) {
                    slot.entries(display.getOutputEntries().get(byproductIndex++));
                }
                widgets.add(slot);
            }
        }

        // The gray arrow
        widgets.add(Widgets.createArrow(new Point(bounds.getCenterX() - 12, bounds.getCenterY() + 2 * 18)));

        Point skullSlotStartPoint = new Point(bounds.getCenterX() - 8, bounds.getCenterY());
        widgets.add(Widgets.createResultSlotBackground(skullSlotStartPoint));

        widgets.add(
            Widgets.createSlot(skullSlotStartPoint)
                .notInteractable()
                .notFavoritesInteractable()
                .disableHighlight()
                .disableBackground()
                .entries(Set.of(display.getSkeletonHead()))
        );

        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 140;
    }

    @Override
    public int getDisplayWidth(SkeletonBuildingDisplay display) {
        return 300;
    }

    @Override
    public int getMaximumDisplaysPerPage() {
        return 1;
    }

    @Override
    public int getFixedDisplaysPerPage() {
        return 1;
    }
}
