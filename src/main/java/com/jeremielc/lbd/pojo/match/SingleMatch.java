package com.jeremielc.lbd.pojo.match;

import com.jeremielc.lbd.exceptions.IllegalTeamException;
import com.jeremielc.lbd.pojo.teams.SinglePlayerTeam;

public class SingleMatch extends AbstractMatch {
    public SingleMatch(SinglePlayerTeam aTeam, SinglePlayerTeam bTeam) throws IllegalTeamException {
        super(aTeam, bTeam);
    }
}
