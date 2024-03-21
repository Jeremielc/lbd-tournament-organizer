package com.jeremielc.lbd.pojo;

import java.util.List;

import com.jeremielc.lbd.pojo.match.AbstractMatch;

public class TournamentConfig implements Comparable<TournamentConfig> {
    private final int score;
    private final String[][] versusTable;
    private final List<AbstractMatch> matchList;

    public TournamentConfig(int score, String[][] versusTable, List<AbstractMatch> matchList) {
        this.score = score;
        this.versusTable = versusTable;
        this.matchList = matchList;
    }

    @Override
    public int compareTo(TournamentConfig o) {
        if (score == o.getScore()) {
            return 0;
        } else if (score > o.getScore()) {
            return 1;
        } else {
            return -1;
        }
    }

    public int getScore() {
        return score;
    }

    public String[][] getVersusTable() {
        return versusTable;
    }

    public List<AbstractMatch> getMatchList() {
        return matchList;
    }
}
