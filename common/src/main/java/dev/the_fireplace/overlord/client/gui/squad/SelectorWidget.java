package dev.the_fireplace.overlord.client.gui.squad;

import com.google.common.collect.Lists;
import com.google.inject.Injector;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.the_fireplace.lib.api.chat.injectables.TranslatorFactory;
import dev.the_fireplace.lib.api.chat.interfaces.Translator;
import dev.the_fireplace.lib.api.uuid.injectables.EmptyUUID;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.client.util.ClientSquad;
import dev.the_fireplace.overlord.domain.data.objects.Squad;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class SelectorWidget extends ObjectSelectionList<SelectorEntry>
{
    private final EmptyUUID emptyUUID;
    private final Squad noneSquad;
    private final SelectorEntry noneEntry;
    private final Consumer<UUID> onSquadUpdated;
    private boolean scrolling;

    public SelectorWidget(Minecraft minecraftClient, int width, int height, int top, int bottom, int itemHeight, Consumer<UUID> onSquadUpdated) {
        super(minecraftClient, width, height, top, bottom, itemHeight);
        this.onSquadUpdated = onSquadUpdated;
        this.setRenderBackground(false);

        Injector injector = OverlordConstants.getInjector();
        TranslatorFactory translatorFactory = injector.getInstance(TranslatorFactory.class);
        Translator translator = translatorFactory.getTranslator(OverlordConstants.MODID);

        this.emptyUUID = injector.getInstance(EmptyUUID.class);
        this.noneSquad = new ClientSquad(
            emptyUUID.get(),
            emptyUUID.get(),
            new ResourceLocation(""),
            ItemStack.EMPTY,
            translator.getTranslatedString("gui.overlord.squad_manager.none")
        );
        this.noneEntry = new SelectorEntry(noneSquad);
        this.addEntry(noneEntry);
    }

    public void addSquads(Collection<? extends Squad> squads) {
        for (Squad squad : squads) {
            this.addEntry(new SelectorEntry(squad));
        }
    }

    public void removeSquad(Squad squad) {
        Collection<SelectorEntry> removeEntries = new HashSet<>();
        this.children().stream()
            .filter(selectorEntry -> selectorEntry.squad.getSquadId().equals(squad.getSquadId()))
            .forEach(removeEntries::add);
        List<SelectorEntry> childrenToKeep = Lists.newArrayList(this.children());
        childrenToKeep.removeAll(removeEntries);
        this.replaceEntries(childrenToKeep);
    }

    public void selectSquad(UUID squadId) {
        Optional<SelectorEntry> firstMatchingSquad = this.children().stream().filter(entry -> entry.hasId(squadId)).findFirst();
        this.setSelected(firstMatchingSquad.orElse(noneEntry));
    }


    @Override
    protected void renderList(PoseStack matrices, int mouseX, int mouseY, float delta) {
        int itemCount = this.getItemCount();

        for (int index = 0; index < itemCount; ++index) {
            int entryTop = this.getRowTop(index) + 2;
            int entryBottom = this.getRowTop(index) + this.itemHeight;
            if (entryBottom >= this.y0 && entryBottom <= this.y1 + 20) {
                int entryHeight = this.itemHeight - 4;
                SelectorEntry entry = this.getEntry(index);
                int rowWidth = this.getRowWidth();
                int entryLeft = this.getRowLeft();
                boolean hovering = this.isMouseOver(mouseX, mouseY) && Objects.equals(this.getEntryAtPos(mouseX, mouseY), entry);
                entry.render(matrices, index, entryTop, entryLeft, rowWidth, entryHeight, mouseX, mouseY, hovering, delta);
            }
        }
    }

    @Override
    protected void updateScrollingState(double mouseX, double mouseY, int button) {
        super.updateScrollingState(mouseX, mouseY, button);
        this.scrolling = button == 0
            && mouseX >= (double) this.getScrollbarPosition()
            && mouseX < (double) (this.getScrollbarPosition() + 6);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.updateScrollingState(mouseX, mouseY, button);
        if (!this.isMouseOver(mouseX, mouseY)) {
            return false;
        } else {
            SelectorEntry entry = this.getEntryAtPos(mouseX, mouseY);
            if (entry != null) {
                if (entry.mouseClicked(mouseX, mouseY, button)) {
                    this.setFocused(entry);
                    this.setDragging(true);
                    this.setSelected(entry);
                    return true;
                }
            } else if (button == 0) {
                this.clickedHeader((int) (mouseX - (double) (this.x0 + this.width / 2 - this.getRowWidth() / 2)), (int) (mouseY - (double) this.y0) + (int) this.getScrollAmount() - 4);
                return true;
            }

            return this.scrolling;
        }
    }

    public final SelectorEntry getEntryAtPos(double x, double y) {
        int int_5 = Mth.floor(y - (double) this.y0) - this.headerHeight + (int) this.getScrollAmount() - 4;
        int index = int_5 / this.itemHeight;
        return x < (double) this.getScrollbarPosition()
            && x >= (double) getRowLeft()
            && x <= (double) (getRowLeft() + getRowWidth())
            && index >= 0
            && int_5 >= 0
            && index < this.getItemCount()
            ? this.children().get(index)
            : null;
    }

    @Override
    protected int getScrollbarPosition() {
        return this.width - 6;
    }

    @Override
    public int getRowWidth() {
        return this.width - (Math.max(0, this.getMaxPosition() - (this.y1 - this.y0 - 4)) > 0 ? 18 : 12);
    }

    @Override
    public int getRowLeft() {
        return x0 + 6;
    }

    public int getWidth() {
        return width;
    }

    public int getTop() {
        return this.y0;
    }

    @Override
    protected int getMaxPosition() {
        return super.getMaxPosition() + 4;
    }

    @Override
    public void setSelected(@Nullable SelectorEntry entry) {
        super.setSelected(entry);
        boolean foundSelection = false;
        for (SelectorEntry checkEntry : children()) {
            if (!checkEntry.equals(entry)) {
                checkEntry.setSelected(false);
            } else {
                foundSelection = true;
                checkEntry.setSelected(true);
                onSquadUpdated.accept(checkEntry.getSquadId());
            }
        }
        if (!foundSelection) {
            noneEntry.setSelected(true);
            onSquadUpdated.accept(noneEntry.getSquadId());
        }
    }
}
