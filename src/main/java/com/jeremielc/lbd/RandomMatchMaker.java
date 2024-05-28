package com.jeremielc.lbd;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.jeremielc.lbd.exceptions.IllegalTeamException;
import com.jeremielc.lbd.exceptions.InvalidCombinationsSizeException;
import com.jeremielc.lbd.pojo.match.AbstractMatch;
import com.jeremielc.lbd.pojo.match.DoubleMatch;
import com.jeremielc.lbd.pojo.match.SingleMatch;
import com.jeremielc.lbd.pojo.teams.DoublePlayerTeam;
import com.jeremielc.lbd.pojo.teams.SinglePlayerTeam;

public class RandomMatchMaker {
    public static List<AbstractMatch> generateRandomDoubleMatchList(List<DoublePlayerTeam> combinations) throws InvalidCombinationsSizeException {
        return generateRandomDoubleMatchList(combinations, true);
    }

    public static List<AbstractMatch> generateRandomDoubleMatchList(List<DoublePlayerTeam> combinations, boolean withReciprocal) throws InvalidCombinationsSizeException {
        if (combinations.size() % 2 != 0) {
            throw new InvalidCombinationsSizeException("The combinations list should contain an even number of player combinations.");
        }

        List<AbstractMatch> matchList = new ArrayList<>();
        Random random = new Random();
        DoublePlayerTeam aTeam, bTeam;

        while (!combinations.isEmpty()) {
            if (combinations.size() > 2) {
                aTeam = combinations.get(random.nextInt(combinations.size()));
                bTeam = combinations.get(random.nextInt(combinations.size()));

                while (aTeam.getFirstPlayer().equals(bTeam.getFirstPlayer()) || aTeam.getSecondPlayer().equals(bTeam.getSecondPlayer())) {
                    aTeam = combinations.get(random.nextInt(combinations.size()));
                    bTeam = combinations.get(random.nextInt(combinations.size()));
                }
            } else {
                aTeam = combinations.get(0);
                bTeam = combinations.get(1);

                if (aTeam.getFirstPlayer().equals(bTeam.getFirstPlayer()) || aTeam.getSecondPlayer().equals(bTeam.getSecondPlayer())) {
                    return null;
                }
            }

            try {
                matchList.add(new DoubleMatch(aTeam, bTeam));

                if (withReciprocal) {
                    matchList.add(new DoubleMatch(bTeam, aTeam));
                }
            } catch (IllegalTeamException ex) {
                System.err.println(ex.getMessage());
                ex.printStackTrace(System.err);
            }

            combinations.remove(aTeam);
            combinations.remove(bTeam);
        }

        return matchList;
    }

    public static List<AbstractMatch> generateSingleMatchList(List<SinglePlayerTeam> combinations) {
        return generateSingleMatchList(combinations, true);
    }

    public static List<AbstractMatch> generateSingleMatchList(List<SinglePlayerTeam> combinations, boolean withReciprocal) {
        List<AbstractMatch> matchList = new ArrayList<>();
        SinglePlayerTeam aTeam, bTeam;

        for (int i = 0; i < combinations.size(); i++) {
            for (int j = i; j < combinations.size(); j++) {
                aTeam = combinations.get(i);
                bTeam = combinations.get(j);

                if (!aTeam.getPlayer().equals(bTeam.getPlayer())) {
                    try {
                        matchList.add(new SingleMatch(aTeam, bTeam));

                        if (withReciprocal) {
                            matchList.add(new SingleMatch(bTeam, aTeam));
                        }
                    } catch (IllegalTeamException ex) {
                        System.err.println(ex.getMessage());
                        ex.printStackTrace(System.err);
                    }
                }
            }
        }

        return matchList;
    }
}
