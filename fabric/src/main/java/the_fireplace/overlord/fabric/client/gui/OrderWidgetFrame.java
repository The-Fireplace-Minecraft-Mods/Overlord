package the_fireplace.overlord.fabric.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Formatting;

public enum OrderWidgetFrame {
    TASK("task", 0, Formatting.GREEN),
    CHALLENGE("challenge", 26, Formatting.DARK_PURPLE),
    GOAL("goal", 52, Formatting.GREEN);

    private final String id;
    private final int texV;
    private final Formatting titleFormat;

    OrderWidgetFrame(String id, int texV, Formatting titleFormat) {
        this.id = id;
        this.texV = texV;
        this.titleFormat = titleFormat;
    }

    public String getId() {
        return this.id;
    }

    @Environment(EnvType.CLIENT)
    public int texV() {
        return this.texV;
    }

    public static OrderWidgetFrame forName(String name) {
        OrderWidgetFrame[] var1 = values();
        int var2 = var1.length;

        for (OrderWidgetFrame orderWidgetFrame : var1)
            if (orderWidgetFrame.id.equals(name))
                return orderWidgetFrame;

        throw new IllegalArgumentException("Unknown frame type '" + name + "'");
    }

    public Formatting getTitleFormat() {
        return this.titleFormat;
    }
}
