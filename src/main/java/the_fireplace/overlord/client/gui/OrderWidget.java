package the_fireplace.overlord.client.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.advancement.AdvancementObtainedStatus;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import the_fireplace.overlord.OverlordHelper;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Environment(EnvType.CLIENT)
public class OrderWidget extends DrawableHelper {
    private static final Identifier WIDGETS_TEX = new Identifier("textures/gui/advancements/widgets.png");
    private static final Pattern BACKSLASH_S_PATTERN = Pattern.compile("(.+) \\S+");
    private final String title;
    private final int width;
    private final List<String> description;
    private final MinecraftClient client;
    @Nullable
    private OrderWidget parent;
    private final List<OrderWidget> children = Lists.newArrayList();
    private final int xPos;
    private final int yPos;
    private final ItemStack icon;
    private final OrderWidgetFrame frame;
    private final SkeletonOrdersGui screen;

    public OrderWidget(MinecraftClient client, ItemStack icon, Text name, Text description, int gridPosX, int gridPosY, int requirementCount, OrderWidgetFrame frame, SkeletonOrdersGui screen) {
        this.client = client;
        this.title = client.textRenderer.trimToWidth(name.asFormattedString(), 163);
        this.xPos = MathHelper.floor(gridPosX * 28.0F);
        this.yPos = MathHelper.floor(gridPosY * 27.0F);
        int requirementCountDigits = String.valueOf(requirementCount).length();
        int k = requirementCount > 1 ? client.textRenderer.getStringWidth("  ") + client.textRenderer.getStringWidth("0") * requirementCountDigits * 2 + client.textRenderer.getStringWidth("/") : 0;
        int l = 29 + client.textRenderer.getStringWidth(this.title) + k;
        String formattedDescriptionString = description.asFormattedString();
        this.description = this.wrapDescription(formattedDescriptionString, l);

        String string2;
        for(Iterator<String> descriptionIterator = this.description.iterator(); descriptionIterator.hasNext(); l = Math.max(l, client.textRenderer.getStringWidth(string2)))
            string2 = descriptionIterator.next();

        this.width = l + 3 + 5;
        this.icon = icon;
        this.frame = frame;
        this.screen = screen;
    }

    private List<String> wrapDescription(String description, int width) {
        if (description.isEmpty())
            return Collections.emptyList();
        else {
            List<String> list = this.client.textRenderer.wrapStringToWidthAsList(description, width);
            if (list.size() < 2)
                return list;
            else {
                String string = list.get(0);
                String string2 = list.get(1);
                int i = this.client.textRenderer.getStringWidth(string + ' ' + string2.split(" ")[0]);
                if (i - width <= 10) {
                    return this.client.textRenderer.wrapStringToWidthAsList(description, i);
                } else {
                    Matcher matcher = BACKSLASH_S_PATTERN.matcher(string);
                    if (matcher.matches()) {
                        int j = this.client.textRenderer.getStringWidth(matcher.group(1));
                        if (width - j <= 10) {
                            return this.client.textRenderer.wrapStringToWidthAsList(description, j);
                        }
                    }

                    return list;
                }
            }
        }
    }

    @Nullable
    private OrderWidget getParent() {
        return parent;
    }

    public void renderLines(int x, int y, boolean firstPass) {
        if (this.parent != null) {
            int i = x + this.parent.xPos + 13;
            int j = x + this.parent.xPos + 26 + 4;
            int k = y + this.parent.yPos + 13;
            int l = x + this.xPos + 13;
            int m = y + this.yPos + 13;
            int n = firstPass ? -16777216 : -1;
            if (firstPass) {
                this.hLine(j, i, k - 1, n);
                this.hLine(j + 1, i, k, n);
                this.hLine(j, i, k + 1, n);
                this.hLine(l, j - 1, m - 1, n);
                this.hLine(l, j - 1, m, n);
                this.hLine(l, j - 1, m + 1, n);
                this.vLine(j - 1, m, k, n);
                this.vLine(j + 1, m, k, n);
            } else {
                this.hLine(j, i, k, n);
                this.hLine(l, j, m, n);
                this.vLine(j, m, k, n);
            }
        }

        for (OrderWidget child : this.children)
            child.renderLines(x, y, firstPass);
    }

    public void renderWidgets(int x, int y) {
        //TODO retrieve the correct enabled/disabled status from the skeleton's AI
        AdvancementObtainedStatus advancementObtainedStatus2 = AdvancementObtainedStatus.UNOBTAINED;

        this.client.getTextureManager().bindTexture(WIDGETS_TEX);
        this.blit(x + this.xPos + 3, y + this.yPos, this.getFrame().texV(), 128 + advancementObtainedStatus2.getSpriteIndex() * 26, 26, 26);
        this.client.getItemRenderer().renderGuiItem(null, this.getIcon(), x + this.xPos + 8, y + this.yPos + 5);

        for (OrderWidget advancementWidget : this.children) {
            advancementWidget.renderWidgets(x, y);
        }
    }

    public void addChild(OrderWidget widget) {
        this.children.add(widget);
    }

