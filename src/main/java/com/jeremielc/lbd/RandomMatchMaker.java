package com.jeremielc.lbd;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.jeremielc.lbd.exceptions.IllegalTeamException;
import com.jeremielc.lbd.exceptions.InvalidCombinationsSizeException;
import com.jeremielc.lbd.pojo.matches.DoubleMatch;
import com.jeremielc.lbd.pojo.matches.SingleMatch;
import com.jeremielc.lbd.pojo.teams.AbstractTeam;
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

    public static List<AbstractMatch> generateRandomDoubleMatches(List<AbstractTeam> combinations, boolean withReciprocal) {
        List<AbstractMatch> matches = new ArrayList<>();
        Random random = new Random();
        DoublePlayerTeam aTeam, bTeam;

        while (!combinations.isEmpty()) {
            if (combinations.size() > 2) {
                aTeam = (DoublePlayerTeam) combinations.get(random.nextInt(combinations.size()));
                bTeam = (DoublePlayerTeam) combinations.get(random.nextInt(combinations.size()));

                while (aTeam.getFirstPlayer().equals(bTeam.getFirstPlayer()) || aTeam.getSecondPlayer().equals(bTeam.getSecondPlayer())) {
                    aTeam = (DoublePlayerTeam) combinations.get(random.nextInt(combinations.size()));
                    bTeam = (DoublePlayerTeam) combinations.get(random.nextInt(combinations.size()));
                }
            } else {
                aTeam = (DoublePlayerTeam) combinations.get(0);
                bTeam = (DoublePlayerTeam) combinations.get(1);
            }

            try {
                matches.add(new DoubleMatch(aTeam, bTeam));

                if (withReciprocal) {
                    matches.add(new DoubleMatch(bTeam, aTeam));
                }
            } catch (IllegalTeamException ex) {
                System.err.println(ex.getMessage());
                ex.printStackTrace(System.err);
            }

            combinations.remove(aTeam);
            combinations.remove(bTeam);
        }

        return matches;
    }

    public static List<AbstractMatch> generateSingleMatchList(List<SinglePlayerTeam> combinations) {
        return generateSingleMatchList(combinations, true);
    }

    public static List<AbstractMatch> generateSingleMatchList(List<SinglePlayerTeam> combinations, boolean withReciprocal) {
        List<AbstractMatch> matches = new ArrayList<>();
        SinglePlayerTeam aTeam, bTeam;

        for (int i = 0; i < combinations.size(); i++) {
            for (int j = i; j < combinations.size(); j++) {
                aTeam = (SinglePlayerTeam) combinations.get(i);
                bTeam = (SinglePlayerTeam) combinations.get(j);

                if (!aTeam.getPlayer().equals(bTeam.getPlayer())) {
                    try {
                        matches.add(new SingleMatch(aTeam, bTeam));

                        if (withReciprocal) {
                            matches.add(new SingleMatch(bTeam, aTeam));
                        }
                    } catch (IllegalTeamException ex) {
                        System.err.println(ex.getMessage());
                        ex.printStackTrace(System.err);
                    }
                }
            }
        }

        return matches;
    }
}
