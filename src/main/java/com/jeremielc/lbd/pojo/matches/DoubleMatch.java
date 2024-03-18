package com.jeremielc.lbd.pojo.matches;

import com.jeremielc.lbd.exceptions.IllegalTeamException;
import com.jeremielc.lbd.pojo.teams.DoublePlayerTeam;

public class DoubleMatch extends AbstractMatch {
    public DoubleMatch(DoublePlayerTeam aTeam, DoublePlayerTeam bTeam) throws IllegalTeamException {
        super(aTeam, bTeam);
    }
}
