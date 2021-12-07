package dev.the_fireplace.overlord.client.impl.data;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.domain.data.SquadPatterns;
import dev.the_fireplace.overlord.domain.data.Squads;
import dev.the_fireplace.overlord.impl.data.AbstractSquadPatterns;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import javax.inject.Inject;
import javax.inject.Named;

@Environment(EnvType.CLIENT)
@Implementation(name = "client")
public final class ClientSquadPatterns extends AbstractSquadPatterns implements SquadPatterns
{
    @Inject
    public ClientSquadPatterns(@Named("client") Squads squads) {
        super(squads);
    }
}
