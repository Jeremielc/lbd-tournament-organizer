package com.jeremielc.lbd.pojo.match;

import com.jeremielc.lbd.exceptions.IllegalTeamException;
import com.jeremielc.lbd.pojo.teams.AbstractTeam;

public abstract class AbstractMatch {
    public static final String SEPARATOR = " vs ";

    private final AbstractTeam teamA;
    private final AbstractTeam teamB;

    protected AbstractMatch(AbstractTeam aTeam, AbstractTeam bTeam) throws IllegalTeamException {
        if (!aTeam.getClass().equals(bTeam.getClass())) {
            throw new IllegalTeamException("In a match, the two teams must be of the same type.");
        }

        this.teamA = aTeam;
        this.teamB = bTeam;
    }

    @Override
    public String toString() {
        return teamA + SEPARATOR + teamB;
    }

    public AbstractTeam getTeamA() {
        return this.teamA;
    }

    public AbstractTeam getTeamB() {
        return this.teamB;
    }
}
