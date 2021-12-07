package dev.the_fireplace.overlord.impl.data;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.domain.data.SquadPatterns;
import dev.the_fireplace.overlord.domain.data.Squads;

import javax.inject.Inject;

@Implementation
public final class SquadPatternsImpl extends AbstractSquadPatterns implements SquadPatterns
{
    @Inject
    public SquadPatternsImpl(Squads squads) {
        super(squads);
    }
}