    public void drawTooltip(int originX, int originY, float alpha, int x, int y) {
        boolean bl = x + originX + this.xPos + this.width + 26 >= this.getScreen().width;
        int var10000 = 113 - originY - this.yPos - 26;
        int var10002 = this.description.size();
        this.client.textRenderer.getClass();
        boolean bl2 = var10000 <= 6 + var10002 * 9;
        float f = 0;
        int j = MathHelper.floor(f * (float)this.width);
        AdvancementObtainedStatus advancementObtainedStatus10;
        AdvancementObtainedStatus advancementObtainedStatus11;
        AdvancementObtainedStatus advancementObtainedStatus12;
        if (j < 2) {
            j = this.width / 2;
            advancementObtainedStatus10 = AdvancementObtainedStatus.UNOBTAINED;
            advancementObtainedStatus11 = AdvancementObtainedStatus.UNOBTAINED;
            advancementObtainedStatus12 = AdvancementObtainedStatus.UNOBTAINED;
        } else if (j > this.width - 2) {
            j = this.width / 2;
            advancementObtainedStatus10 = AdvancementObtainedStatus.OBTAINED;
            advancementObtainedStatus11 = AdvancementObtainedStatus.OBTAINED;
            advancementObtainedStatus12 = AdvancementObtainedStatus.UNOBTAINED;
        } else {
            advancementObtainedStatus10 = AdvancementObtainedStatus.OBTAINED;
            advancementObtainedStatus11 = AdvancementObtainedStatus.UNOBTAINED;
            advancementObtainedStatus12 = AdvancementObtainedStatus.UNOBTAINED;
        }

        int k = this.width - j;
        this.client.getTextureManager().bindTexture(WIDGETS_TEX);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        int l = originY + this.yPos;
        int n;
        if (bl) {
            n = originX + this.xPos - this.width + 26 + 6;
        } else {
            n = originX + this.xPos;
        }

        int var10001 = this.description.size();
        this.client.textRenderer.getClass();
        int o = 32 + var10001 * 9;
        if (!this.description.isEmpty()) {
            if (bl2) {
                this.method_2324(n, l + 26 - o, this.width, o, 10, 200, 26, 0, 52);
            } else {
                this.method_2324(n, l, this.width, o, 10, 200, 26, 0, 52);
            }
        }

        this.blit(n, l, 0, advancementObtainedStatus10.getSpriteIndex() * 26, j, 26);
        this.blit(n + j, l, 200 - k, advancementObtainedStatus11.getSpriteIndex() * 26, k, 26);
        this.blit(originX + this.xPos + 3, originY + this.yPos, this.getFrame().texV(), 128 + advancementObtainedStatus12.getSpriteIndex() * 26, 26, 26);
        if (bl) {
            this.client.textRenderer.drawWithShadow(this.title, (float)(n + 5), (float)(originY + this.yPos + 9), -1);
        } else {
            this.client.textRenderer.drawWithShadow(this.title, (float)(originX + this.xPos + 32), (float)(originY + this.yPos + 9), -1);
        }

        int p;
        int var10003;
        TextRenderer textRenderer;
        String drawDesc;
        float var22;
        if (bl2) {
            for(p = 0; p < this.description.size(); ++p) {
                textRenderer = this.client.textRenderer;
                drawDesc = this.description.get(p);
                var22 = (float)(n + 5);
                var10003 = l + 26 - o + 7;
                this.client.textRenderer.getClass();
                textRenderer.draw(drawDesc, var22, (float)(var10003 + p * 9), -5592406);
            }
        } else {
            for(p = 0; p < this.description.size(); ++p) {
                textRenderer = this.client.textRenderer;
                drawDesc = this.description.get(p);
                var22 = (float)(n + 5);
                var10003 = originY + this.yPos + 9 + 17;
                this.client.textRenderer.getClass();
                textRenderer.draw(drawDesc, var22, (float)(var10003 + p * 9), -5592406);
            }
        }

        this.client.getItemRenderer().renderGuiItem((LivingEntity)null, this.getIcon(), originX + this.xPos + 8, originY + this.yPos + 5);
    }

    protected void method_2324(int i, int j, int k, int l, int m, int n, int o, int p, int q) {
        this.blit(i, j, p, q, m, m);
        this.method_2321(i + m, j, k - m - m, m, p + m, q, n - m - m, o);
        this.blit(i + k - m, j, p + n - m, q, m, m);
        this.blit(i, j + l - m, p, q + o - m, m, m);
        this.method_2321(i + m, j + l - m, k - m - m, m, p + m, q + o - m, n - m - m, o);
        this.blit(i + k - m, j + l - m, p + n - m, q + o - m, m, m);
        this.method_2321(i, j + m, m, l - m - m, p, q + m, n, o - m - m);
        this.method_2321(i + m, j + m, k - m - m, l - m - m, p + m, q + m, n - m - m, o - m - m);
        this.method_2321(i + k - m, j + m, m, l - m - m, p + n - m, q + m, n, o - m - m);
    }

    protected void method_2321(int i, int j, int k, int l, int m, int n, int o, int p) {
        for(int q = 0; q < k; q += o) {
            int r = i + q;
            int s = Math.min(o, k - q);

            for(int t = 0; t < l; t += p) {
                int u = j + t;
                int v = Math.min(p, l - t);
                this.blit(r, u, m, n, s, v);
            }
        }
    }

    public boolean shouldRender(int originX, int originY, int mouseX, int mouseY) {
        int i = originX + this.xPos;
        int j = i + 26;
        int k = originY + this.yPos;
        int l = k + 26;
        return mouseX >= i && mouseX <= j && mouseY >= k && mouseY <= l;
    }

    public void addToTree(@Nullable OrderWidget parent) {
        if (this.parent == null) {
            this.parent = parent;
            if (this.parent != null)
                this.parent.addChild(this);
        } else if(parent != null) {
            OverlordHelper.LOGGER.warn("Attempted to add second parent to Order Widget {}: {}.", this.title, parent.title);
        }
    }

    public int getY() {
        return this.yPos;
    }

    public int getX() {
        return this.xPos;
    }

    @Environment(EnvType.CLIENT)
    public ItemStack getIcon() {
        return this.icon;
    }

    public OrderWidgetFrame getFrame() {
        return this.frame;
    }

    public SkeletonOrdersGui getScreen() {
        return screen;
    }
}
