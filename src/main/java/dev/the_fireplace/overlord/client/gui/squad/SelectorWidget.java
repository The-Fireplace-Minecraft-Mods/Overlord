package dev.the_fireplace.overlord.client.gui.squad;

import dev.the_fireplace.annotateddi.api.DIContainer;
import dev.the_fireplace.lib.api.chat.injectables.TranslatorFactory;
import dev.the_fireplace.lib.api.chat.interfaces.Translator;
import dev.the_fireplace.lib.api.uuid.injectables.EmptyUUID;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.client.util.ClientSquad;
import dev.the_fireplace.overlord.domain.data.objects.Squad;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class SelectorWidget extends AlwaysSelectedEntryListWidget<SelectorEntry>
{
    private final EmptyUUID emptyUUID;
    private final Squad noneSquad;
    private final SelectorEntry noneEntry;
    private boolean scrolling;

    public SelectorWidget(MinecraftClient minecraftClient, int width, int height, int top, int bottom, int itemHeight) {
        super(minecraftClient, width, height, top, bottom, itemHeight);
        this.setRenderBackground(false);

        TranslatorFactory translatorFactory = DIContainer.get().getInstance(TranslatorFactory.class);
        Translator translator = translatorFactory.getTranslator(Overlord.MODID);

        this.emptyUUID = DIContainer.get().getInstance(EmptyUUID.class);
        this.noneSquad = new ClientSquad(
            emptyUUID.get(),
            emptyUUID.get(),
            "",
            ItemStack.EMPTY,
            translator.getTranslatedString("gui.overlord.squad_manager.none")
        );
        this.noneEntry = new SelectorEntry(noneSquad);
        this.addEntry(noneEntry);
        this.setSelected(noneEntry);
    }

    public void addSquads(Collection<? extends Squad> squads) {
        for (Squad squad : squads) {
            this.addEntry(new SelectorEntry(squad));
        }
    }

    public void removeSquad(Squad squad) {
        this.children().stream()
            .filter(selectorEntry -> selectorEntry.squad.getSquadId().equals(squad.getSquadId()))
            .forEach(selectorEntry -> this.children().remove(selectorEntry));
    }

    public void selectSquad(UUID squadId) {
        Optional<SelectorEntry> firstMatchingSquad = this.children().stream().filter(entry -> entry.hasId(squadId)).findFirst();
        this.setSelected(firstMatchingSquad.orElse(noneEntry));
    }


    @Override
    protected void renderList(MatrixStack matrices, int x, int y, int mouseX, int mouseY, float delta) {
        int itemCount = this.getEntryCount();

        for (int index = 0; index < itemCount; ++index) {
            int entryTop = this.getRowTop(index) + 2;
            int entryBottom = this.getRowTop(index) + this.itemHeight;
            if (entryBottom >= this.top && entryBottom <= this.bottom + 20) {
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
            && mouseX >= (double) this.getScrollbarPositionX()
            && mouseX < (double) (this.getScrollbarPositionX() + 6);
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
                    return true;
                }
            } else if (button == 0) {
                this.clickedHeader((int) (mouseX - (double) (this.left + this.width / 2 - this.getRowWidth() / 2)), (int) (mouseY - (double) this.top) + (int) this.getScrollAmount() - 4);
                return true;
            }

            return this.scrolling;
        }
    }

    public final SelectorEntry getEntryAtPos(double x, double y) {
        int int_5 = MathHelper.floor(y - (double) this.top) - this.headerHeight + (int) this.getScrollAmount() - 4;
        int index = int_5 / this.itemHeight;
        return x < (double) this.getScrollbarPositionX()
            && x >= (double) getRowLeft()
            && x <= (double) (getRowLeft() + getRowWidth())
            && index >= 0
            && int_5 >= 0
            && index < this.getEntryCount()
            ? this.children().get(index)
            : null;
    }

    @Override
    protected int getScrollbarPositionX() {
        return this.width - 6;
    }

    @Override
    public int getRowWidth() {
        return this.width - (Math.max(0, this.getMaxPosition() - (this.bottom - this.top - 4)) > 0 ? 18 : 12);
    }

    @Override
    public int getRowLeft() {
        return left + 6;
    }

    public int getWidth() {
        return width;
    }

    public int getTop() {
        return this.top;
    }

    @Override
    protected int getMaxPosition() {
        return super.getMaxPosition() + 4;
    }
}
