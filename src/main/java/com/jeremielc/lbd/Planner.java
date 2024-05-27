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
            List<MatchSet> rounds = planDoubleMatchList(courtCount, config.getMatchList());

            for (MatchSet round : rounds) {
                System.out.println(round.getMatchList());
            }
        } catch (IllegalTeamException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace(System.err);
        }

        return new ArrayList<>();
    }

    private static List<MatchSet> planSingleMatchList(int courtCount, List<AbstractMatch> matchList) throws IllegalTeamException {
        List<MatchSet> rounds = new ArrayList<>();
        MatchSet round = new MatchSet();

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
                if (round.getMatchList() != null) {
                    if (round.getMatchList().size() == courtCount || availableFirstPlayers.size() == 0 || availableSecondPlayers.size() == 0) {
                        break;
                    }
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
                int matchIndex = -1;

                for (int i = 0; i < matchList.size(); i++) {
                    if (matchList.get(i).getTeamA().toString().equalsIgnoreCase(match.getTeamA().toString())) {
                        if (matchList.get(i).getTeamB().toString().equalsIgnoreCase(match.getTeamB().toString())) {
                            matchIndex = i;
                            break;
                        }
                    }
                }

                if (matchIndex != -1) {
                    matchList.remove(matchIndex);
                } else {
                    System.err.println("ERROR : match " + match + " was not found in list.");
                }

                // Remove reciprocal match from matchList
                int reciprocalMatchIndex = -1;

                for (int i = 0; i < matchList.size(); i++) {
                    AbstractMatch item = matchList.get(i);

                    if (item.getTeamA().toString().equalsIgnoreCase(match.getTeamB().toString())) {
                        if (item.getTeamB().toString().equalsIgnoreCase(match.getTeamA().toString())) {
                            reciprocalMatchIndex = i;
                            break;
                        }
                    }
                }

                // Clear reciprocal match if exists
                if (reciprocalMatchIndex != -1) {
                    matchList.remove(reciprocalMatchIndex);
                } else {
                    System.err.println("ERROR : reciprocal match was not found in list.");
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
