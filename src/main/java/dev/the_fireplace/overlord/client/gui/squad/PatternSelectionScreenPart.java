package dev.the_fireplace.overlord.client.gui.squad;

import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.client.gui.PartialScreen;
import dev.the_fireplace.overlord.util.SquadPatterns;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Environment(EnvType.CLIENT)
public class PatternSelectionScreenPart implements PartialScreen
{
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    private final List<PatternButtonWidget> patternWidgets = new ArrayList<>();
    private byte patternPage = 0;
    private ButtonWidget previousButton;
    private ButtonWidget nextButton;
    private Identifier selectedPattern = new Identifier("");

    public PatternSelectionScreenPart(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        createWidgets();
        setPatternVisibility();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Element & Drawable & Selectable> List<T> getChildren() {
        List<T> children = new ArrayList<>(patternWidgets.size() + 2);
        children.addAll((Collection<? extends T>) patternWidgets);
        children.add((T) nextButton);
        children.add((T) previousButton);

        return children;
    }

    private void createWidgets() {
        previousButton = new ButtonWidget(x, y, this.width / 2 - 2, 20, new TranslatableText("gui.overlord.create_squad.previous"), buttonWidget -> {
            if (--this.patternPage <= 0) {
                buttonWidget.active = false;
            }
            if (!nextButton.active) {
                nextButton.active = true;
            }
            setPatternVisibility();
        });
        previousButton.active = false;
        nextButton = new ButtonWidget(x + width / 2 + 4, y, this.width / 2 - 6, 20, new TranslatableText("gui.overlord.create_squad.next"), buttonWidget -> {
            if (++this.patternPage >= getPageCount() - 1) {
                buttonWidget.active = false;
            }
            if (!previousButton.active) {
                previousButton.active = true;
            }
            setPatternVisibility();
        });
        createPatternWidgets();
        nextButton.active = patternWidgets.size() > getPatternsPerPage();
    }

    private void createPatternWidgets() {
        patternWidgets.clear();
        int columnIndex = 0;
        int rowIndex = 0;
        int patternAreaWidth = getPatternAreaWidth();
        int patternAreaHeight = getPatternAreaHeight();
        int widgetWidth = getPatternWidgetWidth(patternAreaWidth);
        int widgetHeight = getPatternWidgetHeight(patternAreaHeight);
        int displayedColumnCount = getColumnCount(patternAreaWidth);
        int displayedRowCount = getRowCount(patternAreaHeight);
        for (String pattern : SquadPatterns.getPatterns()) {
            int column = columnIndex % displayedColumnCount;
            int row = rowIndex % displayedRowCount;
            int widgetX = x + (column * (widgetWidth + 4));
            int widgetY = y + 24 + (row * (widgetHeight + 4));
            Text widgetName = new TranslatableText("squad.overlord.pattern." + pattern + ".name");
            Identifier patternId = new Identifier(Overlord.MODID, pattern);
            PatternButtonWidget patternButtonWidget = new PatternButtonWidget(
                widgetX,
                widgetY,
                widgetWidth,
                widgetHeight,
                widgetName,
                patternId,
                patternWidget -> {
                    this.selectedPattern = patternId;
                    updateActivePattern();
                }
            );
            this.patternWidgets.add(patternButtonWidget);
            columnIndex++;
            if (columnIndex % displayedColumnCount == 0) {
                rowIndex++;
            }
        }
    }

    private int getPatternAreaHeight() {
        return this.height - 24;
    }

    private int getPatternAreaWidth() {
        return this.width;
    }

    private int getPatternWidgetHeight(int patternAreaHeight) {
        return patternAreaHeight / getRowCount(patternAreaHeight) - 4;
    }

    private int getPatternWidgetWidth(int patternAreaWidth) {
        return patternAreaWidth / getColumnCount(patternAreaWidth) - 4;
    }

    private int getColumnCount(int patternAreaWidth) {
        int minimumPatternWidth = 70;
        return Math.max(1, patternAreaWidth / minimumPatternWidth);
    }

    private int getRowCount(int patternAreaHeight) {
        int minimumPatternHeight = 70;
        return Math.max(1, patternAreaHeight / minimumPatternHeight);
    }

    private int getPatternsPerPage() {
        return getColumnCount(getPatternAreaWidth()) * getRowCount(getPatternAreaHeight());
    }

    private void setPatternVisibility() {
        for (int index = 0; index < patternWidgets.size(); index++) {
            int patternPage = index / getPatternsPerPage();
            patternWidgets.get(index).visible = patternPage == this.patternPage;
        }
    }

    private int getPageCount() {
        int patternCount = patternWidgets.size();
        int patternsPerPage = getPatternsPerPage();
        return patternCount / patternsPerPage + (patternCount % patternsPerPage == 0 ? 0 : 1);
    }

    private void updateActivePattern() {
        for (PatternButtonWidget patternWidget : patternWidgets) {
            patternWidget.notifyOfActivePattern(this.selectedPattern);
        }
    }

    public Identifier getSelectedPatternId() {
        return selectedPattern;
    }
}
