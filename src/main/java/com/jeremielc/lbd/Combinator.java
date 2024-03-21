package com.jeremielc.lbd;

import java.util.ArrayList;
import java.util.List;

import com.jeremielc.lbd.exceptions.InvalidPlayerListException;
import com.jeremielc.lbd.pojo.teams.DoublePlayerTeam;
import com.jeremielc.lbd.pojo.teams.SinglePlayerTeam;

public class Combinator {
    public static List<SinglePlayerTeam> generateSingleCombinations(List<String> players) throws InvalidPlayerListException {
        if (players == null) {
            throw new InvalidPlayerListException("Players list cannot be null.");
        }

        List<SinglePlayerTeam> combinations = new ArrayList<>();

        for (String player : players) {
            combinations.add(new SinglePlayerTeam(player));
        }

        return combinations;
    }

    public static List<DoublePlayerTeam> generateMixedPairCombinations(List<String> firstPlayers, List<String> secondPlayers) throws InvalidPlayerListException {
        if (firstPlayers == null) {
            throw new InvalidPlayerListException("First players list cannot be null.");
        }

        if (secondPlayers == null) {
            throw new InvalidPlayerListException("Second players list cannot be null.");
        }

        List<DoublePlayerTeam> combinations = new ArrayList<>();

        for (String firstPlayer : firstPlayers) {
            for (String secondPlayer : secondPlayers) {
                combinations.add(new DoublePlayerTeam(firstPlayer, secondPlayer));
            }
        }

        return combinations;
    }
}
