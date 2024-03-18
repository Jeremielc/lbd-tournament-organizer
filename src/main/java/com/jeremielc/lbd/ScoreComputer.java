package com.jeremielc.lbd;

import java.util.ArrayList;
import java.util.List;

import com.jeremielc.lbd.exceptions.IllegalTeamException;
import com.jeremielc.lbd.exceptions.ParsingException;
import com.jeremielc.lbd.pojo.matches.AbstractMatch;
import com.jeremielc.lbd.pojo.matches.SingleMatch;
import com.jeremielc.lbd.pojo.teams.DoublePlayerTeam;
import com.jeremielc.lbd.pojo.teams.SinglePlayerTeam;

public class ScoreComputer {
    public static int computeMatchmakingScore(String[][] versusTable, List<String> firstPlayers, List<String> secondPlayers) {
        int score = 0;

        try {
            score += computeMergedScore(versusTable, firstPlayers, secondPlayers);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace(System.err);
        }
        

        return score;
    }

    private static int computeMergedScore(String[][] versusTable, List<String> firstPlayers, List<String> secondPlayers) throws ParsingException {
        int score = 0;
        String sep = DoublePlayerTeam.SEPARATOR;

        List<AbstractMatch> singleFirstPlayerMatches = new ArrayList<>();
        List<AbstractMatch> singleSecondPlayerMatches = new ArrayList<>();

        for (int y = 0; y < versusTable.length; y++) {
            for (int x = 0; x < versusTable[y].length; x++) {
                String teamStr = versusTable[y][x];

                if (!teamStr.contains(sep) || teamStr.indexOf(sep) != teamStr.lastIndexOf(sep)) {
                    throw new ParsingException("Cannot parse a " + DoublePlayerTeam.class.getSimpleName() + " object when the separator is not " + sep);
                }

                String[] players = teamStr.split(DoublePlayerTeam.SEPARATOR);

                try {
                    singleFirstPlayerMatches.add(new SingleMatch(new SinglePlayerTeam(firstPlayers.get(x)), new SinglePlayerTeam(players[0])));
                    singleSecondPlayerMatches.add(new SingleMatch(new SinglePlayerTeam(secondPlayers.get(y)), new SinglePlayerTeam(players[1])));
                } catch (IllegalTeamException ex) {
                    System.err.println(ex.getMessage());
                    ex.printStackTrace(System.err);
                }
                
            }
        }

        String[][] firstSubTable = TableMaker.generateVersusTable(firstPlayers, null, singleFirstPlayerMatches);
        //TableMaker.displayVersusTable(firstPlayers, firstPlayers, firstSubTable, false);

        String[][] secondSubTable = TableMaker.generateVersusTable(secondPlayers, null, singleSecondPlayerMatches);
        //TableMaker.displayVersusTable(secondPlayers, secondPlayers, secondSubTable, false);

        for (String[][] subTable : List.of(firstSubTable, secondSubTable)) {
            for (int y = 0; y < subTable.length; y++) {
                for (int x = 0; x < subTable[y].length; x++) {
                    if (y != x) {
                        int val = Integer.parseInt(subTable[y][x].trim());

                        if (val != 1) {
                            score += Math.abs(1 - val);
                        }
                    }
                }
            }
        }

        return score;
    }
}
