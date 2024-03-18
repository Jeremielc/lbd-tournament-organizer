package com.jeremielc.lbd;

public class Candidate implements Comparable {
    private final int score;
    private final String[][] versusTable;

    public Candidate(int score, String[][] versusTable) {
        this.score = score;
        this.versusTable = versusTable;
    }

    @Override
    public int compareTo(Object o) {
        if (this.score == ((Candidate) o).getScore()) {
            return 0;
        } else if (this.score > ((Candidate) o).getScore()) {
            return 1;
        } else {
            return -1;
        }
    }

    public int getScore() {
        return this.score;
    }

    public String[][] getVersusTable() {
        return this.versusTable;
    }
}
