package com.jeremielc.lbd;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
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
import com.jeremielc.lbd.pojo.teams.SinglePlayerTeam;

public class Planner {
    public static List<MatchSet> plan(int courtCount, TournamentConfig config) throws InvalidPlayerListException {
        if (config.getMatchList() == null || config.getMatchList().size() <= 0) {
            return new ArrayList<>(0);
        }

        try {
            if (config.getMatchList().get(0).getTeamA() instanceof SinglePlayerTeam) {
                return planSingleMatchList(courtCount, config.getMatchList());
            } else if (config.getMatchList().get(0).getTeamA() instanceof DoublePlayerTeam) {
                return planDoubleMatchList(courtCount, config.getMatchList());
            }
        } catch (IllegalTeamException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace(System.err);
        }

        return new ArrayList<>(0);
    }

    public static void displayPlanning(List<MatchSet> rounds, int courtCount) {
        String roundFormat = "%0" + String.valueOf(rounds.size()).length() + "d";
        String courtFormat = "%0" + String.valueOf(courtCount).length() + "d";

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < rounds.size(); i++) {
            sb.append(String.format("Round #" + roundFormat + ": ", i + 1));

            for (int j = 0; j < rounds.get(i).getMatchList().size(); j++) {
                sb.append(String.format("Court #" + courtFormat + ": ", j + 1));
                sb.append(rounds.get(i).getMatchList().get(j));

                if (j < rounds.get(i).getMatchList().size() - 1) {
                    sb.append(" | ");
                }
            }

            sb.append("\n");
        }

        System.out.println(sb.toString());
    }

    public static void exportPlanningToCsv(List<MatchSet> rounds, int courtCount) {
        char separator = ';';

        StringBuilder sb = new StringBuilder();

        // Header
        sb.append("Round").append(separator);

        for (int i = 0; i < courtCount; i++) {
            sb.append("Court ").append(i + 1);

            if (i < courtCount - 1) {
                sb.append(separator);
            }
        }

        sb.append("\n");

        // Rounds
        for (int i = 0; i < rounds.size(); i++) {
            sb.append(i + 1).append(separator);

            MatchSet round = rounds.get(i);

            for (AbstractMatch match : round.getMatchList()) {
                sb.append(match).append(separator);
            }

            if (round.getMatchList().size() < courtCount) {
                for (int j = 0; j < courtCount - round.getMatchList().size(); j++) {
                    sb.append(separator);
                }
            }

            sb.append("\n");
        }


        String fileName = String.format(
            "%04d-%02d-%02d-%02d-%02d-%02d - Planning.csv",
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH) + 1,
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
            Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
            Calendar.getInstance().get(Calendar.MINUTE),
            Calendar.getInstance().get(Calendar.SECOND)
        );

        try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8)) {
            osw.write(sb.toString());
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace(System.err);
        }
    }

    private static List<MatchSet> planSingleMatchList(int courtCount, List<AbstractMatch> matchList) throws IllegalTeamException {
        List<MatchSet> rounds = new ArrayList<>();
        MatchSet round = new MatchSet();

        List<String> playersLists = getPlayersFromSinglePlayerMatchList(matchList);
        List<String> availablePlayers = new ArrayList<>(playersLists.size());
        availablePlayers.addAll(playersLists);

        while (matchList.size() > 0) {
            for (AbstractMatch match : matchList) {
                if (round.getMatchList() != null && round.getMatchList().size() == courtCount) {
                    break;
                }

                if (availablePlayers.size() == 0) {
                    break;
                }

                AbstractTeam aTeam = match.getTeamA();
                AbstractTeam bTeam = match.getTeamB();

                String firstPlayer = ((SinglePlayerTeam) aTeam).getPlayer();
                String secondPlayer = ((SinglePlayerTeam) bTeam).getPlayer();

                if (availablePlayers.contains(firstPlayer) && availablePlayers.contains(secondPlayer)) {
                    round.addMatch(match);

                    availablePlayers.remove(firstPlayer);
                    availablePlayers.remove(secondPlayer);
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
                availablePlayers.clear();
                availablePlayers.addAll(playersLists);
            }
        }

        return sortRounds(rounds, courtCount);
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

        return sortRounds(rounds, courtCount);
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

    private static List<MatchSet> sortRounds(List<MatchSet> rounds, int courtCount) {
        List<MatchSet> result = new ArrayList<>();

        for (int i = courtCount; i > 0; i--) {
            final int size = i;
            rounds.stream().filter(item -> item.getMatchList().size() == size).forEach(result::add);
        }

        return result;
    }
}
