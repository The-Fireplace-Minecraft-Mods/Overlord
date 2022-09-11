package dev.the_fireplace.overlord.domain.client;

import dev.the_fireplace.overlord.domain.entity.OrderableEntity;
import dev.the_fireplace.overlord.entity.ai.aiconfig.AISettings;
import net.minecraft.client.gui.screens.Screen;

public interface OrdersGuiFactory
{
    Screen build(Screen parent, OrderableEntity aiEntity);

    Screen build(Screen parent, AISettings ai);
}
