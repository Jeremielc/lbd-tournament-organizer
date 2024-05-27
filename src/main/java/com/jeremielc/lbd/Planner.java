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
            return planDoubleMatchList(courtCount, config.getMatchList());
        } catch (IllegalTeamException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace(System.err);
        }

        return new ArrayList<>(0);
    }

    private static List<MatchSet> planSingleMatchList(int courtCount, List<AbstractMatch> matchList) throws IllegalTeamException {
        List<MatchSet> rounds = new ArrayList<>();
        return rounds;
    }

    private static List<MatchSet> planDoubleMatchList(int courtCount, List<AbstractMatch> matchList) throws IllegalTeamException {
        List<MatchSet> rounds = new ArrayList<>();
        MatchSet round = new MatchSet();

        List<List<String>> playersLists = getPlayersFromDoublePlayerMatchList(matchList);

        List<String> availableFirstPlayers = new ArrayList<>(playersLists.get(0).size());
        List<String> availableSecondPlayers = new ArrayList<>(playersLists.get(1).size());
        availableFirstPlayers.addAll(playersLists.get(0));
        availableSecondPlayers.addAll(playersLists.get(1));

        while (matchList.size() > 0) {
            for (AbstractMatch match : matchList) {
                if (round.getMatchList() != null && round.getMatchList().size() == courtCount) {
                    break;
                }

                if (availableFirstPlayers.size() == 0 || availableSecondPlayers.size() == 0) {
                    break;
                }

                AbstractTeam aTeam = match.getTeamA();
                AbstractTeam bTeam = match.getTeamB();

                String aFirstPlayer = ((DoublePlayerTeam) aTeam).getFirstPlayer();
                String aSecondPlayer = ((DoublePlayerTeam) aTeam).getSecondPlayer();
                String bFirstPlayer = ((DoublePlayerTeam) bTeam).getFirstPlayer();
                String bSecondPlayer = ((DoublePlayerTeam) bTeam).getSecondPlayer();

                if (availableFirstPlayers.contains(aFirstPlayer) && availableFirstPlayers.contains(bFirstPlayer)) {
                    if (availableSecondPlayers.contains(aSecondPlayer) && availableSecondPlayers.contains(bSecondPlayer)) {
                        round.addMatch(match);

                        availableFirstPlayers.remove(aFirstPlayer);
                        availableFirstPlayers.remove(bFirstPlayer);
                        availableSecondPlayers.remove(aSecondPlayer);
                        availableSecondPlayers.remove(bSecondPlayer);
                    }
                }
            }

            // Clean-up
            for (AbstractMatch match : round.getMatchList()) {
                // Remove match from matchList
                matchList.remove(match);

                // Remove reciprocal match from matchList
                for (AbstractMatch item : matchList) {
                    if (item.getTeamA().toString().equals(match.getTeamB().toString())) {
                        if (item.getTeamB().toString().equals(match.getTeamA().toString())) {
                            matchList.remove(item);
                            break;
                        }
                    }
                }
            }

            // Reset for next occurence
            rounds.add(round);
            round = new MatchSet();

            if (matchList.size() > 0) {
                availableFirstPlayers.clear();
                availableSecondPlayers.clear();

                availableFirstPlayers.addAll(playersLists.get(0));
                availableSecondPlayers.addAll(playersLists.get(1));
            }
        }

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
