package dev.the_fireplace.overlord.api.client;

import dev.the_fireplace.overlord.api.entity.OrderableEntity;
import dev.the_fireplace.overlord.client.gui.GuiOpenerImpl;

public interface GuiOpener {
    static GuiOpener getInstance() {
        //noinspection deprecation
        return GuiOpenerImpl.INSTANCE;
    }
    void openOrdersGUI(OrderableEntity entity);
}
