package com.jeremielc.lbd.pojo;

import java.util.ArrayList;
import java.util.List;

import com.jeremielc.lbd.pojo.match.AbstractMatch;

public class MatchSet implements Comparable<MatchSet> {
    private final List<AbstractMatch> matchList;

    public MatchSet(AbstractMatch... args) {
        this.matchList = new ArrayList<>();

        for (AbstractMatch match : args) {
            this.matchList.add(match);
        }
    }

    public void addMatch(AbstractMatch match) {
        matchList.add(match);
    }

    @Override
    public int compareTo(MatchSet o) {
        if (this.matchList.size() == o.getMatchList().size()) {
            return 0;
        } else if (this.matchList.size() > o.getMatchList().size()) {
            return 1;
        } else {
            return -1;
        }
    }

    public List<AbstractMatch> getMatchList() {
        return this.matchList;
    }
}
