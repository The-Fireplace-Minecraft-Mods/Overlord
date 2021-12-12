package dev.the_fireplace.overlord.entrypoints;

import dev.the_fireplace.annotateddi.api.DIContainer;
import dev.the_fireplace.overlord.config.OverlordConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;

import java.util.function.Function;

@Environment(EnvType.CLIENT)
public final class ModMenu implements ModMenuApi
{
    private final OverlordConfigScreenFactory flConfigScreenFactory = DIContainer.get().getInstance(OverlordConfigScreenFactory.class);

    @Override
    public Function<Screen, ? extends Screen> getConfigScreenFactory() {
        return flConfigScreenFactory::getConfigScreen;
    }
}
