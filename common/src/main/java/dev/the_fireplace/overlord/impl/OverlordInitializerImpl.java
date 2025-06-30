package dev.the_fireplace.overlord.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.lib.api.lifecycle.injectables.ServerLifecycle;
import dev.the_fireplace.overlord.OverlordInitializer;
import dev.the_fireplace.overlord.advancement.OverlordCriterions;
import dev.the_fireplace.overlord.command.RegisterCommands;
import dev.the_fireplace.overlord.network.NetworkRegistry;

import java.util.concurrent.atomic.AtomicBoolean;

@Singleton
@Implementation
public final class OverlordInitializerImpl implements OverlordInitializer
{
    private final AtomicBoolean initialized;
    private final ServerLifecycle serverLifecycle;
    private final RegisterCommands registerCommands;
    private final NetworkRegistry networkRegistry;

    @Inject
    public OverlordInitializerImpl(
        ServerLifecycle serverLifecycle,
        RegisterCommands registerCommands,
        NetworkRegistry networkRegistry
    ) {
        this.initialized = new AtomicBoolean(false);
        this.serverLifecycle = serverLifecycle;
        this.registerCommands = registerCommands;
        this.networkRegistry = networkRegistry;
    }

    @Override
    public void initialize() {
        if (initialized.get()) {
            return;
        }
        initialized.set(true);
        networkRegistry.register();
        OverlordCriterions.register();
        serverLifecycle.registerServerStartingCallback(
            server -> registerCommands.register(server.getCommands().getDispatcher())
        );
    }
}
