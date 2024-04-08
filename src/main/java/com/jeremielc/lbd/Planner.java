package com.jeremielc.lbd;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.jeremielc.lbd.exceptions.IllegalTeamException;
import com.jeremielc.lbd.exceptions.InvalidPlayerListException;
import com.jeremielc.lbd.pojo.MatchSet;
import com.jeremielc.lbd.pojo.TournamentConfig;
import com.jeremielc.lbd.pojo.match.AbstractMatch;
import com.jeremielc.lbd.pojo.teams.AbstractTeam;
import com.jeremielc.lbd.pojo.teams.DoublePlayerTeam;

public class Planner {
    public static List<MatchSet> plan(int courtCount, TournamentConfig config) throws InvalidPlayerListException {
        try {
            planDoubleMatchList(courtCount, config.getMatchList());
        } catch (IllegalTeamException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return new ArrayList<>();

        /*switch (playerLists.length) {
            case 1:
            try {
                return plan(courtCount, config.getVersusTable(), playerLists[0]);
            } catch (IllegalTeamException ex) {
                System.err.println(ex.getMessage());
                ex.printStackTrace(System.err);
            }
            case 2:
                try {
                    return plan(courtCount, config.getVersusTable(), playerLists[0], playerLists[1]);
                } catch (IllegalTeamException ex) {
                    System.err.println(ex.getMessage());
                    ex.printStackTrace(System.err);
                }
            default:
                throw new InvalidPlayerListException("PlayerList must have a size comprised between 1 and 2 included.");
        }*/
    }

    private static List<MatchSet> planSingleMatchList(int courtCount, List<AbstractMatch> matchList) throws IllegalTeamException {
        List<MatchSet> rounds = new ArrayList<>();
        MatchSet round = new MatchSet();

        return rounds;
    }

    private static List<MatchSet> planDoubleMatchList(int courtCount, List<AbstractMatch> matchList) throws IllegalTeamException {
        List<MatchSet> rounds = new ArrayList<>();
        MatchSet round = new MatchSet();

        List<List<String>> playersLists =  getPlayersFromDoublePlayerMatchList(matchList);

        List<String> availableFirstPlayers = new ArrayList<>(playersLists.get(0).size());
        List<String> availableSecondPlayers = new ArrayList<>(playersLists.get(1).size());
        availableFirstPlayers.addAll(playersLists.get(0));
        availableSecondPlayers.addAll(playersLists.get(1));

        System.out.println("First players: " + playersLists.get(0));
        System.out.println("Second players: " + playersLists.get(1));

        return rounds;
    }

    private static List<String> getPlayersFromSinglePlayerMatchList(List<AbstractMatch> matchList) {
        Set<String> players = new TreeSet<>();

        for (AbstractMatch match : matchList) {
            players.add(match.getTeamA().getPlayer());
            players.add(match.getTeamB().getPlayer());
        }

        List<String> result = new ArrayList<>(players.size());
        result.addAll(players);

        return result;
    }

    private static List<List<String>> getPlayersFromDoublePlayerMatchList(List<AbstractMatch> matchList) {
        Set<String> firstPlayers = new TreeSet<>();
        Set<String> secondPlayers = new TreeSet<>();
        AbstractTeam aTeam, bTeam;

        for (AbstractMatch match : matchList) {
            aTeam = match.getTeamA();
            bTeam = match.getTeamB();

            firstPlayers.add(((DoublePlayerTeam) aTeam).getFirstPlayer());
            firstPlayers.add(((DoublePlayerTeam) bTeam).getFirstPlayer());

            secondPlayers.add(((DoublePlayerTeam) aTeam).getSecondPlayer());
            secondPlayers.add(((DoublePlayerTeam) bTeam).getSecondPlayer());
        }

        List<String> firstResult = new ArrayList<>(firstPlayers.size());
        firstResult.addAll(firstPlayers);

        List<String> secondResult = new ArrayList<>(secondPlayers.size());
        secondResult.addAll(secondPlayers);

        List<List<String>> result = new ArrayList<>(2);
        result.add(firstResult);
        result.add(secondResult);

        return result;
    }
}
